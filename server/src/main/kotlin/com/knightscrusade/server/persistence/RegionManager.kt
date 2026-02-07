package com.knightscrusade.server.persistence

import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.ChunkPos
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages region files for chunk persistence.
 *
 * Provides a simple API for saving and loading chunks, automatically
 * routing requests to the correct region file based on chunk coordinates.
 * Region files are cached to avoid repeated file handles.
 *
 * @param worldDir Directory for world data (region files stored in worldDir/region/)
 */
class RegionManager(private val worldDir: Path) {

    private val logger = LoggerFactory.getLogger(RegionManager::class.java)
    private val regionDir = worldDir.resolve("region")
    private val regions = ConcurrentHashMap<Pair<Int, Int>, RegionFile>()

    /**
     * Save a chunk to disk.
     */
    fun saveChunk(chunk: ChunkColumn) {
        val region = getRegion(chunk.pos)
        region.saveChunk(chunk)
    }

    /**
     * Load a chunk from disk.
     * @return The loaded chunk, or null if not saved
     */
    fun loadChunk(pos: ChunkPos): ChunkColumn? {
        val region = getRegion(pos)
        return region.loadChunk(pos)
    }

    /**
     * Check if a chunk has been saved to disk.
     */
    fun hasSavedChunk(pos: ChunkPos): Boolean {
        val region = getRegion(pos)
        return region.hasChunk(pos)
    }

    /**
     * Save all dirty chunks from the provided map.
     * @return Number of chunks saved
     */
    fun saveAllDirty(chunks: Map<ChunkPos, ChunkColumn>): Int {
        var saved = 0
        for ((_, chunk) in chunks) {
            if (chunk.dirty) {
                saveChunk(chunk)
                chunk.markClean()
                saved++
            }
        }
        if (saved > 0) {
            logger.info("Auto-saved $saved dirty chunks")
        }
        return saved
    }

    private fun getRegion(pos: ChunkPos): RegionFile {
        val (rx, rz) = RegionFile.regionCoords(pos)
        return regions.getOrPut(Pair(rx, rz)) {
            RegionFile(regionDir, rx, rz)
        }
    }
}
