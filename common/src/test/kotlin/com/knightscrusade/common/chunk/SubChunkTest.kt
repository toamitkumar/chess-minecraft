package com.knightscrusade.common.chunk

import com.knightscrusade.common.block.BlockType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * Tests for [SubChunk] — 16x16x16 block storage.
 */
class SubChunkTest {

    @Test
    fun `new sub-chunk is empty (all AIR)`() {
        val subChunk = SubChunk()
        assertTrue(subChunk.isEmpty())
        assertEquals(BlockType.AIR, subChunk.getBlock(0, 0, 0))
        assertEquals(BlockType.AIR, subChunk.getBlock(15, 15, 15))
    }

    @Test
    fun `setBlock and getBlock round-trip correctly`() {
        val subChunk = SubChunk()
        subChunk.setBlock(0, 0, 0, BlockType.STONE)
        subChunk.setBlock(15, 15, 15, BlockType.OBSIDIAN)
        subChunk.setBlock(8, 4, 12, BlockType.GRASS)

        assertEquals(BlockType.STONE, subChunk.getBlock(0, 0, 0))
        assertEquals(BlockType.OBSIDIAN, subChunk.getBlock(15, 15, 15))
        assertEquals(BlockType.GRASS, subChunk.getBlock(8, 4, 12))
    }

    @Test
    fun `sub-chunk is not empty after setting a block`() {
        val subChunk = SubChunk()
        subChunk.setBlock(7, 7, 7, BlockType.STONE)
        assertFalse(subChunk.isEmpty())
    }

    @Test
    fun `out-of-bounds coordinates throw exception`() {
        val subChunk = SubChunk()
        assertThrows<IllegalArgumentException> { subChunk.getBlock(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { subChunk.getBlock(16, 0, 0) }
        assertThrows<IllegalArgumentException> { subChunk.getBlock(0, -1, 0) }
        assertThrows<IllegalArgumentException> { subChunk.getBlock(0, 16, 0) }
        assertThrows<IllegalArgumentException> { subChunk.getBlock(0, 0, -1) }
        assertThrows<IllegalArgumentException> { subChunk.getBlock(0, 0, 16) }
    }

    @Test
    fun `setBlock overwrites existing block`() {
        val subChunk = SubChunk()
        subChunk.setBlock(5, 5, 5, BlockType.STONE)
        assertEquals(BlockType.STONE, subChunk.getBlock(5, 5, 5))

        subChunk.setBlock(5, 5, 5, BlockType.DIRT)
        assertEquals(BlockType.DIRT, subChunk.getBlock(5, 5, 5))
    }

    @Test
    fun `getRawData and setRawData round-trip correctly`() {
        val original = SubChunk()
        original.setBlock(0, 0, 0, BlockType.STONE)
        original.setBlock(7, 3, 12, BlockType.QUARTZ)
        original.setBlock(15, 15, 15, BlockType.BEDROCK)

        val rawData = original.getRawData()
        assertEquals(16 * 16 * 16, rawData.size)

        val copy = SubChunk()
        copy.setRawData(rawData)

        assertEquals(BlockType.STONE, copy.getBlock(0, 0, 0))
        assertEquals(BlockType.QUARTZ, copy.getBlock(7, 3, 12))
        assertEquals(BlockType.BEDROCK, copy.getBlock(15, 15, 15))
        assertEquals(BlockType.AIR, copy.getBlock(1, 1, 1))
    }

    @Test
    fun `setRawData rejects wrong-size arrays`() {
        val subChunk = SubChunk()
        assertThrows<IllegalArgumentException> { subChunk.setRawData(ShortArray(100)) }
    }

    @Test
    fun `all 4096 positions are independently addressable`() {
        val subChunk = SubChunk()
        var count = 0
        for (y in 0 until 16) {
            for (z in 0 until 16) {
                for (x in 0 until 16) {
                    val type = BlockType.entries[count % BlockType.entries.size]
                    subChunk.setBlock(x, y, z, type)
                    count++
                }
            }
        }

        count = 0
        for (y in 0 until 16) {
            for (z in 0 until 16) {
                for (x in 0 until 16) {
                    val expected = BlockType.entries[count % BlockType.entries.size]
                    assertEquals(expected, subChunk.getBlock(x, y, z),
                        "Mismatch at ($x, $y, $z)")
                    count++
                }
            }
        }
    }
}
