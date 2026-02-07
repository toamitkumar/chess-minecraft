package com.knightscrusade.common.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [AABB] — axis-aligned bounding box collision math.
 */
class AABBTest {

    @Test
    fun `fromEntity creates correct AABB`() {
        val aabb = AABB.fromEntity(5.0, 10.0, 5.0, 0.3, 1.8)
        assertEquals(4.7, aabb.minX, 0.001)
        assertEquals(10.0, aabb.minY, 0.001)
        assertEquals(4.7, aabb.minZ, 0.001)
        assertEquals(5.3, aabb.maxX, 0.001)
        assertEquals(11.8, aabb.maxY, 0.001)
        assertEquals(5.3, aabb.maxZ, 0.001)
    }

    @Test
    fun `block creates unit cube`() {
        val aabb = AABB.block(3, 5, 7)
        assertEquals(3.0, aabb.minX)
        assertEquals(5.0, aabb.minY)
        assertEquals(7.0, aabb.minZ)
        assertEquals(4.0, aabb.maxX)
        assertEquals(6.0, aabb.maxY)
        assertEquals(8.0, aabb.maxZ)
    }

    @Test
    fun `intersects detects overlapping AABBs`() {
        val a = AABB(0.0, 0.0, 0.0, 2.0, 2.0, 2.0)
        val b = AABB(1.0, 1.0, 1.0, 3.0, 3.0, 3.0)
        assertTrue(a.intersects(b))
        assertTrue(b.intersects(a))
    }

    @Test
    fun `intersects rejects non-overlapping AABBs`() {
        val a = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val b = AABB(2.0, 2.0, 2.0, 3.0, 3.0, 3.0)
        assertFalse(a.intersects(b))
    }

    @Test
    fun `intersects rejects touching but not overlapping AABBs`() {
        val a = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val b = AABB(1.0, 0.0, 0.0, 2.0, 1.0, 1.0) // Touching face
        assertFalse(a.intersects(b))
    }

    @Test
    fun `offset moves AABB correctly`() {
        val aabb = AABB(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        val moved = aabb.offset(10.0, 20.0, 30.0)
        assertEquals(11.0, moved.minX)
        assertEquals(22.0, moved.minY)
        assertEquals(33.0, moved.minZ)
    }

    @Test
    fun `expand extends AABB in movement direction`() {
        val aabb = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val expanded = aabb.expand(2.0, -1.0, 0.5)
        assertEquals(0.0, expanded.minX)  // Positive dx doesn't change min
        assertEquals(-1.0, expanded.minY) // Negative dy extends min
        assertEquals(0.0, expanded.minZ)
        assertEquals(3.0, expanded.maxX)  // Positive dx extends max
        assertEquals(1.0, expanded.maxY)  // Negative dy doesn't change max
        assertEquals(1.5, expanded.maxZ)
    }

    @Test
    fun `clipYCollide clips falling movement against floor`() {
        val entity = AABB(0.0, 1.0, 0.0, 1.0, 2.0, 1.0)
        val floor = AABB.block(0, 0, 0) // Block at (0,0,0) to (1,1,1)
        val dy = entity.clipYCollide(floor, -2.0)
        assertEquals(0.0, dy, 0.001) // Entity stops at Y=1 (on top of block at Y=0-1)
    }

    @Test
    fun `clipYCollide does not clip when not overlapping XZ`() {
        val entity = AABB(5.0, 1.0, 5.0, 6.0, 2.0, 6.0)
        val floor = AABB.block(0, 0, 0) // Far away on XZ plane
        val dy = entity.clipYCollide(floor, -2.0)
        assertEquals(-2.0, dy, 0.001) // No clipping
    }

    @Test
    fun `clipXCollide clips movement against wall`() {
        val entity = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val wall = AABB.block(2, 0, 0)
        val dx = entity.clipXCollide(wall, 3.0)
        assertEquals(1.0, dx, 0.001) // Clips at x=2
    }

    @Test
    fun `clipZCollide clips movement against wall`() {
        val entity = AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val wall = AABB.block(0, 0, 2)
        val dz = entity.clipZCollide(wall, 3.0)
        assertEquals(1.0, dz, 0.001)
    }

    @Test
    fun `dimensions are correct`() {
        val aabb = AABB(1.0, 2.0, 3.0, 4.0, 7.0, 9.0)
        assertEquals(3.0, aabb.width, 0.001)
        assertEquals(5.0, aabb.height, 0.001)
        assertEquals(6.0, aabb.depth, 0.001)
    }

    @Test
    fun `center is correct`() {
        val aabb = AABB(0.0, 0.0, 0.0, 4.0, 6.0, 8.0)
        assertEquals(2.0, aabb.centerX, 0.001)
        assertEquals(3.0, aabb.centerY, 0.001)
        assertEquals(4.0, aabb.centerZ, 0.001)
    }
}
