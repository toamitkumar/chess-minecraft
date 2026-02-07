package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.GameSystem
import com.knightscrusade.common.ecs.components.*
import com.knightscrusade.server.world.ServerWorld
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Handles the Knight's L-Leap ability — a chess-inspired ballistic leap.
 *
 * The L-Leap moves the Knight in an L-pattern: 2 blocks forward + 1 block
 * to the right (following the closest cardinal direction from the player's
 * facing). The trajectory follows a parabolic arc over [LeapComponent.leapDuration].
 *
 * Server validates that the target position is clear before initiating the leap.
 *
 * @param serverWorld Used to check if target position is valid
 */
class LeapSystem(private val serverWorld: ServerWorld) : GameSystem {

    companion object {
        /** Height of the parabolic arc apex above start/end. */
        const val ARC_HEIGHT = 3.0
    }

    override fun update(world: EcsWorld, deltaTime: Float) {
        val entities = world.query(
            PositionComponent::class.java,
            VelocityComponent::class.java,
            CollisionComponent::class.java,
            InputComponent::class.java,
            LeapComponent::class.java
        )

        for (entity in entities) {
            val pos = entity.get(PositionComponent::class.java)!!
            val vel = entity.get(VelocityComponent::class.java)!!
            val collision = entity.get(CollisionComponent::class.java)!!
            val input = entity.get(InputComponent::class.java)!!
            val leap = entity.get(LeapComponent::class.java)!!

            // Tick cooldown
            if (leap.remainingCooldown > 0f) {
                leap.remainingCooldown = (leap.remainingCooldown - deltaTime).coerceAtLeast(0f)
            }

            if (leap.leaping) {
                // Advance the leap animation
                leap.leapTime += deltaTime
                val t = (leap.leapTime / leap.leapDuration).coerceIn(0f, 1f)

                // Lerp position along XZ
                pos.x = lerp(leap.startX, leap.targetX, t.toDouble())
                pos.z = lerp(leap.startZ, leap.targetZ, t.toDouble())

                // Parabolic arc for Y: y = start + arc * 4t(1-t)
                val baseY = lerp(leap.startY, leap.targetY, t.toDouble())
                pos.y = baseY + ARC_HEIGHT * 4.0 * t * (1.0 - t)

                // Zero velocity during leap
                vel.vx = 0.0
                vel.vy = 0.0
                vel.vz = 0.0

                // Finish leap
                if (t >= 1f) {
                    leap.leaping = false
                    leap.remainingCooldown = leap.cooldownSeconds
                    collision.onGround = true
                }

            } else if (input.lLeap && leap.ready && collision.onGround) {
                // Initiate a new leap
                val target = computeLeapTarget(pos, input)
                if (isTargetClear(target, collision)) {
                    leap.startX = pos.x
                    leap.startY = pos.y
                    leap.startZ = pos.z
                    leap.targetX = target.first
                    leap.targetY = target.second
                    leap.targetZ = target.third
                    leap.leaping = true
                    leap.leapTime = 0f
                    collision.onGround = false
                }
            }
        }
    }

    /**
     * Compute the L-shaped target position:
     * 2 blocks in the nearest cardinal direction from yaw, then 1 block perpendicular (right).
     */
    private fun computeLeapTarget(pos: PositionComponent, input: InputComponent): Triple<Double, Double, Double> {
        // Snap yaw to nearest cardinal direction
        val yaw = ((input.yaw % 360f) + 360f) % 360f
        val cardinal = ((yaw + 45f) / 90f).toInt() % 4

        // Forward direction (2 blocks) and right offset (1 block)
        val (fwdX, fwdZ, rightX, rightZ) = when (cardinal) {
            0 -> arrayOf(0, 1, -1, 0)    // North (+Z)
            1 -> arrayOf(-1, 0, 0, -1)   // West (-X)
            2 -> arrayOf(0, -1, 1, 0)    // South (-Z)
            3 -> arrayOf(1, 0, 0, 1)     // East (+X)
            else -> arrayOf(0, 1, -1, 0)
        }

        val targetX = pos.x + fwdX * 2.0 + rightX * 1.0
        val targetZ = pos.z + fwdZ * 2.0 + rightZ * 1.0

        // Find the surface Y at the target position
        val targetY = findSurfaceY(targetX, targetZ, pos.y)

        return Triple(targetX, targetY, targetZ)
    }

    /**
     * Find the Y position of the terrain surface at (x, z), searching near the given height.
     */
    private fun findSurfaceY(x: Double, z: Double, nearY: Double): Double {
        val bx = kotlin.math.floor(x).toInt()
        val bz = kotlin.math.floor(z).toInt()
        val startY = nearY.toInt().coerceIn(0, 254)

        // Search downward for solid ground
        for (y in startY downTo 0) {
            if (serverWorld.getBlock(bx, y, bz).solid &&
                !serverWorld.getBlock(bx, y + 1, bz).solid) {
                return (y + 1).toDouble()
            }
        }
        return nearY
    }

    /**
     * Check that the target position has enough headroom for the entity.
     */
    private fun isTargetClear(target: Triple<Double, Double, Double>, collision: CollisionComponent): Boolean {
        val bx = kotlin.math.floor(target.first).toInt()
        val by = target.second.toInt()
        val bz = kotlin.math.floor(target.third).toInt()
        val heightBlocks = kotlin.math.ceil(collision.height.toDouble()).toInt()

        for (dy in 0 until heightBlocks) {
            val y = by + dy
            if (y < 0 || y > 255) return false
            if (serverWorld.getBlock(bx, y, bz).solid) return false
        }
        return true
    }

    private fun lerp(a: Double, b: Double, t: Double): Double = a + (b - a) * t
}
