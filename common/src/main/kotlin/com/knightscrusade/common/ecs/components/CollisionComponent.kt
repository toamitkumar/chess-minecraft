package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component

/**
 * Collision volume for an entity, defined as an axis-aligned bounding box
 * relative to the entity's feet position.
 *
 * The AABB is centered on the entity's X/Z position, and extends from
 * the entity's Y position (feet) upward by [height].
 *
 * @param width Entity width in blocks (AABB extends +/- width/2 on X and Z)
 * @param height Entity height in blocks (AABB extends from feet Y upward)
 * @param onGround Whether the entity is currently standing on solid ground
 */
data class CollisionComponent(
    val width: Float = 0.6f,
    val height: Float = 1.8f,
    var onGround: Boolean = false
) : Component {
    /** Half-width for AABB calculations. */
    val halfWidth: Float get() = width / 2f
}
