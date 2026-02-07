package com.knightscrusade.common.chunk

import com.knightscrusade.common.block.BlockType

/**
 * 16x16x16 block storage unit. Uses a flat ShortArray for M0 prototype.
 * Index formula: y * 256 + z * 16 + x
 */
class SubChunk {

    private val blocks = ShortArray(SIZE * SIZE * SIZE)

    fun getBlock(x: Int, y: Int, z: Int): BlockType {
        return BlockType.fromId(blocks[index(x, y, z)].toInt())
    }

    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) {
        blocks[index(x, y, z)] = type.id.toShort()
    }

    fun getBlockId(x: Int, y: Int, z: Int): Int {
        return blocks[index(x, y, z)].toInt()
    }

    fun isEmpty(): Boolean = blocks.all { it.toInt() == 0 }

    /** Raw block data for serialization */
    fun getRawData(): ShortArray = blocks.copyOf()

    fun setRawData(data: ShortArray) {
        require(data.size == blocks.size) { "Data size mismatch: expected ${blocks.size}, got ${data.size}" }
        System.arraycopy(data, 0, blocks, 0, blocks.size)
    }

    private fun index(x: Int, y: Int, z: Int): Int {
        require(x in 0 until SIZE && y in 0 until SIZE && z in 0 until SIZE) {
            "Block coordinates out of range: ($x, $y, $z)"
        }
        return y * SIZE * SIZE + z * SIZE + x
    }

    companion object {
        const val SIZE = 16
    }
}
