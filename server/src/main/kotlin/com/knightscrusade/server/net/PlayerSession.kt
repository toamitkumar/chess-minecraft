package com.knightscrusade.server.net

import com.google.protobuf.MessageLite
import com.knightscrusade.common.chunk.ChunkPos
import io.netty.channel.Channel
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Represents a connected player on the server.
 *
 * The PlayerSession is the server's abstraction for a player connection,
 * agnostic to whether the player is local (single-player) or remote (multiplayer).
 * All server code references players through this class, never through
 * direct channel references.
 *
 * @param playerId Unique identifier for this player
 * @param playerName Display name of the player
 * @param channel Netty channel for sending packets to this player
 */
class PlayerSession(
    val playerId: UUID,
    val playerName: String,
    val channel: Channel
) {
    /** Entity UUID in the ECS world (set on spawn) */
    var entityId: UUID = UUID.randomUUID()

    /** Current position in world space */
    var x: Double = 0.0
    var y: Double = 65.0
    var z: Double = 0.0

    /** Current look direction */
    var yaw: Float = 0f
    var pitch: Float = 0f

    /** Whether the player is standing on solid ground */
    var onGround: Boolean = false

    /** Input state from the most recent MovePacket */
    var forward: Boolean = false
    var backward: Boolean = false
    var left: Boolean = false
    var right: Boolean = false
    var jump: Boolean = false
    var sprint: Boolean = false
    var lLeap: Boolean = false

    /** Sequence number of the latest processed input */
    var lastSequence: Long = 0

    /** Set of chunk positions currently loaded on this player's client */
    val loadedChunks = ConcurrentHashMap.newKeySet<ChunkPos>()

    /** Render distance in chunks */
    val renderDistance: Int = 8

    /**
     * Send a packet to this player's client.
     *
     * @param message The protobuf message to send
     */
    fun sendPacket(message: MessageLite) {
        if (channel.isActive) {
            channel.writeAndFlush(message)
        }
    }

    override fun toString(): String = "PlayerSession($playerName, $playerId)"
}
