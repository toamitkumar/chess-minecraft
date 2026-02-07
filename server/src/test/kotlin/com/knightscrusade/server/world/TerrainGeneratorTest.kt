package com.knightscrusade.server.world

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkPos
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [TerrainGenerator] — terrain generation with both flat and noise modes.
 */
class TerrainGeneratorTest {

    private val flatGenerator = TerrainGenerator(useNoise = false)
    private val noiseGenerator = TerrainGenerator(seed = 42, useNoise = true)

    // === Flat terrain tests (preserved from Phase 3) ===

    @Test
    fun `flat - generated chunk has bedrock at Y=0`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(0, 0))
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                assertEquals(BlockType.BEDROCK, chunk.getBlock(x, 0, z),
                    "Bedrock expected at ($x, 0, $z)")
            }
        }
    }

    @Test
    fun `flat - generated chunk has stone from Y=1 to Y=58`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(1, 1))
        assertEquals(BlockType.STONE, chunk.getBlock(8, 1, 8))
        assertEquals(BlockType.STONE, chunk.getBlock(8, 30, 8))
        assertEquals(BlockType.STONE, chunk.getBlock(8, 58, 8))
    }

    @Test
    fun `flat - generated chunk has dirt from Y=59 to Y=62`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(1, 1))
        assertEquals(BlockType.DIRT, chunk.getBlock(8, 59, 8))
        assertEquals(BlockType.DIRT, chunk.getBlock(8, 62, 8))
    }

    @Test
    fun `flat - generated chunk has grass at Y=63`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(1, 1))
        assertEquals(BlockType.GRASS, chunk.getBlock(8, 63, 8))
    }

    @Test
    fun `flat - generated chunk has air above Y=63 for normal chunks`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(1, 1))
        assertEquals(BlockType.AIR, chunk.getBlock(8, 64, 8))
        assertEquals(BlockType.AIR, chunk.getBlock(8, 100, 8))
    }

    @Test
    fun `flat - center chunk has obsidian tower`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(0, 0))
        assertEquals(BlockType.OBSIDIAN, chunk.getBlock(6, 64, 6))
        assertEquals(BlockType.OBSIDIAN, chunk.getBlock(9, 70, 9))
        assertEquals(BlockType.AIR, chunk.getBlock(7, 65, 7))
    }

    @Test
    fun `flat - center chunk has gold crown on top of tower`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(0, 0))
        assertEquals(BlockType.GOLD_BLOCK, chunk.getBlock(7, 81, 7))
    }

    @Test
    fun `chunk is clean after generation`() {
        val chunk = flatGenerator.generateChunk(ChunkPos(5, 5))
        assertFalse(chunk.dirty)
    }

    @Test
    fun `flat - different chunk positions generate same terrain layers`() {
        val chunk1 = flatGenerator.generateChunk(ChunkPos(10, 10))
        val chunk2 = flatGenerator.generateChunk(ChunkPos(-5, -5))
        assertEquals(BlockType.BEDROCK, chunk1.getBlock(0, 0, 0))
        assertEquals(BlockType.BEDROCK, chunk2.getBlock(0, 0, 0))
        assertEquals(BlockType.GRASS, chunk1.getBlock(0, 63, 0))
        assertEquals(BlockType.GRASS, chunk2.getBlock(0, 63, 0))
    }

    // === Noise terrain tests ===

    @Test
    fun `noise - always has bedrock at Y=0`() {
        val chunk = noiseGenerator.generateChunk(ChunkPos(5, 5))
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                assertEquals(BlockType.BEDROCK, chunk.getBlock(x, 0, z),
                    "Bedrock expected at ($x, 0, $z)")
            }
        }
    }

    @Test
    fun `noise - terrain height is within valid range`() {
        // Check multiple positions
        for (wx in -100..100 step 17) {
            for (wz in -100..100 step 17) {
                val height = noiseGenerator.getHeight(wx, wz)
                assertTrue(height >= TerrainGenerator.MIN_HEIGHT,
                    "Height $height at ($wx, $wz) should be >= ${TerrainGenerator.MIN_HEIGHT}")
                assertTrue(height <= TerrainGenerator.MAX_HEIGHT,
                    "Height $height at ($wx, $wz) should be <= ${TerrainGenerator.MAX_HEIGHT}")
            }
        }
    }

    @Test
    fun `noise - deterministic with same seed`() {
        val gen1 = TerrainGenerator(seed = 123, useNoise = true)
        val gen2 = TerrainGenerator(seed = 123, useNoise = true)

        for (wx in 0..50 step 7) {
            for (wz in 0..50 step 7) {
                assertEquals(gen1.getHeight(wx, wz), gen2.getHeight(wx, wz),
                    "Same seed should produce same height at ($wx, $wz)")
            }
        }
    }

    @Test
    fun `noise - different seeds produce different terrain`() {
        val gen1 = TerrainGenerator(seed = 1, useNoise = true)
        val gen2 = TerrainGenerator(seed = 9999, useNoise = true)

        var differences = 0
        for (wx in 0..50 step 5) {
            for (wz in 0..50 step 5) {
                if (gen1.getHeight(wx, wz) != gen2.getHeight(wx, wz)) {
                    differences++
                }
            }
        }
        assertTrue(differences > 0, "Different seeds should produce different terrain")
    }

    @Test
    fun `noise - terrain has grass on surface`() {
        val chunk = noiseGenerator.generateChunk(ChunkPos(2, 2))
        // Check a column — should have grass at surface level
        val worldX = 2 * 16 + 8
        val worldZ = 2 * 16 + 8
        val surfaceY = noiseGenerator.getHeight(worldX, worldZ)
        assertEquals(BlockType.GRASS, chunk.getBlock(8, surfaceY, 8),
            "Surface should be grass at Y=$surfaceY")
    }

    @Test
    fun `noise - terrain has dirt below surface`() {
        val chunk = noiseGenerator.generateChunk(ChunkPos(2, 2))
        val worldX = 2 * 16 + 8
        val worldZ = 2 * 16 + 8
        val surfaceY = noiseGenerator.getHeight(worldX, worldZ)
        if (surfaceY > 5) {
            assertEquals(BlockType.DIRT, chunk.getBlock(8, surfaceY - 1, 8),
                "Below surface should be dirt")
        }
    }

    @Test
    fun `noise - terrain has stone deep underground`() {
        val chunk = noiseGenerator.generateChunk(ChunkPos(2, 2))
        assertEquals(BlockType.STONE, chunk.getBlock(8, 10, 8),
            "Deep underground should be stone")
    }

    @Test
    fun `noise - air above terrain surface`() {
        val chunk = noiseGenerator.generateChunk(ChunkPos(5, 5))
        val worldX = 5 * 16 + 8
        val worldZ = 5 * 16 + 8
        val surfaceY = noiseGenerator.getHeight(worldX, worldZ)
        assertEquals(BlockType.AIR, chunk.getBlock(8, surfaceY + 10, 8),
            "Should be air well above terrain surface")
    }

    @Test
    fun `noise - terrain is not flat`() {
        // Sample multiple heights — noise terrain should have variation
        val heights = mutableSetOf<Int>()
        for (wx in -200..200 step 16) {
            for (wz in -200..200 step 16) {
                heights.add(noiseGenerator.getHeight(wx, wz))
            }
        }
        assertTrue(heights.size > 3,
            "Noise terrain should have height variation, got ${heights.size} unique heights")
    }
}
