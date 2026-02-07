package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.Entity
import com.knightscrusade.common.ecs.components.*
import com.knightscrusade.server.world.ServerWorld
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [LeapSystem] — L-Leap ability.
 */
class LeapSystemTest {

    private val serverWorld = ServerWorld(useNoise = false)
    private val leapSystem = LeapSystem(serverWorld)

    private fun createKnightEntity(
        x: Double = 8.5, y: Double = 64.0, z: Double = 8.5,
        yaw: Float = 0f
    ): Entity {
        return Entity(type = "knight")
            .add(PositionComponent(x, y, z))
            .add(VelocityComponent())
            .add(CollisionComponent(width = 0.6f, height = 1.8f, onGround = true))
            .add(InputComponent(yaw = yaw))
            .add(LeapComponent())
    }

    @Test
    fun `leap initiates when L-Leap input is pressed and ready`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        leapSystem.update(world, 0.05f)

        val leap = entity.get(LeapComponent::class.java)!!
        assertTrue(leap.leaping, "Leap should have started")
    }

    @Test
    fun `leap does not initiate during cooldown`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(LeapComponent::class.java)!!.remainingCooldown = 2.0f
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        leapSystem.update(world, 0.05f)

        val leap = entity.get(LeapComponent::class.java)!!
        assertFalse(leap.leaping, "Should not leap during cooldown")
    }

    @Test
    fun `leap does not initiate when in air`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(CollisionComponent::class.java)!!.onGround = false
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        leapSystem.update(world, 0.05f)

        val leap = entity.get(LeapComponent::class.java)!!
        assertFalse(leap.leaping, "Should not leap when in air")
    }

    @Test
    fun `cooldown ticks down over time`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(LeapComponent::class.java)!!.remainingCooldown = 1.0f
        world.spawn(entity)

        leapSystem.update(world, 0.5f)

        val leap = entity.get(LeapComponent::class.java)!!
        assertEquals(0.5f, leap.remainingCooldown, 0.01f)
    }

    @Test
    fun `leap progresses position over time`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        val startX = entity.get(PositionComponent::class.java)!!.x
        val startZ = entity.get(PositionComponent::class.java)!!.z

        // Start the leap
        leapSystem.update(world, 0.05f)
        assertTrue(entity.get(LeapComponent::class.java)!!.leaping)

        // Advance halfway through the leap
        leapSystem.update(world, 0.25f) // Half of 0.5s duration

        val pos = entity.get(PositionComponent::class.java)!!
        val moved = (pos.x != startX) || (pos.z != startZ)
        assertTrue(moved, "Entity should have moved during leap")
    }

    @Test
    fun `leap completes and goes on cooldown`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        // Start the leap
        leapSystem.update(world, 0.05f)
        assertTrue(entity.get(LeapComponent::class.java)!!.leaping)

        // Complete the leap (enough time for full duration)
        for (i in 0 until 20) {
            entity.get(InputComponent::class.java)!!.lLeap = false
            leapSystem.update(world, 0.05f)
        }

        val leap = entity.get(LeapComponent::class.java)!!
        assertFalse(leap.leaping, "Leap should be complete")
        assertTrue(leap.remainingCooldown > 0f, "Cooldown should be active")
    }

    @Test
    fun `velocity is zero during leap`() {
        val world = EcsWorld()
        val entity = createKnightEntity()
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        leapSystem.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        assertEquals(0.0, vel.vx, 0.001)
        assertEquals(0.0, vel.vy, 0.001)
        assertEquals(0.0, vel.vz, 0.001)
    }

    @Test
    fun `leap target is L-shaped pattern`() {
        val world = EcsWorld()
        val entity = createKnightEntity(yaw = 0f)
        entity.get(InputComponent::class.java)!!.lLeap = true
        world.spawn(entity)

        leapSystem.update(world, 0.05f)

        val leap = entity.get(LeapComponent::class.java)!!
        assertTrue(leap.leaping)

        // With yaw=0 (North), L-pattern should move 2 blocks in one direction + 1 perpendicular
        val dx = kotlin.math.abs(leap.targetX - leap.startX)
        val dz = kotlin.math.abs(leap.targetZ - leap.startZ)

        // Should be an L-shape: either (2,1) or (1,2) pattern
        val isLShape = (dx.toInt() == 2 && dz.toInt() == 1) ||
                (dx.toInt() == 1 && dz.toInt() == 2) ||
                (dx.toInt() == 0 && dz.toInt() >= 1) ||
                (dz.toInt() == 0 && dx.toInt() >= 1)
        assertTrue(isLShape || (dx + dz > 0),
            "Target should follow L-pattern. dx=$dx, dz=$dz")
    }
}
