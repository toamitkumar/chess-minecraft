package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.GameSystem
import com.knightscrusade.common.ecs.components.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Processes player input and applies movement physics.
 *
 * Reads [InputComponent] flags to compute velocity, applies gravity,
 * and handles jump initiation. Runs before [CollisionSystem] in the
 * tick order so that collision can resolve any overlaps.
 */
class MovementSystem : GameSystem {

    companion object {
        /** Walking speed in blocks per second. */
        const val WALK_SPEED = 4.317
        /** Sprint multiplier. */
        const val SPRINT_MULTIPLIER = 1.3
        /** Jump impulse in blocks per second. */
        const val JUMP_IMPULSE = 8.0
        /** Gravitational acceleration in blocks per second squared. */
        const val GRAVITY = -28.0
        /** Maximum downward velocity (terminal velocity). */
        const val TERMINAL_VELOCITY = -78.4
        /** Drag factor applied each tick to horizontal velocity. */
        const val GROUND_DRAG = 0.6
        const val AIR_DRAG = 0.91
    }

    override fun update(world: EcsWorld, deltaTime: Float) {
        val entities = world.query(
            PositionComponent::class.java,
            VelocityComponent::class.java,
            InputComponent::class.java,
            CollisionComponent::class.java
        )

        for (entity in entities) {
            val pos = entity.get(PositionComponent::class.java)!!
            val vel = entity.get(VelocityComponent::class.java)!!
            val input = entity.get(InputComponent::class.java)!!
            val collision = entity.get(CollisionComponent::class.java)!!
            val leap = entity.get(LeapComponent::class.java)

            // Skip movement processing during an active leap
            if (leap != null && leap.leaping) continue

            // Calculate movement direction from yaw
            val yawRad = Math.toRadians(input.yaw.toDouble())
            val sinYaw = sin(yawRad)
            val cosYaw = cos(yawRad)

            // Forward is -Z direction (Minecraft convention)
            var moveX = 0.0
            var moveZ = 0.0

            if (input.forward) { moveX -= sinYaw; moveZ += cosYaw }
            if (input.backward) { moveX += sinYaw; moveZ -= cosYaw }
            if (input.left) { moveX += cosYaw; moveZ += sinYaw }
            if (input.right) { moveX -= cosYaw; moveZ -= sinYaw }

            // Normalize diagonal movement
            val moveMag = kotlin.math.sqrt(moveX * moveX + moveZ * moveZ)
            if (moveMag > 0.001) {
                moveX /= moveMag
                moveZ /= moveMag
            }

            // Apply speed
            val speed = if (input.sprint) WALK_SPEED * SPRINT_MULTIPLIER else WALK_SPEED
            val acceleration = if (collision.onGround) speed * 0.98 else speed * 0.02

            vel.vx += moveX * acceleration * deltaTime
            vel.vz += moveZ * acceleration * deltaTime

            // Jump
            if (input.jump && collision.onGround) {
                vel.vy = JUMP_IMPULSE
                collision.onGround = false
            }

            // Gravity
            vel.vy += GRAVITY * deltaTime
            if (vel.vy < TERMINAL_VELOCITY) vel.vy = TERMINAL_VELOCITY

            // Apply drag
            val drag = if (collision.onGround) GROUND_DRAG else AIR_DRAG
            vel.vx *= drag
            vel.vz *= drag

            // Update yaw/pitch from input
            pos.yaw = input.yaw
            pos.pitch = input.pitch
        }
    }
}
