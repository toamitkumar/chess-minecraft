package com.knightscrusade.server.net

import com.google.protobuf.ByteString
import com.google.protobuf.MessageLite
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.Entity
import com.knightscrusade.common.ecs.components.*
import com.knightscrusade.common.proto.Packets
import com.knightscrusade.server.world.ServerWorld
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Handles incoming packets from clients on the server side.
 *
 * Processes login requests, movement packets, and block changes.
 * Creates ECS entities for players on login and routes input to
 * [InputComponent] for processing by server systems.
 */
@ChannelHandler.Sharable
class ServerNetworkHandler(
    private val world: ServerWorld,
    private val ecsWorld: EcsWorld,
    private val sessionManager: PlayerSessionManager
) : SimpleChannelInboundHandler<MessageLite>() {

    private val logger = LoggerFactory.getLogger(ServerNetworkHandler::class.java)

    override fun channelRead0(ctx: ChannelHandlerContext, msg: MessageLite) {
        when (msg) {
            is Packets.LoginRequest -> handleLogin(ctx, msg)
            is Packets.MovePacket -> handleMove(msg)
            is Packets.BlockChangePacket -> handleBlockChange(msg)
            else -> logger.warn("Unknown packet type: ${msg.javaClass.simpleName}")
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val session = sessionManager.getAllSessions().find { it.channel == ctx.channel() }
        if (session != null) {
            // Despawn the entity
            ecsWorld.despawn(session.entityId)
            sessionManager.removeSession(session.playerId)

            // Notify other players
            val removePacket = Packets.EntityRemovePacket.newBuilder()
                .setEntityId(session.entityId.toString())
                .build()
            for (s in sessionManager.getAllSessions()) {
                s.sendPacket(removePacket)
            }

            logger.info("Player ${session.playerName} disconnected")
        }
        super.channelInactive(ctx)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("Server network error", cause)
        ctx.close()
    }

    /**
     * Handle player login: create session, spawn ECS entity, stream initial chunks.
     */
    private fun handleLogin(ctx: ChannelHandlerContext, packet: Packets.LoginRequest) {
        val playerId = UUID.randomUUID()
        val session = PlayerSession(playerId, packet.playerName, ctx.channel())

        // Get spawn position
        val (spawnX, spawnY, spawnZ) = world.getSpawnPosition()
        session.x = spawnX
        session.y = spawnY
        session.z = spawnZ

        // Create Knight entity with all required components
        val entity = Entity(type = "knight")
            .add(PositionComponent(spawnX, spawnY, spawnZ))
            .add(VelocityComponent())
            .add(CollisionComponent(width = 0.6f, height = 1.8f))
            .add(InputComponent())
            .add(PlayerComponent(playerId, packet.playerName))
            .add(LeapComponent())

        ecsWorld.spawn(entity)
        session.entityId = entity.id

        sessionManager.addSession(session)

        // Send login response
        val response = Packets.LoginResponse.newBuilder()
            .setPlayerId(playerId.toString())
            .setSpawnX(spawnX)
            .setSpawnY(spawnY)
            .setSpawnZ(spawnZ)
            .build()
        session.sendPacket(response)

        // Send entity spawn to the player (and others)
        val spawnPacket = Packets.EntitySpawnPacket.newBuilder()
            .setEntityId(entity.id.toString())
            .setEntityType("knight")
            .setX(spawnX)
            .setY(spawnY)
            .setZ(spawnZ)
            .setYaw(0f)
            .setPitch(0f)
            .build()
        for (s in sessionManager.getAllSessions()) {
            s.sendPacket(spawnPacket)
        }

        // Stream initial chunks around spawn
        streamChunksToPlayer(session)

        logger.info("Player ${packet.playerName} logged in at ($spawnX, $spawnY, $spawnZ) entity=${entity.id}")
    }

    /**
     * Handle player movement: route input to ECS InputComponent.
     * Server-side physics will compute the actual position.
     */
    private fun handleMove(packet: Packets.MovePacket) {
        val playerId = try { UUID.fromString(packet.playerId) } catch (_: Exception) { return }
        val session = sessionManager.getSession(playerId) ?: return

        // Update session input state
        session.forward = packet.forward
        session.backward = packet.backward
        session.left = packet.left
        session.right = packet.right
        session.jump = packet.jump
        session.sprint = packet.sprint
        session.lLeap = packet.lLeap
        session.lastSequence = packet.sequence

        // Route input to the ECS entity's InputComponent
        val entity = ecsWorld.getEntity(session.entityId) ?: return
        val input = entity.get(InputComponent::class.java) ?: return
        input.forward = packet.forward
        input.backward = packet.backward
        input.left = packet.left
        input.right = packet.right
        input.jump = packet.jump
        input.sprint = packet.sprint
        input.lLeap = packet.lLeap
        input.yaw = packet.yaw
        input.pitch = packet.pitch
        input.lastSequence = packet.sequence

        // Update session position from entity (will be set by ECS systems)
        val pos = entity.get(PositionComponent::class.java)
        if (pos != null) {
            session.x = pos.x
            session.y = pos.y
            session.z = pos.z
            session.yaw = pos.yaw
            session.pitch = pos.pitch
        }

        // Check if player moved to a new chunk region — stream new chunks
        streamChunksToPlayer(session)
    }

    /**
     * Handle block change: validate and apply, then broadcast to all players.
     */
    private fun handleBlockChange(packet: Packets.BlockChangePacket) {
        val blockType = com.knightscrusade.common.block.BlockType.fromId(packet.blockId)

        val success = world.setBlock(packet.x, packet.y, packet.z, blockType)
        if (!success) {
            logger.debug("Block change rejected at (${packet.x}, ${packet.y}, ${packet.z})")
            return
        }

        val broadcastPacket = Packets.BlockChangePacket.newBuilder()
            .setX(packet.x)
            .setY(packet.y)
            .setZ(packet.z)
            .setBlockId(packet.blockId)
            .build()

        for (session in sessionManager.getAllSessions()) {
            session.sendPacket(broadcastPacket)
        }
    }

    /**
     * Send chunks within render distance that the player hasn't received yet.
     */
    fun streamChunksToPlayer(session: PlayerSession) {
        val playerChunkX = (session.x / 16).toInt()
        val playerChunkZ = (session.z / 16).toInt()
        val rd = session.renderDistance

        for (cx in (playerChunkX - rd)..(playerChunkX + rd)) {
            for (cz in (playerChunkZ - rd)..(playerChunkZ + rd)) {
                val pos = ChunkPos(cx, cz)
                if (pos !in session.loadedChunks) {
                    val chunk = world.getChunk(pos)
                    sendChunkData(session, chunk)
                    session.loadedChunks.add(pos)
                }
            }
        }
    }

    /**
     * Serialize and send a chunk column to a player.
     */
    private fun sendChunkData(session: PlayerSession, chunk: ChunkColumn) {
        val buffer = ByteBuffer.allocate(ChunkColumn.NUM_SUB_CHUNKS * 16 * 16 * 16 * 2)
        for (subIndex in 0 until ChunkColumn.NUM_SUB_CHUNKS) {
            val rawData = chunk.getSubChunk(subIndex).getRawData()
            for (s in rawData) {
                buffer.putShort(s)
            }
        }
        buffer.flip()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val packet = Packets.ChunkDataPacket.newBuilder()
            .setChunkX(chunk.pos.x)
            .setChunkZ(chunk.pos.z)
            .setBlockData(ByteString.copyFrom(bytes))
            .build()

        session.sendPacket(packet)
    }
}
