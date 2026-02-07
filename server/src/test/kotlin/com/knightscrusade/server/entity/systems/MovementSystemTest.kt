package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.Entity
import com.knightscrusade.common.ecs.components.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [MovementSystem] — input processing and physics.
 */
class MovementSystemTest {

    private fun createTestEntity(
        x: Double = 0.0, y: Double = 65.0, z: Double = 0.0,
        onGround: Boolean = true,
        yaw: Float = 0f
    ): Entity {
        return Entity(type = "test")
            .add(PositionComponent(x, y, z))
            .add(VelocityComponent())
            .add(CollisionComponent(onGround = onGround))
            .add(InputComponent(yaw = yaw))
    }

    @Test
    fun `no input produces no horizontal acceleration`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity()
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        // Should have near-zero horizontal velocity (only drag applied to 0)
        assertEquals(0.0, vel.vx, 0.001)
        assertEquals(0.0, vel.vz, 0.001)
    }

    @Test
    fun `forward input creates negative Z velocity`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity(yaw = 0f)
        entity.get(InputComponent::class.java)!!.forward = true
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        // With yaw=0, forward should be +Z direction
        assertTrue(vel.vz > 0, "Forward input should produce positive Z velocity with yaw=0")
    }

    @Test
    fun `gravity applies when not on ground`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity(onGround = false)
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        assertTrue(vel.vy < 0, "Gravity should make vy negative")
    }

    @Test
    fun `jump sets upward velocity when on ground`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity(onGround = true)
        entity.get(InputComponent::class.java)!!.jump = true
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        val collision = entity.get(CollisionComponent::class.java)!!
        assertTrue(vel.vy > 0, "Jump should set positive vy")
        assertFalse(collision.onGround, "Jump should set onGround to false")
    }

    @Test
    fun `jump does nothing when in air`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity(onGround = false)
        entity.get(InputComponent::class.java)!!.jump = true
        entity.get(VelocityComponent::class.java)!!.vy = -5.0
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        assertTrue(vel.vy < 0, "Cannot jump in air, vy should remain negative")
    }

    @Test
    fun `sprint increases speed`() {
        val world = EcsWorld()
        val system = MovementSystem()

        val walkEntity = createTestEntity()
        walkEntity.get(InputComponent::class.java)!!.forward = true
        world.spawn(walkEntity)

        val sprintEntity = createTestEntity()
        sprintEntity.get(InputComponent::class.java)!!.apply {
            forward = true
            sprint = true
        }
        world.spawn(sprintEntity)

        system.update(world, 0.05f)

        val walkVel = walkEntity.get(VelocityComponent::class.java)!!
        val sprintVel = sprintEntity.get(VelocityComponent::class.java)!!

        assertTrue(sprintVel.horizontalSpeed() > walkVel.horizontalSpeed(),
            "Sprint should produce higher speed than walking")
    }

    @Test
    fun `terminal velocity is enforced`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity(onGround = false)
        entity.get(VelocityComponent::class.java)!!.vy = -100.0 // Already past terminal
        world.spawn(entity)

        system.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        assertTrue(vel.vy >= MovementSystem.TERMINAL_VELOCITY,
            "Velocity should not exceed terminal velocity")
    }

    @Test
    fun `skips entity during active leap`() {
        val world = EcsWorld()
        val system = MovementSystem()
        val entity = createTestEntity()
        entity.add(LeapComponent(leaping = true))
        entity.get(InputComponent::class.java)!!.forward = true
        world.spawn(entity)

        val velBefore = entity.get(VelocityComponent::class.java)!!.vz

        system.update(world, 0.05f)

        val velAfter = entity.get(VelocityComponent::class.java)!!.vz
        // During leap, movement system should not modify velocity
        assertEquals(velBefore, velAfter, 0.001)
    }
}
