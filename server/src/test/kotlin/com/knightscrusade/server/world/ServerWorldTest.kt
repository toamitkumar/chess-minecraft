package com.knightscrusade.server.world

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkPos
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [ServerWorld] — server-side authoritative world state.
 * Uses flat terrain mode for predictable block positions.
 */
class ServerWorldTest {

    @Test
    fun `getChunk generates and caches chunk on first access`() {
        val world = ServerWorld(useNoise = false)
        val pos = ChunkPos(0, 0)

        val chunk1 = world.getChunk(pos)
        val chunk2 = world.getChunk(pos)

        assertSame(chunk1, chunk2, "Same chunk should be returned from cache")
        assertEquals(1, world.loadedChunkCount())
    }

    @Test
    fun `getBlock returns correct terrain blocks`() {
        val world = ServerWorld(useNoise = false)
        assertEquals(BlockType.BEDROCK, world.getBlock(0, 0, 0))
        assertEquals(BlockType.STONE, world.getBlock(0, 30, 0))
        assertEquals(BlockType.GRASS, world.getBlock(0, 63, 0))
        assertEquals(BlockType.AIR, world.getBlock(0, 64, 0))
    }

    @Test
    fun `setBlock modifies world state`() {
        val world = ServerWorld(useNoise = false)
        assertTrue(world.setBlock(5, 64, 5, BlockType.STONE))
        assertEquals(BlockType.STONE, world.getBlock(5, 64, 5))
    }

    @Test
    fun `setBlock rejects out-of-range Y`() {
        val world = ServerWorld(useNoise = false)
        assertFalse(world.setBlock(0, -1, 0, BlockType.STONE))
        assertFalse(world.setBlock(0, 256, 0, BlockType.STONE))
    }

    @Test
    fun `setBlock rejects bedrock removal`() {
        val world = ServerWorld(useNoise = false)
        assertEquals(BlockType.BEDROCK, world.getBlock(0, 0, 0))
        assertFalse(world.setBlock(0, 0, 0, BlockType.AIR))
        assertEquals(BlockType.BEDROCK, world.getBlock(0, 0, 0))
    }

    @Test
    fun `getBlock works across chunk boundaries`() {
        val world = ServerWorld(useNoise = false)
        assertEquals(BlockType.GRASS, world.getBlock(20, 63, 20))
        assertEquals(BlockType.GRASS, world.getBlock(-5, 63, -5))
    }

    @Test
    fun `getSpawnPosition returns position above terrain`() {
        val world = ServerWorld(useNoise = false)
        val (x, y, z) = world.getSpawnPosition()
        assertTrue(y >= 64.0, "Spawn Y should be above terrain surface")
    }

    @Test
    fun `multiple chunks load independently`() {
        val world = ServerWorld(useNoise = false)
        world.getChunk(ChunkPos(0, 0))
        world.getChunk(ChunkPos(1, 0))
        world.getChunk(ChunkPos(0, 1))
        assertEquals(3, world.loadedChunkCount())
    }
}
