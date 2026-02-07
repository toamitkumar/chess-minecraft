package com.knightscrusade.client.net

import com.google.protobuf.MessageLite
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import com.knightscrusade.common.chunk.SubChunk
import com.knightscrusade.common.net.PacketDecoder
import com.knightscrusade.common.net.PacketEncoder
import com.knightscrusade.common.proto.Packets
import com.knightscrusade.client.render.chunk.ClientChunkManager
import com.knightscrusade.client.render.entity.EntityRenderer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalChannel
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

/**
 * Client-side network handler that connects to the server via Netty.
 *
 * In single-player, connects to the IntegratedServer via a LocalChannel
 * (in-memory, zero-overhead). Handles login, chunk data reception,
 * block changes, and entity updates. Routes entity state to the
 * EntityRenderer for visual representation.
 */
class ClientNetworkHandler(
    private val chunkManager: ClientChunkManager,
    private val entityRenderer: EntityRenderer
) {

    private val logger = LoggerFactory.getLogger(ClientNetworkHandler::class.java)

    private var channel: Channel? = null
    private var eventLoopGroup: DefaultEventLoopGroup? = null

    /** Player ID assigned by the server after login */
    @Volatile
    var playerId: String? = null
        private set

    /** Spawn position received from server */
    @Volatile
    var spawnX: Double = 0.0; private set

    @Volatile
    var spawnY: Double = 65.0; private set

    @Volatile
    var spawnZ: Double = 0.0; private set

    /** Server-authoritative player position (updated from EntityMovePacket) */
    @Volatile
    var serverX: Double = 0.0; private set
    @Volatile
    var serverY: Double = 65.0; private set
    @Volatile
    var serverZ: Double = 0.0; private set
    @Volatile
    var serverOnGround: Boolean = false; private set

    /** Whether the client is connected and logged in */
    @Volatile
    var connected: Boolean = false
        private set

    /** Packet counters for debug overlay */
    @Volatile
    var packetsSent: Long = 0; private set

    @Volatile
    var packetsReceived: Long = 0; private set

    /** Entity ID for the local player's entity */
    @Volatile
    var localEntityId: String? = null
        private set

    /**
     * Connect to the server at the given local address.
     */
    fun connect(address: LocalAddress) {
        eventLoopGroup = DefaultEventLoopGroup(1)

        val bootstrap = Bootstrap()
            .group(eventLoopGroup)
            .channel(LocalChannel::class.java)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.pipeline().addLast(
                        PacketDecoder(),
                        PacketEncoder(),
                        ClientPacketHandler()
                    )
                }
            })

        channel = bootstrap.connect(address).sync().channel()
        logger.info("Connected to server at $address")
    }

    /**
     * Send a login request to the server.
     */
    fun login(playerName: String) {
        val packet = Packets.LoginRequest.newBuilder()
            .setPlayerName(playerName)
            .build()
        sendPacket(packet)
    }

    /**
     * Send any protobuf message to the server.
     */
    fun sendPacket(message: MessageLite) {
        channel?.let {
            if (it.isActive) {
                it.writeAndFlush(message)
                packetsSent++
            }
        }
    }

    /**
     * Disconnect from the server.
     */
    fun disconnect() {
        channel?.close()?.sync()
        eventLoopGroup?.shutdownGracefully()
        connected = false
        logger.info("Disconnected from server")
    }

    /**
     * Inner handler that processes incoming server packets.
     */
    private inner class ClientPacketHandler : SimpleChannelInboundHandler<MessageLite>() {

        override fun channelRead0(ctx: ChannelHandlerContext, msg: MessageLite) {
            packetsReceived++

            when (msg) {
                is Packets.LoginResponse -> handleLoginResponse(msg)
                is Packets.ChunkDataPacket -> handleChunkData(msg)
                is Packets.BlockChangePacket -> handleBlockChange(msg)
                is Packets.EntitySpawnPacket -> handleEntitySpawn(msg)
                is Packets.EntityMovePacket -> handleEntityMove(msg)
                is Packets.EntityRemovePacket -> handleEntityRemove(msg)
                else -> logger.warn("Unknown server packet: ${msg.javaClass.simpleName}")
            }
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            logger.error("Client network error", cause)
            ctx.close()
        }
    }

    private fun handleLoginResponse(packet: Packets.LoginResponse) {
        playerId = packet.playerId
        spawnX = packet.spawnX
        spawnY = packet.spawnY
        spawnZ = packet.spawnZ
        serverX = spawnX
        serverY = spawnY
        serverZ = spawnZ
        connected = true
        logger.info("Login successful! Player ID: $playerId, spawn: ($spawnX, $spawnY, $spawnZ)")
    }

    /**
     * Deserialize chunk data from the server and store it in the client chunk manager.
     */
    private fun handleChunkData(packet: Packets.ChunkDataPacket) {
        val pos = ChunkPos(packet.chunkX, packet.chunkZ)
        val chunk = ChunkColumn(pos)
        val bytes = packet.blockData.toByteArray()
        val buffer = ByteBuffer.wrap(bytes)

        for (subIndex in 0 until ChunkColumn.NUM_SUB_CHUNKS) {
            val rawData = ShortArray(SubChunk.SIZE * SubChunk.SIZE * SubChunk.SIZE)
            for (i in rawData.indices) {
                if (buffer.hasRemaining()) {
                    rawData[i] = buffer.short
                }
            }
            chunk.getSubChunk(subIndex).setRawData(rawData)
        }

        chunkManager.setChunk(pos, chunk)
    }

    private fun handleBlockChange(packet: Packets.BlockChangePacket) {
        val chunkPos = ChunkPos.fromWorldPos(packet.x, packet.z)
        val chunk = chunkManager.getChunk(chunkPos) ?: return
        val localX = ((packet.x % 16) + 16) % 16
        val localZ = ((packet.z % 16) + 16) % 16
        chunk.setBlock(localX, packet.y, localZ, com.knightscrusade.common.block.BlockType.fromId(packet.blockId))
    }

    private fun handleEntitySpawn(packet: Packets.EntitySpawnPacket) {
        entityRenderer.spawnEntity(
            packet.entityId, packet.entityType,
            packet.x, packet.y, packet.z,
            packet.yaw, packet.pitch
        )
        // Track the local player's entity
        if (localEntityId == null && playerId != null) {
            localEntityId = packet.entityId
            entityRenderer.localEntityId = packet.entityId
        }
        logger.debug("Entity spawned: ${packet.entityType} id=${packet.entityId}")
    }

    private fun handleEntityMove(packet: Packets.EntityMovePacket) {
        entityRenderer.updateEntity(
            packet.entityId,
            packet.x, packet.y, packet.z,
            packet.yaw, packet.pitch
        )

        // Track server-authoritative position for local player
        if (packet.entityId == localEntityId) {
            serverX = packet.x
            serverY = packet.y
            serverZ = packet.z
            serverOnGround = packet.onGround
        }
    }

    private fun handleEntityRemove(packet: Packets.EntityRemovePacket) {
        entityRenderer.removeEntity(packet.entityId)
        logger.debug("Entity removed: ${packet.entityId}")
    }
}
