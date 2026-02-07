package com.knightscrusade.client.animation

import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * A single keyframe in an animation clip.
 *
 * Stores the target position and rotation for a specific bone
 * at a specific time. Between keyframes, values are interpolated
 * (lerp for position, slerp for rotation).
 *
 * @param time Time offset in seconds from the start of the clip
 * @param boneName Name of the bone this keyframe affects
 * @param position Target position (relative to parent)
 * @param rotation Target rotation (relative to parent)
 */
data class Keyframe(
    val time: Float,
    val boneName: String,
    val position: Vector3f? = null,
    val rotation: Quaternionf? = null
)
