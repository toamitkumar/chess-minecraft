package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component

/**
 * Input state for a player-controlled entity.
 *
 * Updated from incoming MovePackets on the server. The MovementSystem
 * reads these flags to compute velocity.
 */
data class InputComponent(
    var forward: Boolean = false,
    var backward: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false,
    var jump: Boolean = false,
    var sprint: Boolean = false,
    var lLeap: Boolean = false,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var lastSequence: Long = 0
) : Component
