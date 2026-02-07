package com.knightscrusade.server.persistence

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * Tests for [RegionFile] — chunk serialization and persistence.
 */
class RegionFileTest {

    @TempDir
    lateinit var tempDir: Path

    private fun createTestChunk(pos: ChunkPos): ChunkColumn {
        val chunk = ChunkColumn(pos)
        // Fill with a recognizable pattern
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                chunk.setBlock(x, 0, z, BlockType.BEDROCK)
                for (y in 1..58) {
                    chunk.setBlock(x, y, z, BlockType.STONE)
                }
                for (y in 59..62) {
                    chunk.setBlock(x, y, z, BlockType.DIRT)
                }
                chunk.setBlock(x, 63, z, BlockType.GRASS)
            }
        }
        // Add a unique marker
        chunk.setBlock(0, 64, 0, BlockType.GOLD_BLOCK)
        return chunk
    }

    @Test
    fun `save and load chunk round-trips correctly`() {
        val pos = ChunkPos(0, 0)
        val regionFile = RegionFile(tempDir, 0, 0)

        val original = createTestChunk(pos)
        regionFile.saveChunk(original)

        val loaded = regionFile.loadChunk(pos)
        assertNotNull(loaded)

        // Verify blocks match
        assertEquals(BlockType.BEDROCK, loaded!!.getBlock(0, 0, 0))
        assertEquals(BlockType.STONE, loaded.getBlock(8, 30, 8))
        assertEquals(BlockType.GRASS, loaded.getBlock(8, 63, 8))
        assertEquals(BlockType.GOLD_BLOCK, loaded.getBlock(0, 64, 0))
        assertEquals(BlockType.AIR, loaded.getBlock(8, 100, 8))
    }

    @Test
    fun `load returns null for unsaved chunk`() {
        val regionFile = RegionFile(tempDir, 0, 0)
        assertNull(regionFile.loadChunk(ChunkPos(5, 5)))
    }

    @Test
    fun `hasChunk returns false for unsaved chunk`() {
        val regionFile = RegionFile(tempDir, 0, 0)
        assertFalse(regionFile.hasChunk(ChunkPos(0, 0)))
    }

    @Test
    fun `hasChunk returns true after saving`() {
        val regionFile = RegionFile(tempDir, 0, 0)
        regionFile.saveChunk(createTestChunk(ChunkPos(3, 3)))
        assertTrue(regionFile.hasChunk(ChunkPos(3, 3)))
    }

    @Test
    fun `multiple chunks in same region`() {
        val regionFile = RegionFile(tempDir, 0, 0)

        val chunk1 = createTestChunk(ChunkPos(0, 0))
        val chunk2 = createTestChunk(ChunkPos(1, 1))
        chunk2.setBlock(0, 64, 0, BlockType.OBSIDIAN) // Different marker

        regionFile.saveChunk(chunk1)
        regionFile.saveChunk(chunk2)

        val loaded1 = regionFile.loadChunk(ChunkPos(0, 0))
        val loaded2 = regionFile.loadChunk(ChunkPos(1, 1))

        assertNotNull(loaded1)
        assertNotNull(loaded2)
        assertEquals(BlockType.GOLD_BLOCK, loaded1!!.getBlock(0, 64, 0))
        assertEquals(BlockType.OBSIDIAN, loaded2!!.getBlock(0, 64, 0))
    }

    @Test
    fun `region coordinates calculated correctly`() {
        assertEquals(Pair(0, 0), RegionFile.regionCoords(ChunkPos(0, 0)))
        assertEquals(Pair(0, 0), RegionFile.regionCoords(ChunkPos(31, 31)))
        assertEquals(Pair(1, 0), RegionFile.regionCoords(ChunkPos(32, 0)))
        assertEquals(Pair(-1, -1), RegionFile.regionCoords(ChunkPos(-1, -1)))
        assertEquals(Pair(-1, 0), RegionFile.regionCoords(ChunkPos(-32, 0)))
    }

    @Test
    fun `save and load with negative chunk coordinates`() {
        val pos = ChunkPos(-5, -10)
        val (rx, rz) = RegionFile.regionCoords(pos)
        val regionFile = RegionFile(tempDir, rx, rz)

        val original = createTestChunk(pos)
        regionFile.saveChunk(original)

        val loaded = regionFile.loadChunk(pos)
        assertNotNull(loaded)
        assertEquals(BlockType.GOLD_BLOCK, loaded!!.getBlock(0, 64, 0))
    }

    @Test
    fun `overwrite existing chunk`() {
        val pos = ChunkPos(0, 0)
        val regionFile = RegionFile(tempDir, 0, 0)

        // Save once
        val original = createTestChunk(pos)
        regionFile.saveChunk(original)

        // Modify and save again
        original.setBlock(5, 64, 5, BlockType.QUARTZ)
        regionFile.saveChunk(original)

        // Load should have the updated data
        val loaded = regionFile.loadChunk(pos)
        assertNotNull(loaded)
        assertEquals(BlockType.QUARTZ, loaded!!.getBlock(5, 64, 5))
    }
}
