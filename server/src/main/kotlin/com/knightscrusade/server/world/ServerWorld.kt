package com.knightscrusade.server.world

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Server-side world state. Owns all chunk data and is the authoritative
 * source of truth for the game world.
 *
 * Generates chunks on demand, caches them, and handles block modifications.
 * In Phase 5, this will be backed by region file persistence.
 */
class ServerWorld(
    seed: Int = 42,
    useNoise: Boolean = true
) {

    private val logger = LoggerFactory.getLogger(ServerWorld::class.java)

    private val chunks = ConcurrentHashMap<ChunkPos, ChunkColumn>()
    private val terrainGenerator = TerrainGenerator(seed = seed, useNoise = useNoise)

    /**
     * Get or generate a chunk at the specified position.
     * Chunks are generated lazily and cached.
     *
     * @param pos Chunk position in chunk coordinates
     * @return The chunk column at this position
     */
    fun getChunk(pos: ChunkPos): ChunkColumn {
        return chunks.getOrPut(pos) {
            terrainGenerator.generateChunk(pos).also {
                logger.debug("Generated chunk at $pos")
            }
        }
    }

    /**
     * Get a block at world coordinates. Generates the containing chunk if needed.
     *
     * @return The block type at (x, y, z)
     */
    fun getBlock(x: Int, y: Int, z: Int): BlockType {
        val chunkPos = ChunkPos.fromWorldPos(x, z)
        val chunk = getChunk(chunkPos)
        val localX = ((x % 16) + 16) % 16
        val localZ = ((z % 16) + 16) % 16
        return chunk.getBlock(localX, y, localZ)
    }

    /**
     * Set a block at world coordinates. Server-authoritative: validates the change.
     *
     * @return true if the block was successfully changed
     */
    fun setBlock(x: Int, y: Int, z: Int, type: BlockType): Boolean {
        if (y < 0 || y >= ChunkColumn.HEIGHT) return false

        // Don't allow breaking bedrock
        if (getBlock(x, y, z) == BlockType.BEDROCK && type == BlockType.AIR) return false

        val chunkPos = ChunkPos.fromWorldPos(x, z)
        val chunk = getChunk(chunkPos)
        val localX = ((x % 16) + 16) % 16
        val localZ = ((z % 16) + 16) % 16
        chunk.setBlock(localX, y, localZ, type)
        return true
    }

    /**
     * Get all currently loaded chunks.
     */
    fun getLoadedChunks(): Map<ChunkPos, ChunkColumn> = chunks

    /**
     * Get the number of loaded chunks.
     */
    fun loadedChunkCount(): Int = chunks.size

    /**
     * Get the spawn position (center of the world, on top of terrain).
     */
    fun getSpawnPosition(): Triple<Double, Double, Double> {
        // Find the surface at (0, 0) by scanning down from the top
        val chunk = getChunk(ChunkPos(0, 0))
        for (y in ChunkColumn.HEIGHT - 1 downTo 0) {
            if (chunk.getBlock(8, y, 8).solid) {
                return Triple(8.5, (y + 1).toDouble(), 8.5)
            }
        }
        return Triple(8.5, 65.0, 8.5)
    }
}
