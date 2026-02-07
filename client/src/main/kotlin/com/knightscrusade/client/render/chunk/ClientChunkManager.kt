package com.knightscrusade.client.render.chunk

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Client-side chunk storage and mesh management.
 *
 * Stores chunk data received from the server, builds meshes for rendering,
 * and tracks which chunks need mesh rebuilds. In Phase 2 this also generates
 * a hardcoded flat world; in Phase 3 chunk data will come from the server.
 */
class ClientChunkManager {

    private val logger = LoggerFactory.getLogger(ClientChunkManager::class.java)

    /** Chunk data storage */
    private val chunks = ConcurrentHashMap<ChunkPos, ChunkColumn>()

    /** Built meshes per sub-chunk: key = "chunkX,chunkZ,subIndex" */
    private val meshes = ConcurrentHashMap<String, ChunkMesh>()

    /** Chunks needing mesh rebuild */
    private val dirtyChunks = ConcurrentHashMap.newKeySet<ChunkPos>()

    /**
     * Generate a flat world for Phase 2 testing.
     * Creates a grid of chunks centered on (0, 0) with flat terrain.
     *
     * @param radius Number of chunks in each direction from center
     */
    fun generateFlatWorld(radius: Int = 8) {
        logger.info("Generating flat world with radius $radius (${(2 * radius + 1) * (2 * radius + 1)} chunks)")

        for (cx in -radius..radius) {
            for (cz in -radius..radius) {
                val pos = ChunkPos(cx, cz)
                val chunk = ChunkColumn(pos)

                // Fill terrain layers
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

                // Add some structures for visual interest
                addStructures(chunk, cx, cz)

                chunks[pos] = chunk
                dirtyChunks.add(pos)
            }
        }

        logger.info("Flat world generated: ${chunks.size} chunks")
    }

    /**
     * Add some small structures to make the flat world more visually interesting.
     */
    private fun addStructures(chunk: ChunkColumn, cx: Int, cz: Int) {
        // Obsidian tower at the world center
        if (cx == 0 && cz == 0) {
            for (y in 64..80) {
                for (x in 6..9) {
                    for (z in 6..9) {
                        // Hollow tower
                        if (x == 6 || x == 9 || z == 6 || z == 9) {
                            chunk.setBlock(x, y, z, BlockType.OBSIDIAN)
                        }
                    }
                }
            }
            // Gold block on top (coordinate marker)
            chunk.setBlock(7, 81, 7, BlockType.GOLD_BLOCK)
            chunk.setBlock(8, 81, 7, BlockType.GOLD_BLOCK)
            chunk.setBlock(7, 81, 8, BlockType.GOLD_BLOCK)
            chunk.setBlock(8, 81, 8, BlockType.GOLD_BLOCK)
        }

        // Quartz pillar at chunk (3, 3)
        if (cx == 3 && cz == 3) {
            for (y in 64..72) {
                chunk.setBlock(8, y, 8, BlockType.QUARTZ)
            }
        }

        // Stone wall across chunk (0, 2)
        if (cx == 0 && cz == 2) {
            for (x in 0..15) {
                for (y in 64..67) {
                    chunk.setBlock(x, y, 8, BlockType.BLACKSTONE)
                }
            }
        }
    }

    /**
     * Store a chunk received from the server (or generated locally).
     */
    fun setChunk(pos: ChunkPos, chunk: ChunkColumn) {
        chunks[pos] = chunk
        dirtyChunks.add(pos)
    }

    /**
     * Get a chunk by its position.
     */
    fun getChunk(pos: ChunkPos): ChunkColumn? = chunks[pos]

    /**
     * Rebuild meshes for any dirty chunks. Call once per frame.
     * Returns the number of meshes rebuilt.
     */
    fun rebuildDirtyMeshes(): Int {
        var rebuilt = 0
        val toRebuild = dirtyChunks.toList()
        dirtyChunks.removeAll(toRebuild.toSet())

        for (pos in toRebuild) {
            val chunk = chunks[pos] ?: continue

            for (subIndex in 0 until ChunkColumn.NUM_SUB_CHUNKS) {
                val key = "${pos.x},${pos.z},$subIndex"
                val vertexData = ChunkMeshBuilder.buildMesh(
                    chunk, subIndex, pos.worldX, pos.worldZ
                )

                if (vertexData.isEmpty()) {
                    meshes.remove(key)?.cleanup()
                } else {
                    val mesh = meshes.getOrPut(key) { ChunkMesh() }
                    mesh.upload(vertexData)
                    rebuilt++
                }
            }
        }

        return rebuilt
    }

    /**
     * Get all built meshes for rendering.
     */
    fun getMeshes(): Collection<ChunkMesh> = meshes.values

    /**
     * Get the total number of loaded chunks.
     */
    fun getChunkCount(): Int = chunks.size

    /**
     * Get the total number of uploaded mesh objects.
     */
    fun getMeshCount(): Int = meshes.values.count { it.uploaded }

    /**
     * Test if a chunk is within frustum using AABB test against frustum planes.
     */
    fun isChunkVisible(pos: ChunkPos, subIndex: Int, frustumPlanes: Array<FloatArray>): Boolean {
        val minX = pos.worldX.toFloat()
        val minY = (subIndex * 16).toFloat()
        val minZ = pos.worldZ.toFloat()
        val maxX = minX + 16f
        val maxY = minY + 16f
        val maxZ = minZ + 16f

        for (plane in frustumPlanes) {
            val a = plane[0]; val b = plane[1]; val c = plane[2]; val d = plane[3]

            // Test the corner that is most in the direction of the plane normal
            val px = if (a > 0) maxX else minX
            val py = if (b > 0) maxY else minY
            val pz = if (c > 0) maxZ else minZ

            if (a * px + b * py + c * pz + d < 0) {
                return false // Chunk is entirely outside this plane
            }
        }
        return true
    }

    /**
     * Clean up all OpenGL resources.
     */
    fun cleanup() {
        meshes.values.forEach { it.cleanup() }
        meshes.clear()
        chunks.clear()
    }
}
