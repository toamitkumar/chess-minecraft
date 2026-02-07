package com.knightscrusade.common.chunk

/**
 * Chunk coordinates in chunk-space (each chunk is 16x16 blocks).
 * World block position = chunkPos * 16 + localPos
 */
data class ChunkPos(val x: Int, val z: Int) {

    /** World-space X of the chunk's origin (west edge) */
    val worldX: Int get() = x * 16

    /** World-space Z of the chunk's origin (north edge) */
    val worldZ: Int get() = z * 16

    companion object {
        /** Convert world block coordinates to chunk coordinates */
        fun fromWorldPos(worldX: Int, worldZ: Int): ChunkPos {
            return ChunkPos(
                if (worldX < 0) (worldX + 1) / 16 - 1 else worldX / 16,
                if (worldZ < 0) (worldZ + 1) / 16 - 1 else worldZ / 16
            )
        }
    }
}
