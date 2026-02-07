package com.knightscrusade.server.persistence

import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

/**
 * SQLite database for player profile persistence.
 *
 * Stores player UUID, name, and spawn position. Used to restore
 * player state across server restarts.
 *
 * @param worldDir Directory for world data (database stored as worldDir/players.db)
 */
class PlayerDatabase(worldDir: Path) {

    private val logger = LoggerFactory.getLogger(PlayerDatabase::class.java)
    private val dbPath = worldDir.resolve("players.db")
    private var connection: Connection? = null

    /**
     * Player profile data.
     */
    data class PlayerProfile(
        val uuid: UUID,
        val name: String,
        val spawnX: Double,
        val spawnY: Double,
        val spawnZ: Double
    )

    /**
     * Open the database and create tables if needed.
     */
    fun open() {
        Files.createDirectories(dbPath.parent)
        connection = DriverManager.getConnection("jdbc:sqlite:${dbPath.toAbsolutePath()}")
        createTables()
        logger.info("Player database opened at $dbPath")
    }

    /**
     * Close the database connection.
     */
    fun close() {
        connection?.close()
        connection = null
        logger.info("Player database closed")
    }

    /**
     * Save or update a player profile.
     */
    fun savePlayer(profile: PlayerProfile) {
        val conn = connection ?: return
        val sql = """
            INSERT OR REPLACE INTO player_profiles (uuid, name, spawn_x, spawn_y, spawn_z)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, profile.uuid.toString())
            stmt.setString(2, profile.name)
            stmt.setDouble(3, profile.spawnX)
            stmt.setDouble(4, profile.spawnY)
            stmt.setDouble(5, profile.spawnZ)
            stmt.executeUpdate()
        }
    }

    /**
     * Load a player profile by UUID.
     * @return The player profile, or null if not found
     */
    fun loadPlayer(uuid: UUID): PlayerProfile? {
        val conn = connection ?: return null
        val sql = "SELECT uuid, name, spawn_x, spawn_y, spawn_z FROM player_profiles WHERE uuid = ?"
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, uuid.toString())
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return PlayerProfile(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    name = rs.getString("name"),
                    spawnX = rs.getDouble("spawn_x"),
                    spawnY = rs.getDouble("spawn_y"),
                    spawnZ = rs.getDouble("spawn_z")
                )
            }
        }
        return null
    }

    /**
     * Load a player profile by name.
     */
    fun loadPlayerByName(name: String): PlayerProfile? {
        val conn = connection ?: return null
        val sql = "SELECT uuid, name, spawn_x, spawn_y, spawn_z FROM player_profiles WHERE name = ?"
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, name)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return PlayerProfile(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    name = rs.getString("name"),
                    spawnX = rs.getDouble("spawn_x"),
                    spawnY = rs.getDouble("spawn_y"),
                    spawnZ = rs.getDouble("spawn_z")
                )
            }
        }
        return null
    }

    /**
     * Get the total number of stored player profiles.
     */
    fun playerCount(): Int {
        val conn = connection ?: return 0
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT COUNT(*) FROM player_profiles")
            if (rs.next()) return rs.getInt(1)
        }
        return 0
    }

    private fun createTables() {
        val conn = connection ?: return
        conn.createStatement().use { stmt ->
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_profiles (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    spawn_x REAL NOT NULL DEFAULT 0,
                    spawn_y REAL NOT NULL DEFAULT 65,
                    spawn_z REAL NOT NULL DEFAULT 0
                )
            """.trimIndent())
        }
    }
}
