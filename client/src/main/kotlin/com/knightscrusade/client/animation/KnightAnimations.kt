package com.knightscrusade.client.animation

import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

/**
 * Programmatic animation clip definitions for the Knight entity.
 *
 * Creates IDLE, WALK, and RUN clips with keyframes that animate
 * leg and arm bones. Uses sine-wave-style limb swings for natural movement.
 */
object KnightAnimations {

    /**
     * Create idle animation: subtle breathing/swaying motion.
     * 4 keyframes over 2 seconds.
     */
    fun createIdleClip(): AnimationClip {
        val clip = AnimationClip("idle", duration = 2.0f, looping = true)

        // Body gentle bob
        clip.addKeyframe(Keyframe(0.0f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(0.5f, "body", position = Vector3f(0f, 0.51f, 0f)))
        clip.addKeyframe(Keyframe(1.0f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(1.5f, "body", position = Vector3f(0f, 0.49f, 0f)))

        // Head slight look around
        clip.addKeyframe(Keyframe(0.0f, "head", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(1.0f, "head", rotation = rotX(3f)))
        clip.addKeyframe(Keyframe(2.0f, "head", rotation = rotX(0f)))

        return clip.finalize()
    }

    /**
     * Create walk animation: alternating leg/arm swings.
     * 8 keyframes over 0.8 seconds.
     */
    fun createWalkClip(): AnimationClip {
        val clip = AnimationClip("walk", duration = 0.8f, looping = true)
        val swingAngle = 25f

        // Front legs alternate
        clip.addKeyframe(Keyframe(0.0f, "leg_front_left", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_front_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_front_left", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.6f, "leg_front_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.8f, "leg_front_left", rotation = rotX(swingAngle)))

        clip.addKeyframe(Keyframe(0.0f, "leg_front_right", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_front_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_front_right", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.6f, "leg_front_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.8f, "leg_front_right", rotation = rotX(-swingAngle)))

        // Back legs opposite to front
        clip.addKeyframe(Keyframe(0.0f, "leg_back_left", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_back_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_back_left", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.6f, "leg_back_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.8f, "leg_back_left", rotation = rotX(-swingAngle)))

        clip.addKeyframe(Keyframe(0.0f, "leg_back_right", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_back_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_back_right", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.6f, "leg_back_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.8f, "leg_back_right", rotation = rotX(swingAngle)))

        // Arms swing opposite to legs
        clip.addKeyframe(Keyframe(0.0f, "arm_left", rotation = rotX(-15f)))
        clip.addKeyframe(Keyframe(0.4f, "arm_left", rotation = rotX(15f)))
        clip.addKeyframe(Keyframe(0.8f, "arm_left", rotation = rotX(-15f)))

        clip.addKeyframe(Keyframe(0.0f, "arm_right", rotation = rotX(15f)))
        clip.addKeyframe(Keyframe(0.4f, "arm_right", rotation = rotX(-15f)))
        clip.addKeyframe(Keyframe(0.8f, "arm_right", rotation = rotX(15f)))

        // Body slight bounce
        clip.addKeyframe(Keyframe(0.0f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(0.2f, "body", position = Vector3f(0f, 0.52f, 0f)))
        clip.addKeyframe(Keyframe(0.4f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(0.6f, "body", position = Vector3f(0f, 0.52f, 0f)))
        clip.addKeyframe(Keyframe(0.8f, "body", position = Vector3f(0f, 0.5f, 0f)))

        return clip.finalize()
    }

    /**
     * Create run animation: faster, wider swings.
     * 6 keyframes over 0.4 seconds.
     */
    fun createRunClip(): AnimationClip {
        val clip = AnimationClip("run", duration = 0.4f, looping = true)
        val swingAngle = 40f

        // Front legs — faster, wider
        clip.addKeyframe(Keyframe(0.0f, "leg_front_left", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.1f, "leg_front_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.2f, "leg_front_left", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.3f, "leg_front_left", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_front_left", rotation = rotX(swingAngle)))

        clip.addKeyframe(Keyframe(0.0f, "leg_front_right", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.1f, "leg_front_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.2f, "leg_front_right", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.3f, "leg_front_right", rotation = rotX(0f)))
        clip.addKeyframe(Keyframe(0.4f, "leg_front_right", rotation = rotX(-swingAngle)))

        // Back legs
        clip.addKeyframe(Keyframe(0.0f, "leg_back_left", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_back_left", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.4f, "leg_back_left", rotation = rotX(-swingAngle)))

        clip.addKeyframe(Keyframe(0.0f, "leg_back_right", rotation = rotX(swingAngle)))
        clip.addKeyframe(Keyframe(0.2f, "leg_back_right", rotation = rotX(-swingAngle)))
        clip.addKeyframe(Keyframe(0.4f, "leg_back_right", rotation = rotX(swingAngle)))

        // Arms
        clip.addKeyframe(Keyframe(0.0f, "arm_left", rotation = rotX(-25f)))
        clip.addKeyframe(Keyframe(0.2f, "arm_left", rotation = rotX(25f)))
        clip.addKeyframe(Keyframe(0.4f, "arm_left", rotation = rotX(-25f)))

        clip.addKeyframe(Keyframe(0.0f, "arm_right", rotation = rotX(25f)))
        clip.addKeyframe(Keyframe(0.2f, "arm_right", rotation = rotX(-25f)))
        clip.addKeyframe(Keyframe(0.4f, "arm_right", rotation = rotX(25f)))

        // More body bounce
        clip.addKeyframe(Keyframe(0.0f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(0.1f, "body", position = Vector3f(0f, 0.54f, 0f)))
        clip.addKeyframe(Keyframe(0.2f, "body", position = Vector3f(0f, 0.5f, 0f)))
        clip.addKeyframe(Keyframe(0.3f, "body", position = Vector3f(0f, 0.54f, 0f)))
        clip.addKeyframe(Keyframe(0.4f, "body", position = Vector3f(0f, 0.5f, 0f)))

        return clip.finalize()
    }

    /**
     * Create a Quaternion rotation around the X axis (pitch) from degrees.
     */
    private fun rotX(degrees: Float): Quaternionf {
        return Quaternionf().rotateX(Math.toRadians(degrees.toDouble()).toFloat())
    }
}
