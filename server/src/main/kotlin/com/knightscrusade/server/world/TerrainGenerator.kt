package com.knightscrusade.server.world

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import com.knightscrusade.common.noise.FastNoiseLite

/**
 * Generates terrain for new chunks using 4-octave Simplex noise.
 *
 * Produces natural-looking terrain with rolling hills (Y=40-80 range),
 * proper bedrock/stone/dirt/grass layers, and decorative structures
 * at fixed positions. Also provides a flat terrain mode for testing.
 *
 * @param seed World seed for deterministic generation
 * @param useNoise Whether to use noise terrain (true) or flat terrain (false)
 */
class TerrainGenerator(
    private val seed: Int = 42,
    private val useNoise: Boolean = true
) {

    companion object {
        /** Height range for noise terrain. */
        const val MIN_HEIGHT = 40
        const val MAX_HEIGHT = 80
        const val SEA_LEVEL = 63

        /** Flat terrain constants (used when useNoise = false). */
        const val FLAT_SURFACE_Y = 63
        const val BEDROCK_Y = 0
        const val FLAT_STONE_TOP_Y = 58
        const val FLAT_DIRT_TOP_Y = 62
    }

    /** Primary heightmap noise: 4-octave FBm Simplex. */
    private val heightNoise = FastNoiseLite(seed).apply {
        SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2)
        SetFractalType(FastNoiseLite.FractalType.FBm)
        SetFractalOctaves(4)
        SetFractalLacunarity(2.0f)
        SetFractalGain(0.5f)
        SetFrequency(0.005f)
    }

    /** Secondary detail noise for terrain variation. */
    private val detailNoise = FastNoiseLite(seed + 1).apply {
        SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2)
        SetFractalType(FastNoiseLite.FractalType.FBm)
        SetFractalOctaves(2)
        SetFrequency(0.02f)
    }

    /**
     * Generate a new chunk column with terrain and structures.
     *
     * @param pos Chunk position in chunk coordinates
     * @return Fully populated chunk column
     */
    fun generateChunk(pos: ChunkPos): ChunkColumn {
        val chunk = ChunkColumn(pos)

        if (useNoise) {
            generateNoiseTerrain(chunk, pos)
        } else {
            generateFlatTerrain(chunk)
        }

        generateStructures(chunk, pos)
        chunk.markClean()
        return chunk
    }

    /**
     * Generate terrain using noise-based heightmap.
     * Produces rolling hills with bedrock/stone/dirt/grass layers.
     */
    private fun generateNoiseTerrain(chunk: ChunkColumn, pos: ChunkPos) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                val worldX = pos.worldX + x
                val worldZ = pos.worldZ + z

                val surfaceY = getHeight(worldX, worldZ)

                // Bedrock layer (Y=0, with some variation at Y=1-3)
                chunk.setBlock(x, 0, z, BlockType.BEDROCK)
                for (y in 1..3) {
                    // Scattered bedrock in first few layers
                    val bedrockNoise = detailNoise.GetNoise(
                        (worldX * 17 + y * 31).toFloat(),
                        (worldZ * 13 + y * 7).toFloat()
                    )
                    if (bedrockNoise > (y - 1) * 0.3f) {
                        chunk.setBlock(x, y, z, BlockType.BEDROCK)
                    } else {
                        chunk.setBlock(x, y, z, BlockType.STONE)
                    }
                }

                // Stone layer (Y=4 to surfaceY-4)
                for (y in 4..(surfaceY - 4).coerceAtLeast(4)) {
                    chunk.setBlock(x, y, z, BlockType.STONE)
                }

                // Dirt layer (top 3 blocks below surface)
                for (y in (surfaceY - 3).coerceAtLeast(5) until surfaceY) {
                    chunk.setBlock(x, y, z, BlockType.DIRT)
                }

                // Surface block
                if (surfaceY > 0) {
                    chunk.setBlock(x, surfaceY, z, BlockType.GRASS)
                }
            }
        }
    }

    /**
     * Generate flat terrain for testing (used when useNoise=false).
     * Matches the Phase 2-3 flat world format.
     */
    private fun generateFlatTerrain(chunk: ChunkColumn) {
        for (x in 0 until 16) {
            for (z in 0 until 16) {
                chunk.setBlock(x, BEDROCK_Y, z, BlockType.BEDROCK)
                for (y in 1..FLAT_STONE_TOP_Y) {
                    chunk.setBlock(x, y, z, BlockType.STONE)
                }
                for (y in (FLAT_STONE_TOP_Y + 1)..FLAT_DIRT_TOP_Y) {
                    chunk.setBlock(x, y, z, BlockType.DIRT)
                }
                chunk.setBlock(x, FLAT_SURFACE_Y, z, BlockType.GRASS)
            }
        }
    }

    /**
     * Get the terrain surface height at a world position using noise.
     *
     * @param worldX World X coordinate
     * @param worldZ World Z coordinate
     * @return Surface Y level (MIN_HEIGHT to MAX_HEIGHT range)
     */
    fun getHeight(worldX: Int, worldZ: Int): Int {
        val baseNoise = heightNoise.GetNoise(worldX.toFloat(), worldZ.toFloat())
        val detail = detailNoise.GetNoise(worldX.toFloat(), worldZ.toFloat()) * 0.3f

        // Map noise from [-1, 1] to height range
        val normalizedNoise = (baseNoise + detail + 1f) / 2f  // [0, 1]
        return (MIN_HEIGHT + normalizedNoise * (MAX_HEIGHT - MIN_HEIGHT)).toInt()
            .coerceIn(MIN_HEIGHT, MAX_HEIGHT)
    }

    /**
     * Place structures in specific chunks to create visual landmarks.
     */
    private fun generateStructures(chunk: ChunkColumn, pos: ChunkPos) {
        if (pos.x == 0 && pos.z == 0) {
            generateObsidianTower(chunk, pos)
        }
        if (pos.x == 3 && pos.z == 3) {
            generateQuartzPillar(chunk, pos)
        }
        if (pos.x == 0 && pos.z == 2) {
            generateBlackstoneWall(chunk, pos)
        }
        if (pos.x == -2 && pos.z == -1) {
            generateSandPatch(chunk, pos)
        }
    }

    /** Hollow obsidian tower with gold crown — Dark Castle landmark. */
    private fun generateObsidianTower(chunk: ChunkColumn, pos: ChunkPos) {
        // Find surface height at tower center
        val centerWorldX = pos.worldX + 7
        val centerWorldZ = pos.worldZ + 7
        val surfaceY = if (useNoise) getHeight(centerWorldX, centerWorldZ) else FLAT_SURFACE_Y
        val baseY = surfaceY + 1

        for (y in baseY..(baseY + 16)) {
            for (x in 6..9) {
                for (z in 6..9) {
                    if (x == 6 || x == 9 || z == 6 || z == 9) {
                        chunk.setBlock(x, y, z, BlockType.OBSIDIAN)
                    }
                }
            }
        }
        for (x in 6..9) {
            for (z in 6..9) {
                chunk.setBlock(x, baseY + 17, z, BlockType.GOLD_BLOCK)
            }
        }
    }

    /** Single quartz pillar. */
    private fun generateQuartzPillar(chunk: ChunkColumn, pos: ChunkPos) {
        val surfaceY = if (useNoise) getHeight(pos.worldX + 8, pos.worldZ + 8) else FLAT_SURFACE_Y
        val baseY = surfaceY + 1
        for (y in baseY..(baseY + 8)) {
            chunk.setBlock(8, y, 8, BlockType.QUARTZ)
        }
    }

    /** Blackstone wall across the chunk. */
    private fun generateBlackstoneWall(chunk: ChunkColumn, pos: ChunkPos) {
        for (x in 0..15) {
            val surfaceY = if (useNoise) getHeight(pos.worldX + x, pos.worldZ + 8) else FLAT_SURFACE_Y
            val baseY = surfaceY + 1
            for (y in baseY..(baseY + 3)) {
                chunk.setBlock(x, y, 8, BlockType.BLACKSTONE)
            }
        }
    }

    /** Sand patch replacing grass. */
    private fun generateSandPatch(chunk: ChunkColumn, pos: ChunkPos) {
        for (x in 4..11) {
            for (z in 4..11) {
                val surfaceY = if (useNoise) getHeight(pos.worldX + x, pos.worldZ + z) else FLAT_SURFACE_Y
                chunk.setBlock(x, surfaceY, z, BlockType.SAND)
            }
        }
    }
}
