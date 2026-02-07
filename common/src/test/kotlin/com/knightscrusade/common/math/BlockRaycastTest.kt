package com.knightscrusade.common.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for block raycasting (DDA voxel traversal).
 */
class BlockRaycastTest {

    @Test
    fun `raycast hits block directly ahead`() {
        // Standing at (0.5, 1.5, 0.5), looking along +X
        val hit = blockRaycast(
            0.5f, 1.5f, 0.5f,
            1f, 0f, 0f,
            maxDistance = 10f
        ) { x, y, z -> x == 5 && y == 1 && z == 0 }

        assertNotNull(hit)
        assertEquals(5, hit!!.blockX)
        assertEquals(1, hit.blockY)
        assertEquals(0, hit.blockZ)
        // Should hit the -X face
        assertEquals(-1, hit.faceNormalX)
        assertEquals(0, hit.faceNormalY)
        assertEquals(0, hit.faceNormalZ)
    }

    @Test
    fun `raycast hits block below (looking down)`() {
        val hit = blockRaycast(
            0.5f, 5.5f, 0.5f,
            0f, -1f, 0f,
            maxDistance = 10f
        ) { x, y, z -> y == 0 }

        assertNotNull(hit)
        assertEquals(0, hit!!.blockY)
        assertEquals(0, hit.faceNormalX)
        assertEquals(1, hit.faceNormalY) // Hit top face
        assertEquals(0, hit.faceNormalZ)
    }

    @Test
    fun `raycast returns null when no block in range`() {
        val hit = blockRaycast(
            0f, 0f, 0f,
            1f, 0f, 0f,
            maxDistance = 5f
        ) { _, _, _ -> false }

        assertNull(hit)
    }

    @Test
    fun `raycast place position is adjacent to hit`() {
        val hit = blockRaycast(
            0.5f, 2.5f, 0.5f,
            0f, -1f, 0f,
            maxDistance = 10f
        ) { _, y, _ -> y == 0 }

        assertNotNull(hit)
        assertEquals(0, hit!!.blockY)
        assertEquals(1, hit.placeY) // Place on top of hit block
    }

    @Test
    fun `raycast respects max distance`() {
        val hit = blockRaycast(
            0f, 0f, 0f,
            1f, 0f, 0f,
            maxDistance = 3f
        ) { x, _, _ -> x == 10 }

        assertNull(hit) // Block at x=10 is too far
    }

    @Test
    fun `raycast works with diagonal direction`() {
        // Looking at 45 degrees on XZ plane
        val dir = 0.707107f // sqrt(2)/2
        val hit = blockRaycast(
            0.5f, 1.5f, 0.5f,
            dir, 0f, dir,
            maxDistance = 10f
        ) { x, y, z -> x == 3 && y == 1 && z == 3 }

        assertNotNull(hit)
        assertEquals(3, hit!!.blockX)
        assertEquals(1, hit.blockY)
        assertEquals(3, hit.blockZ)
    }

    @Test
    fun `hit distance is positive`() {
        val hit = blockRaycast(
            0.5f, 1.5f, 0.5f,
            1f, 0f, 0f,
            maxDistance = 10f
        ) { x, y, z -> x == 3 && y == 1 && z == 0 }

        assertNotNull(hit)
        assertTrue(hit!!.distance > 0f)
        assertTrue(hit.distance < 10f)
    }
}
