package com.knightscrusade.client.animation

import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * A named animation clip containing keyframes for multiple bones.
 *
 * Clips are sampled at a given time to produce bone transforms.
 * Supports looping and linear interpolation between keyframes.
 *
 * @param name Clip name (e.g. "idle", "walk", "run")
 * @param duration Total duration in seconds
 * @param looping Whether the clip loops
 */
class AnimationClip(
    val name: String,
    val duration: Float,
    val looping: Boolean = true
) {
    /** Keyframes grouped by bone name, sorted by time. */
    private val keyframesByBone = mutableMapOf<String, MutableList<Keyframe>>()

    /**
     * Add a keyframe to this clip.
     */
    fun addKeyframe(keyframe: Keyframe): AnimationClip {
        keyframesByBone.getOrPut(keyframe.boneName) { mutableListOf() }
            .add(keyframe)
        return this
    }

    /**
     * Sort all keyframes by time (call after adding all keyframes).
     */
    fun finalize(): AnimationClip {
        keyframesByBone.values.forEach { frames -> frames.sortBy { it.time } }
        return this
    }

    /**
     * Sample this clip at the given time and apply to the skeleton.
     *
     * @param skeleton The skeleton to apply bone transforms to
     * @param time Current time within the clip (will be wrapped if looping)
     * @param weight Blend weight (0.0 = no effect, 1.0 = full effect)
     */
    fun sample(skeleton: Skeleton, time: Float, weight: Float = 1f) {
        val t = if (looping && duration > 0f) time % duration else time.coerceAtMost(duration)

        for ((boneName, keyframes) in keyframesByBone) {
            val bone = skeleton.getBone(boneName) ?: continue
            if (keyframes.isEmpty()) continue

            // Find surrounding keyframes
            val (prev, next) = findSurroundingKeyframes(keyframes, t)
            val segmentDuration = next.time - prev.time
            val localT = if (segmentDuration > 0f) (t - prev.time) / segmentDuration else 0f

            // Interpolate position
            if (prev.position != null && next.position != null) {
                val interpPos = Vector3f()
                prev.position.lerp(next.position, localT, interpPos)
                if (weight >= 1f) {
                    bone.localPosition.set(interpPos)
                } else {
                    bone.localPosition.lerp(interpPos, weight)
                }
            } else if (prev.position != null) {
                if (weight >= 1f) {
                    bone.localPosition.set(prev.position)
                } else {
                    bone.localPosition.lerp(prev.position, weight)
                }
            }

            // Interpolate rotation (slerp)
            if (prev.rotation != null && next.rotation != null) {
                val interpRot = Quaternionf()
                prev.rotation.slerp(next.rotation, localT, interpRot)
                if (weight >= 1f) {
                    bone.localRotation.set(interpRot)
                } else {
                    bone.localRotation.slerp(interpRot, weight)
                }
            } else if (prev.rotation != null) {
                if (weight >= 1f) {
                    bone.localRotation.set(prev.rotation)
                } else {
                    bone.localRotation.slerp(prev.rotation, weight)
                }
            }
        }
    }

    /**
     * Get all bone names that have keyframes in this clip.
     */
    fun getAnimatedBones(): Set<String> = keyframesByBone.keys

    private fun findSurroundingKeyframes(keyframes: List<Keyframe>, time: Float): Pair<Keyframe, Keyframe> {
        if (keyframes.size == 1) return Pair(keyframes[0], keyframes[0])

        for (i in 0 until keyframes.size - 1) {
            if (time >= keyframes[i].time && time <= keyframes[i + 1].time) {
                return Pair(keyframes[i], keyframes[i + 1])
            }
        }

        // Past end — return last keyframe
        val last = keyframes.last()
        return if (looping) Pair(last, keyframes.first()) else Pair(last, last)
    }
}
