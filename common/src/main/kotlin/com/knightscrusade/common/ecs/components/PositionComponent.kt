package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component

/**
 * Position and orientation of an entity in the world.
 *
 * Coordinates use the same system as Minecraft: X/Z horizontal, Y vertical.
 * Yaw is rotation around Y-axis (0 = +Z, 90 = -X), pitch is vertical look angle.
 */
data class PositionComponent(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0f,
    var pitch: Float = 0f
) : Component
