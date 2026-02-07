package com.knightscrusade.server.net

import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages all active player sessions on the server.
 *
 * Provides thread-safe access to player sessions by UUID, and handles
 * session creation and removal as players connect and disconnect.
 */
class PlayerSessionManager {

    private val logger = LoggerFactory.getLogger(PlayerSessionManager::class.java)

    private val sessions = ConcurrentHashMap<UUID, PlayerSession>()

    /**
     * Register a new player session.
     */
    fun addSession(session: PlayerSession) {
        sessions[session.playerId] = session
        logger.info("Player connected: ${session.playerName} (${session.playerId})")
    }

    /**
     * Remove a player session by UUID.
     */
    fun removeSession(playerId: UUID): PlayerSession? {
        val session = sessions.remove(playerId)
        if (session != null) {
            logger.info("Player disconnected: ${session.playerName} (${session.playerId})")
        }
        return session
    }

    /**
     * Get a session by player UUID.
     */
    fun getSession(playerId: UUID): PlayerSession? = sessions[playerId]

    /**
     * Get all active sessions.
     */
    fun getAllSessions(): Collection<PlayerSession> = sessions.values

    /**
     * Get the number of connected players.
     */
    fun playerCount(): Int = sessions.size
}
