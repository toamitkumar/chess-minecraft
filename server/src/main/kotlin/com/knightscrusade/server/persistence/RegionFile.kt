package com.knightscrusade.server.persistence

import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import com.knightscrusade.common.chunk.SubChunk
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

/**
 * Anvil-like region file format for chunk persistence.
 *
 * Each region file stores a 32x32 grid of chunks. The file has a fixed
 * header (4 bytes per slot = 4096 bytes) that maps chunk positions to
 * file offsets, followed by variable-length compressed chunk data.
 *
 * File format:
 * ```
 * [Header: 32*32*4 bytes — offset/length pairs per chunk slot]
 * [Chunk data: zlib-compressed SubChunk raw data]
 * ```
 *
 * @param regionDir Directory where region files are stored
 * @param regionX Region X coordinate (region = chunk / 32)
 * @param regionZ Region Z coordinate
 */
class RegionFile(
    private val regionDir: Path,
    val regionX: Int,
    val regionZ: Int
) {
    private val logger = LoggerFactory.getLogger(RegionFile::class.java)

    companion object {
        /** Chunks per region side. */
        const val REGION_SIZE = 32
        /** Number of chunk slots in the header. */
        const val HEADER_ENTRIES = REGION_SIZE * REGION_SIZE
        /** Size of header in bytes (4 bytes per entry). */
        const val HEADER_SIZE = HEADER_ENTRIES * 4

        /** Get region coordinates from a chunk position. */
        fun regionCoords(chunkPos: ChunkPos): Pair<Int, Int> {
            val rx = if (chunkPos.x >= 0) chunkPos.x / REGION_SIZE else (chunkPos.x - REGION_SIZE + 1) / REGION_SIZE
            val rz = if (chunkPos.z >= 0) chunkPos.z / REGION_SIZE else (chunkPos.z - REGION_SIZE + 1) / REGION_SIZE
            return Pair(rx, rz)
        }
    }

    private val filePath: Path = regionDir.resolve("r.$regionX.$regionZ.region")

    /**
     * Save a chunk to the region file.
     *
     * The chunk is serialized and zlib-compressed, then written
     * at the next available position. The header is updated to
     * point to the new data.
     */
    fun saveChunk(chunk: ChunkColumn) {
        Files.createDirectories(regionDir)

        val localX = ((chunk.pos.x % REGION_SIZE) + REGION_SIZE) % REGION_SIZE
        val localZ = ((chunk.pos.z % REGION_SIZE) + REGION_SIZE) % REGION_SIZE

        // Serialize chunk data
        val chunkData = serializeChunk(chunk)
        val compressed = compress(chunkData)

        // Read existing header (or create new)
        val header = readOrCreateHeader()

        // Write compressed data at end of file
        val raf = RandomAccessFile(filePath.toFile(), "rw")
        try {
            val dataOffset = if (raf.length() < HEADER_SIZE) HEADER_SIZE.toLong() else raf.length()
            raf.seek(dataOffset)
            raf.writeInt(compressed.size)
            raf.write(compressed)

            // Update header: store offset in the slot
            val headerIndex = localZ * REGION_SIZE + localX
            header[headerIndex] = dataOffset.toInt()

            // Write header
            raf.seek(0)
            for (offset in header) {
                raf.writeInt(offset)
            }
        } finally {
            raf.close()
        }
    }

    /**
     * Load a chunk from the region file.
     *
     * @param pos Chunk position to load
     * @return The loaded chunk, or null if not present in the file
     */
    fun loadChunk(pos: ChunkPos): ChunkColumn? {
        if (!Files.exists(filePath)) return null

        val localX = ((pos.x % REGION_SIZE) + REGION_SIZE) % REGION_SIZE
        val localZ = ((pos.z % REGION_SIZE) + REGION_SIZE) % REGION_SIZE
        val headerIndex = localZ * REGION_SIZE + localX

        val raf = RandomAccessFile(filePath.toFile(), "r")
        try {
            if (raf.length() < HEADER_SIZE) return null

            // Read offset from header
            raf.seek((headerIndex * 4).toLong())
            val dataOffset = raf.readInt()
            if (dataOffset == 0) return null // Not saved yet

            // Read compressed data
            raf.seek(dataOffset.toLong())
            val compressedSize = raf.readInt()
            val compressed = ByteArray(compressedSize)
            raf.readFully(compressed)

            // Decompress and deserialize
            val chunkData = decompress(compressed)
            return deserializeChunk(pos, chunkData)
        } catch (e: Exception) {
            logger.error("Failed to load chunk $pos from region file", e)
            return null
        } finally {
            raf.close()
        }
    }

    /**
     * Check if a chunk exists in this region file.
     */
    fun hasChunk(pos: ChunkPos): Boolean {
        if (!Files.exists(filePath)) return false

        val localX = ((pos.x % REGION_SIZE) + REGION_SIZE) % REGION_SIZE
        val localZ = ((pos.z % REGION_SIZE) + REGION_SIZE) % REGION_SIZE
        val headerIndex = localZ * REGION_SIZE + localX

        val raf = RandomAccessFile(filePath.toFile(), "r")
        return try {
            if (raf.length() < HEADER_SIZE) return false
            raf.seek((headerIndex * 4).toLong())
            raf.readInt() != 0
        } finally {
            raf.close()
        }
    }

    private fun readOrCreateHeader(): IntArray {
        val header = IntArray(HEADER_ENTRIES)
        if (Files.exists(filePath)) {
            val raf = RandomAccessFile(filePath.toFile(), "r")
            try {
                if (raf.length() >= HEADER_SIZE) {
                    for (i in header.indices) {
                        header[i] = raf.readInt()
                    }
                }
            } finally {
                raf.close()
            }
        }
        return header
    }

    /**
     * Serialize a chunk's block data to a byte array.
     * Format: 16 sub-chunks * 4096 shorts = 131072 shorts = 262144 bytes.
     */
    private fun serializeChunk(chunk: ChunkColumn): ByteArray {
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        for (subIndex in 0 until ChunkColumn.NUM_SUB_CHUNKS) {
            val rawData = chunk.getSubChunk(subIndex).getRawData()
            for (s in rawData) {
                dos.writeShort(s.toInt())
            }
        }
        dos.flush()
        return baos.toByteArray()
    }

    /**
     * Deserialize a chunk from a byte array.
     */
    private fun deserializeChunk(pos: ChunkPos, data: ByteArray): ChunkColumn {
        val chunk = ChunkColumn(pos)
        val dis = DataInputStream(ByteArrayInputStream(data))
        for (subIndex in 0 until ChunkColumn.NUM_SUB_CHUNKS) {
            val rawData = ShortArray(SubChunk.SIZE * SubChunk.SIZE * SubChunk.SIZE)
            for (i in rawData.indices) {
                rawData[i] = dis.readShort()
            }
            chunk.getSubChunk(subIndex).setRawData(rawData)
        }
        return chunk
    }

    private fun compress(data: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        DeflaterOutputStream(baos).use { it.write(data) }
        return baos.toByteArray()
    }

    private fun decompress(data: ByteArray): ByteArray {
        return InflaterInputStream(ByteArrayInputStream(data)).readAllBytes()
    }
}
