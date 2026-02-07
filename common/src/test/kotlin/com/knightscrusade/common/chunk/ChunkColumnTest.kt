package com.knightscrusade.common.chunk

import com.knightscrusade.common.block.BlockType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [ChunkColumn] — full-height column of sub-chunks.
 */
class ChunkColumnTest {

    @Test
    fun `new chunk column is all AIR`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))
        assertEquals(BlockType.AIR, chunk.getBlock(0, 0, 0))
        assertEquals(BlockType.AIR, chunk.getBlock(15, 255, 15))
        assertEquals(BlockType.AIR, chunk.getBlock(8, 128, 8))
    }

    @Test
    fun `setBlock and getBlock across sub-chunk boundaries`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))

        // Bottom of first sub-chunk
        chunk.setBlock(0, 0, 0, BlockType.BEDROCK)
        assertEquals(BlockType.BEDROCK, chunk.getBlock(0, 0, 0))

        // Top of first sub-chunk
        chunk.setBlock(0, 15, 0, BlockType.STONE)
        assertEquals(BlockType.STONE, chunk.getBlock(0, 15, 0))

        // Bottom of second sub-chunk
        chunk.setBlock(0, 16, 0, BlockType.DIRT)
        assertEquals(BlockType.DIRT, chunk.getBlock(0, 16, 0))

        // High up
        chunk.setBlock(8, 200, 8, BlockType.QUARTZ)
        assertEquals(BlockType.QUARTZ, chunk.getBlock(8, 200, 8))

        // Top of world
        chunk.setBlock(15, 255, 15, BlockType.GOLD_BLOCK)
        assertEquals(BlockType.GOLD_BLOCK, chunk.getBlock(15, 255, 15))
    }

    @Test
    fun `out-of-range Y returns AIR`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))
        assertEquals(BlockType.AIR, chunk.getBlock(0, -1, 0))
        assertEquals(BlockType.AIR, chunk.getBlock(0, 256, 0))
        assertEquals(BlockType.AIR, chunk.getBlock(0, 999, 0))
    }

    @Test
    fun `setBlock out-of-range Y is silently ignored`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))
        // Should not throw
        chunk.setBlock(0, -1, 0, BlockType.STONE)
        chunk.setBlock(0, 256, 0, BlockType.STONE)
    }

    @Test
    fun `dirty flag tracks modifications`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))
        assertFalse(chunk.dirty)

        chunk.setBlock(0, 0, 0, BlockType.STONE)
        assertTrue(chunk.dirty)

        chunk.markClean()
        assertFalse(chunk.dirty)
    }

    @Test
    fun `chunk column has 16 sub-chunks`() {
        assertEquals(16, ChunkColumn.NUM_SUB_CHUNKS)
        assertEquals(256, ChunkColumn.HEIGHT)
    }

    @Test
    fun `pos stores chunk coordinates`() {
        val pos = ChunkPos(5, -3)
        val chunk = ChunkColumn(pos)
        assertEquals(5, chunk.pos.x)
        assertEquals(-3, chunk.pos.z)
    }

    @Test
    fun `getSubChunk returns correct sub-chunk`() {
        val chunk = ChunkColumn(ChunkPos(0, 0))
        chunk.setBlock(5, 20, 5, BlockType.STONE) // Y=20 is in sub-chunk index 1

        val subChunk = chunk.getSubChunk(1)
        assertEquals(BlockType.STONE, subChunk.getBlock(5, 4, 5)) // local Y = 20 - 16 = 4
    }
}
