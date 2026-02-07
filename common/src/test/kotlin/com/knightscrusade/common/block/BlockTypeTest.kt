package com.knightscrusade.common.block

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [BlockType] enum and its companion object.
 */
class BlockTypeTest {

    @Test
    fun `all block types have unique IDs`() {
        val ids = BlockType.entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "Duplicate block IDs found")
    }

    @Test
    fun `fromId returns correct block type`() {
        for (type in BlockType.entries) {
            assertEquals(type, BlockType.fromId(type.id), "fromId(${type.id}) should return $type")
        }
    }

    @Test
    fun `fromId returns AIR for unknown IDs`() {
        assertEquals(BlockType.AIR, BlockType.fromId(-1))
        assertEquals(BlockType.AIR, BlockType.fromId(9999))
    }

    @Test
    fun `AIR is not solid and is transparent`() {
        assertFalse(BlockType.AIR.solid)
        assertTrue(BlockType.AIR.transparent)
    }

    @Test
    fun `STONE is solid and not transparent`() {
        assertTrue(BlockType.STONE.solid)
        assertFalse(BlockType.STONE.transparent)
    }

    @Test
    fun `WATER is not solid and is transparent`() {
        assertFalse(BlockType.WATER.solid)
        assertTrue(BlockType.WATER.transparent)
    }

    @Test
    fun `TORCH emits light`() {
        assertTrue(BlockType.TORCH.lightLevel > 0, "Torch should emit light")
        assertEquals(14, BlockType.TORCH.lightLevel)
    }

    @Test
    fun `GOLD_BLOCK emits light`() {
        assertTrue(BlockType.GOLD_BLOCK.lightLevel > 0, "Gold block should emit light")
    }

    @Test
    fun `all block types have 3-component color arrays`() {
        for (type in BlockType.entries) {
            assertEquals(3, type.color.size, "$type should have RGB color (3 components)")
            for (i in 0..2) {
                assertTrue(type.color[i] in 0f..1f, "$type color[$i] should be in [0, 1]")
            }
        }
    }

    @Test
    fun `AIR has ID 0`() {
        assertEquals(0, BlockType.AIR.id, "AIR must have ID 0 (convention for empty blocks)")
    }
}
