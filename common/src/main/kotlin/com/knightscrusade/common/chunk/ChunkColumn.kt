package com.knightscrusade.common.chunk

import com.knightscrusade.common.block.BlockType

/**
 * A full-height column of sub-chunks at a given (chunkX, chunkZ) position.
 * For M0: Y range 0..255 = 16 sub-chunks.
 */
class ChunkColumn(val pos: ChunkPos) {

    private val subChunks = Array(NUM_SUB_CHUNKS) { SubChunk() }

    var dirty: Boolean = false
        private set

    fun markDirty() { dirty = true }
    fun markClean() { dirty = false }

    fun getBlock(x: Int, y: Int, z: Int): BlockType {
        if (y < 0 || y >= HEIGHT) return BlockType.AIR
        val subIndex = y / SubChunk.SIZE
        val localY = y % SubChunk.SIZE
        return subChunks[subIndex].getBlock(x, localY, z)
    }

    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) {
        if (y < 0 || y >= HEIGHT) return
        val subIndex = y / SubChunk.SIZE
        val localY = y % SubChunk.SIZE
        subChunks[subIndex].setBlock(x, localY, z, type)
        dirty = true
    }

    fun getSubChunk(index: Int): SubChunk {
        require(index in 0 until NUM_SUB_CHUNKS) { "SubChunk index out of range: $index" }
        return subChunks[index]
    }

    companion object {
        const val NUM_SUB_CHUNKS = 16
        const val HEIGHT = NUM_SUB_CHUNKS * SubChunk.SIZE // 256
    }
}
