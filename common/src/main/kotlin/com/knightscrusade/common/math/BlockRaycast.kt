package com.knightscrusade.common.math

import kotlin.math.floor
import kotlin.math.abs
import kotlin.math.sign

/**
 * Result of a block raycast: the hit block and the face that was hit.
 *
 * @param blockX World X coordinate of the hit block
 * @param blockY World Y coordinate of the hit block
 * @param blockZ World Z coordinate of the hit block
 * @param faceNormalX Normal X of the face that was hit (-1, 0, or 1)
 * @param faceNormalY Normal Y of the face that was hit (-1, 0, or 1)
 * @param faceNormalZ Normal Z of the face that was hit (-1, 0, or 1)
 * @param distance Distance from ray origin to hit point
 */
data class RaycastHit(
    val blockX: Int, val blockY: Int, val blockZ: Int,
    val faceNormalX: Int, val faceNormalY: Int, val faceNormalZ: Int,
    val distance: Float
) {
    /** The adjacent block position where a new block would be placed. */
    val placeX: Int get() = blockX + faceNormalX
    val placeY: Int get() = blockY + faceNormalY
    val placeZ: Int get() = blockZ + faceNormalZ
}

/**
 * Performs a voxel traversal (DDA algorithm) to find the first solid block
 * along a ray. This is the standard algorithm for block selection in voxel games.
 *
 * @param originX Ray origin X (typically eye position)
 * @param originY Ray origin Y
 * @param originZ Ray origin Z
 * @param dirX Normalized ray direction X
 * @param dirY Normalized ray direction Y
 * @param dirZ Normalized ray direction Z
 * @param maxDistance Maximum reach distance in blocks
 * @param isSolid Function that returns true if a block at (x, y, z) is solid
 * @return The hit result, or null if no block was hit within range
 */
fun blockRaycast(
    originX: Float, originY: Float, originZ: Float,
    dirX: Float, dirY: Float, dirZ: Float,
    maxDistance: Float = 5f,
    isSolid: (Int, Int, Int) -> Boolean
): RaycastHit? {
    // Current voxel position
    var x = floor(originX).toInt()
    var y = floor(originY).toInt()
    var z = floor(originZ).toInt()

    // Step direction
    val stepX = if (dirX > 0) 1 else if (dirX < 0) -1 else 0
    val stepY = if (dirY > 0) 1 else if (dirY < 0) -1 else 0
    val stepZ = if (dirZ > 0) 1 else if (dirZ < 0) -1 else 0

    // Distance along ray to cross one voxel boundary per axis
    val tDeltaX = if (dirX != 0f) abs(1f / dirX) else Float.MAX_VALUE
    val tDeltaY = if (dirY != 0f) abs(1f / dirY) else Float.MAX_VALUE
    val tDeltaZ = if (dirZ != 0f) abs(1f / dirZ) else Float.MAX_VALUE

    // Distance along ray to the next voxel boundary per axis
    var tMaxX = if (dirX > 0) (x + 1 - originX) * tDeltaX
    else if (dirX < 0) (originX - x) * tDeltaX
    else Float.MAX_VALUE

    var tMaxY = if (dirY > 0) (y + 1 - originY) * tDeltaY
    else if (dirY < 0) (originY - y) * tDeltaY
    else Float.MAX_VALUE

    var tMaxZ = if (dirZ > 0) (z + 1 - originZ) * tDeltaZ
    else if (dirZ < 0) (originZ - z) * tDeltaZ
    else Float.MAX_VALUE

    var faceNX = 0
    var faceNY = 0
    var faceNZ = 0
    var dist = 0f

    for (i in 0 until (maxDistance * 3).toInt()) {
        if (isSolid(x, y, z)) {
            return RaycastHit(x, y, z, faceNX, faceNY, faceNZ, dist)
        }

        // Advance to the nearest voxel boundary
        if (tMaxX < tMaxY) {
            if (tMaxX < tMaxZ) {
                dist = tMaxX
                if (dist > maxDistance) return null
                x += stepX
                tMaxX += tDeltaX
                faceNX = -stepX; faceNY = 0; faceNZ = 0
            } else {
                dist = tMaxZ
                if (dist > maxDistance) return null
                z += stepZ
                tMaxZ += tDeltaZ
                faceNX = 0; faceNY = 0; faceNZ = -stepZ
            }
        } else {
            if (tMaxY < tMaxZ) {
                dist = tMaxY
                if (dist > maxDistance) return null
                y += stepY
                tMaxY += tDeltaY
                faceNX = 0; faceNY = -stepY; faceNZ = 0
            } else {
                dist = tMaxZ
                if (dist > maxDistance) return null
                z += stepZ
                tMaxZ += tDeltaZ
                faceNX = 0; faceNY = 0; faceNZ = -stepZ
            }
        }
    }

    return null
}
