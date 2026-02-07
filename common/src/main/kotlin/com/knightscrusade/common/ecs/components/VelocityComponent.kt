package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component

/**
 * Linear velocity of an entity in blocks per second.
 *
 * Modified by the MovementSystem based on input and physics.
 * Gravity applies to vy each tick when the entity is not on the ground.
 */
data class VelocityComponent(
    var vx: Double = 0.0,
    var vy: Double = 0.0,
    var vz: Double = 0.0
) : Component {
    /** Horizontal speed (XZ plane). */
    fun horizontalSpeed(): Double = kotlin.math.sqrt(vx * vx + vz * vz)
}
