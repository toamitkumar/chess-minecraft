package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.GameSystem
import com.knightscrusade.common.ecs.components.CollisionComponent
import com.knightscrusade.common.ecs.components.LeapComponent
import com.knightscrusade.common.ecs.components.PositionComponent
import com.knightscrusade.common.ecs.components.VelocityComponent
import com.knightscrusade.common.math.AABB
import com.knightscrusade.server.world.ServerWorld
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Resolves entity-vs-world AABB collision using axis-by-axis sweep.
 *
 * For each entity with position, velocity, and collision components,
 * this system moves the entity along each axis independently and clips
 * the movement against nearby solid blocks. This prevents entities from
 * passing through walls or falling through floors.
 *
 * @param world The server world to test block collisions against
 */
class CollisionSystem(private val serverWorld: ServerWorld) : GameSystem {

    override fun update(world: EcsWorld, deltaTime: Float) {
        val entities = world.query(
            PositionComponent::class.java,
            VelocityComponent::class.java,
            CollisionComponent::class.java
        )

        for (entity in entities) {
            val pos = entity.get(PositionComponent::class.java)!!
            val vel = entity.get(VelocityComponent::class.java)!!
            val collision = entity.get(CollisionComponent::class.java)!!
            val leap = entity.get(LeapComponent::class.java)

            // Skip collision during leaps — LeapSystem handles positioning
            if (leap != null && leap.leaping) continue

            val hw = collision.halfWidth.toDouble()
            val h = collision.height.toDouble()

            // Desired movement this tick
            var dx = vel.vx * deltaTime
            var dy = vel.vy * deltaTime
            var dz = vel.vz * deltaTime

            // Get entity AABB at current position
            val entityBox = AABB.fromEntity(pos.x, pos.y, pos.z, hw, h)

            // Expand to cover movement range
            val sweepBox = entityBox.expand(dx, dy, dz)

            // Collect all solid block AABBs in the sweep region
            val blockBoxes = collectBlockAABBs(sweepBox)

            // Resolve Y axis first (gravity is most important)
            var origDy = dy
            for (block in blockBoxes) {
                dy = entityBox.clipYCollide(block, dy)
            }
            val resolvedBox1 = entityBox.offset(0.0, dy, 0.0)

            // Resolve X axis
            var resolvedDx = dx
            for (block in blockBoxes) {
                resolvedDx = resolvedBox1.clipXCollide(block, resolvedDx)
            }
            val resolvedBox2 = resolvedBox1.offset(resolvedDx, 0.0, 0.0)

            // Resolve Z axis
            var resolvedDz = dz
            for (block in blockBoxes) {
                resolvedDz = resolvedBox2.clipZCollide(block, resolvedDz)
            }

            // Apply resolved movement
            pos.x += resolvedDx
            pos.y += dy
            pos.z += resolvedDz

            // Detect ground contact
            if (origDy != dy && origDy < 0) {
                collision.onGround = true
                vel.vy = 0.0
            } else if (origDy == dy) {
                collision.onGround = false
            }

            // Zero velocity on wall collision
            if (resolvedDx != dx) vel.vx = 0.0
            if (dy != origDy && origDy > 0) vel.vy = 0.0  // Hit ceiling
            if (resolvedDz != dz) vel.vz = 0.0
        }
    }

    /**
     * Collect AABBs of all solid blocks overlapping the given region.
     */
    private fun collectBlockAABBs(region: AABB): List<AABB> {
        val boxes = mutableListOf<AABB>()

        val minBX = floor(region.minX).toInt()
        val minBY = floor(region.minY).toInt().coerceAtLeast(0)
        val maxBX = ceil(region.maxX).toInt()
        val maxBY = ceil(region.maxY).toInt().coerceAtMost(255)
        val minBZ = floor(region.minZ).toInt()
        val maxBZ = ceil(region.maxZ).toInt()

        for (bx in minBX..maxBX) {
            for (by in minBY..maxBY) {
                for (bz in minBZ..maxBZ) {
                    val block = serverWorld.getBlock(bx, by, bz)
                    if (block.solid) {
                        boxes.add(AABB.block(bx, by, bz))
                    }
                }
            }
        }

        return boxes
    }
}
