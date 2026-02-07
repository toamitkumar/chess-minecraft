package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component

/**
 * Tracks L-Leap ability state for the Knight entity.
 *
 * The L-Leap is the Knight's signature chess move: a ballistic arc
 * following an L-pattern (2 blocks + 1 block perpendicular).
 *
 * @param cooldownSeconds Total cooldown duration
 * @param remainingCooldown Time until L-Leap is available again
 * @param leaping Whether the entity is currently mid-leap
 * @param leapTime Elapsed time within the current leap arc
 * @param leapDuration Total duration of the leap animation
 * @param startX Starting X position of the leap
 * @param startY Starting Y position of the leap
 * @param startZ Starting Z position of the leap
 * @param targetX Target X position of the leap
 * @param targetY Target Y position of the leap
 * @param targetZ Target Z position of the leap
 */
data class LeapComponent(
    val cooldownSeconds: Float = 3.0f,
    var remainingCooldown: Float = 0f,
    var leaping: Boolean = false,
    var leapTime: Float = 0f,
    val leapDuration: Float = 0.5f,
    var startX: Double = 0.0,
    var startY: Double = 0.0,
    var startZ: Double = 0.0,
    var targetX: Double = 0.0,
    var targetY: Double = 0.0,
    var targetZ: Double = 0.0
) : Component {
    /** Whether the leap ability is ready to use. */
    val ready: Boolean get() = !leaping && remainingCooldown <= 0f
}
