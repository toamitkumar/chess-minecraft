package com.knightscrusade.common.chunk

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [ChunkPos] — chunk coordinate system.
 */
class ChunkPosTest {

    @Test
    fun `worldX and worldZ return correct block positions`() {
        val pos = ChunkPos(3, 5)
        assertEquals(48, pos.worldX)  // 3 * 16
        assertEquals(80, pos.worldZ)  // 5 * 16
    }

    @Test
    fun `negative chunk coordinates produce correct world positions`() {
        val pos = ChunkPos(-2, -1)
        assertEquals(-32, pos.worldX)  // -2 * 16
        assertEquals(-16, pos.worldZ)  // -1 * 16
    }

    @Test
    fun `fromWorldPos for positive coordinates`() {
        assertEquals(ChunkPos(0, 0), ChunkPos.fromWorldPos(0, 0))
        assertEquals(ChunkPos(0, 0), ChunkPos.fromWorldPos(15, 15))
        assertEquals(ChunkPos(1, 1), ChunkPos.fromWorldPos(16, 16))
        assertEquals(ChunkPos(1, 0), ChunkPos.fromWorldPos(16, 0))
        assertEquals(ChunkPos(2, 3), ChunkPos.fromWorldPos(40, 50))
    }

    @Test
    fun `fromWorldPos for negative coordinates`() {
        assertEquals(ChunkPos(-1, -1), ChunkPos.fromWorldPos(-1, -1))
        assertEquals(ChunkPos(-1, -1), ChunkPos.fromWorldPos(-16, -16))
        assertEquals(ChunkPos(-2, -2), ChunkPos.fromWorldPos(-17, -17))
    }

    @Test
    fun `data class equality`() {
        val a = ChunkPos(3, 5)
        val b = ChunkPos(3, 5)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `data class inequality`() {
        assertNotEquals(ChunkPos(3, 5), ChunkPos(3, 6))
        assertNotEquals(ChunkPos(3, 5), ChunkPos(4, 5))
    }

    @Test
    fun `origin chunk world positions`() {
        val pos = ChunkPos(0, 0)
        assertEquals(0, pos.worldX)
        assertEquals(0, pos.worldZ)
    }
}
