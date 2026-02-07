package com.knightscrusade.server.persistence

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.UUID

/**
 * Tests for [PlayerDatabase] — SQLite player profile persistence.
 */
class PlayerDatabaseTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var db: PlayerDatabase

    @BeforeEach
    fun setup() {
        db = PlayerDatabase(tempDir)
        db.open()
    }

    @AfterEach
    fun teardown() {
        db.close()
    }

    @Test
    fun `save and load player profile`() {
        val uuid = UUID.randomUUID()
        val profile = PlayerDatabase.PlayerProfile(uuid, "Knight", 10.5, 65.0, 20.5)
        db.savePlayer(profile)

        val loaded = db.loadPlayer(uuid)
        assertNotNull(loaded)
        assertEquals(uuid, loaded!!.uuid)
        assertEquals("Knight", loaded.name)
        assertEquals(10.5, loaded.spawnX, 0.001)
        assertEquals(65.0, loaded.spawnY, 0.001)
        assertEquals(20.5, loaded.spawnZ, 0.001)
    }

    @Test
    fun `load returns null for unknown UUID`() {
        assertNull(db.loadPlayer(UUID.randomUUID()))
    }

    @Test
    fun `load by name`() {
        val uuid = UUID.randomUUID()
        db.savePlayer(PlayerDatabase.PlayerProfile(uuid, "TestKnight", 0.0, 65.0, 0.0))

        val loaded = db.loadPlayerByName("TestKnight")
        assertNotNull(loaded)
        assertEquals(uuid, loaded!!.uuid)
    }

    @Test
    fun `load by name returns null for unknown name`() {
        assertNull(db.loadPlayerByName("NonExistentPlayer"))
    }

    @Test
    fun `player count`() {
        assertEquals(0, db.playerCount())

        db.savePlayer(PlayerDatabase.PlayerProfile(UUID.randomUUID(), "Player1", 0.0, 0.0, 0.0))
        assertEquals(1, db.playerCount())

        db.savePlayer(PlayerDatabase.PlayerProfile(UUID.randomUUID(), "Player2", 0.0, 0.0, 0.0))
        assertEquals(2, db.playerCount())
    }

    @Test
    fun `upsert overwrites existing player`() {
        val uuid = UUID.randomUUID()
        db.savePlayer(PlayerDatabase.PlayerProfile(uuid, "OldName", 0.0, 0.0, 0.0))
        db.savePlayer(PlayerDatabase.PlayerProfile(uuid, "NewName", 100.0, 200.0, 300.0))

        assertEquals(1, db.playerCount()) // Should not create a duplicate
        val loaded = db.loadPlayer(uuid)!!
        assertEquals("NewName", loaded.name)
        assertEquals(100.0, loaded.spawnX, 0.001)
    }

    @Test
    fun `database reopens correctly`() {
        val uuid = UUID.randomUUID()
        db.savePlayer(PlayerDatabase.PlayerProfile(uuid, "Persistent", 1.0, 2.0, 3.0))
        db.close()

        // Reopen
        val db2 = PlayerDatabase(tempDir)
        db2.open()
        val loaded = db2.loadPlayer(uuid)
        db2.close()

        assertNotNull(loaded)
        assertEquals("Persistent", loaded!!.name)
    }
}
