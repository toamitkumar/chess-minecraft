package com.knightscrusade.common.net

import com.google.protobuf.MessageLite
import com.knightscrusade.common.proto.Packets
import org.slf4j.LoggerFactory

/**
 * Registry mapping packet type IDs to protobuf message classes.
 *
 * Every packet sent over the network is prefixed with a type ID so the receiver
 * knows which protobuf message to deserialize. This registry maintains the
 * bidirectional mapping between IDs and message types.
 *
 * The same registry is used on both client and server, ensuring consistent
 * packet identification across the network boundary.
 */
object PacketRegistry {

    private val logger = LoggerFactory.getLogger(PacketRegistry::class.java)

    /** Map from packet ID to a parser that creates the protobuf message from bytes */
    private val parsers = mutableMapOf<Int, (ByteArray) -> MessageLite>()

    /** Map from message class to packet ID */
    private val idsByClass = mutableMapOf<Class<out MessageLite>, Int>()

    init {
        register(1, Packets.LoginRequest::class.java) { Packets.LoginRequest.parseFrom(it) }
        register(2, Packets.LoginResponse::class.java) { Packets.LoginResponse.parseFrom(it) }
        register(3, Packets.MovePacket::class.java) { Packets.MovePacket.parseFrom(it) }
        register(4, Packets.ChunkDataPacket::class.java) { Packets.ChunkDataPacket.parseFrom(it) }
        register(5, Packets.BlockChangePacket::class.java) { Packets.BlockChangePacket.parseFrom(it) }
        register(6, Packets.EntitySpawnPacket::class.java) { Packets.EntitySpawnPacket.parseFrom(it) }
        register(7, Packets.EntityMovePacket::class.java) { Packets.EntityMovePacket.parseFrom(it) }
        register(8, Packets.EntityRemovePacket::class.java) { Packets.EntityRemovePacket.parseFrom(it) }

        logger.debug("PacketRegistry initialized with ${parsers.size} packet types")
    }

    /**
     * Register a packet type with a unique ID and parser function.
     *
     * @param id Unique integer identifier for this packet type
     * @param clazz The protobuf message class
     * @param parser Function to deserialize bytes into the message
     */
    private fun register(id: Int, clazz: Class<out MessageLite>, parser: (ByteArray) -> MessageLite) {
        require(id !in parsers) { "Duplicate packet ID: $id" }
        parsers[id] = parser
        idsByClass[clazz] = id
    }

    /**
     * Get the packet ID for a given message instance.
     *
     * @throws IllegalArgumentException if the message type is not registered
     */
    fun getPacketId(message: MessageLite): Int {
        return idsByClass[message.javaClass]
            ?: throw IllegalArgumentException("Unregistered packet type: ${message.javaClass.simpleName}")
    }

    /**
     * Parse a packet from its ID and raw bytes.
     *
     * @throws IllegalArgumentException if the packet ID is not registered
     */
    fun parsePacket(id: Int, data: ByteArray): MessageLite {
        val parser = parsers[id]
            ?: throw IllegalArgumentException("Unknown packet ID: $id")
        return parser(data)
    }

    /**
     * Check if a packet ID is registered.
     */
    fun isRegistered(id: Int): Boolean = id in parsers

    /**
     * Get the total number of registered packet types.
     */
    fun registeredCount(): Int = parsers.size
}
