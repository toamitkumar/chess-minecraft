package com.knightscrusade.common.math

/**
 * Axis-Aligned Bounding Box for collision detection.
 *
 * Defined by minimum and maximum corners. Immutable — operations
 * return new AABB instances.
 */
data class AABB(
    val minX: Double, val minY: Double, val minZ: Double,
    val maxX: Double, val maxY: Double, val maxZ: Double
) {
    /** Width along X axis. */
    val width: Double get() = maxX - minX

    /** Height along Y axis. */
    val height: Double get() = maxY - minY

    /** Depth along Z axis. */
    val depth: Double get() = maxZ - minZ

    /** Center point of the AABB. */
    val centerX: Double get() = (minX + maxX) / 2.0
    val centerY: Double get() = (minY + maxY) / 2.0
    val centerZ: Double get() = (minZ + maxZ) / 2.0

    /**
     * Create an AABB from an entity's feet position and collision dimensions.
     */
    companion object {
        fun fromEntity(x: Double, y: Double, z: Double, halfWidth: Double, height: Double): AABB {
            return AABB(
                x - halfWidth, y, z - halfWidth,
                x + halfWidth, y + height, z + halfWidth
            )
        }

        /** AABB for a single block at grid position (bx, by, bz). */
        fun block(bx: Int, by: Int, bz: Int): AABB {
            return AABB(
                bx.toDouble(), by.toDouble(), bz.toDouble(),
                bx + 1.0, by + 1.0, bz + 1.0
            )
        }
    }

    /** Offset this AABB by a delta. */
    fun offset(dx: Double, dy: Double, dz: Double): AABB {
        return AABB(minX + dx, minY + dy, minZ + dz, maxX + dx, maxY + dy, maxZ + dz)
    }

    /** Expand this AABB by an amount in all directions. */
    fun expand(dx: Double, dy: Double, dz: Double): AABB {
        var newMinX = minX; var newMinY = minY; var newMinZ = minZ
        var newMaxX = maxX; var newMaxY = maxY; var newMaxZ = maxZ
        if (dx < 0) newMinX += dx else newMaxX += dx
        if (dy < 0) newMinY += dy else newMaxY += dy
        if (dz < 0) newMinZ += dz else newMaxZ += dz
        return AABB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
    }

    /** Check if this AABB intersects another. */
    fun intersects(other: AABB): Boolean {
        return maxX > other.minX && minX < other.maxX &&
                maxY > other.minY && minY < other.maxY &&
                maxZ > other.minZ && minZ < other.maxZ
    }

    /**
     * Clip the Y movement of this AABB against another along the Y axis.
     * Returns the clipped Y offset.
     */
    fun clipYCollide(other: AABB, dy: Double): Double {
        if (maxX <= other.minX || minX >= other.maxX) return dy
        if (maxZ <= other.minZ || minZ >= other.maxZ) return dy

        if (dy > 0 && maxY <= other.minY) {
            val clip = other.minY - maxY
            if (clip < dy) return clip
        }
        if (dy < 0 && minY >= other.maxY) {
            val clip = other.maxY - minY
            if (clip > dy) return clip
        }
        return dy
    }

    /**
     * Clip the X movement of this AABB against another along the X axis.
     * Returns the clipped X offset.
     */
    fun clipXCollide(other: AABB, dx: Double): Double {
        if (maxY <= other.minY || minY >= other.maxY) return dx
        if (maxZ <= other.minZ || minZ >= other.maxZ) return dx

        if (dx > 0 && maxX <= other.minX) {
            val clip = other.minX - maxX
            if (clip < dx) return clip
        }
        if (dx < 0 && minX >= other.maxX) {
            val clip = other.maxX - minX
            if (clip > dx) return clip
        }
        return dx
    }

    /**
     * Clip the Z movement of this AABB against another along the Z axis.
     * Returns the clipped Z offset.
     */
    fun clipZCollide(other: AABB, dz: Double): Double {
        if (maxX <= other.minX || minX >= other.maxX) return dz
        if (maxY <= other.minY || minY >= other.maxY) return dz

        if (dz > 0 && maxZ <= other.minZ) {
            val clip = other.minZ - maxZ
            if (clip < dz) return clip
        }
        if (dz < 0 && minZ >= other.maxZ) {
            val clip = other.maxZ - minZ
            if (clip > dz) return clip
        }
        return dz
    }
}
