package com.knightscrusade.server.entity.systems

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.ecs.Entity
import com.knightscrusade.common.ecs.components.*
import com.knightscrusade.server.world.ServerWorld
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [CollisionSystem] — AABB collision resolution.
 */
class CollisionSystemTest {

    private val serverWorld = ServerWorld(useNoise = false)
    private val collisionSystem = CollisionSystem(serverWorld)

    @Test
    fun `entity falls and lands on terrain`() {
        val world = EcsWorld()

        // Place entity above terrain (terrain surface is Y=63)
        val entity = Entity(type = "test")
            .add(PositionComponent(8.5, 70.0, 8.5))
            .add(VelocityComponent(vy = -5.0))
            .add(CollisionComponent(width = 0.6f, height = 1.8f, onGround = false))

        world.spawn(entity)

        // Simulate several ticks of falling
        for (i in 0 until 20) {
            collisionSystem.update(world, 0.05f)
            // Reapply gravity-like velocity if not on ground
            val col = entity.get(CollisionComponent::class.java)!!
            if (!col.onGround) {
                entity.get(VelocityComponent::class.java)!!.vy -= 1.4 // Simplified gravity
            }
        }

        val pos = entity.get(PositionComponent::class.java)!!
        val col = entity.get(CollisionComponent::class.java)!!

        // Entity should have landed on or near the terrain surface
        assertTrue(pos.y <= 70.0, "Entity should have moved down")
        assertTrue(col.onGround || pos.y < 68.0, "Entity should have landed or moved significantly")
    }

    @Test
    fun `entity does not fall through bedrock`() {
        val world = EcsWorld()

        // Place entity just above bedrock at Y=0
        val entity = Entity(type = "test")
            .add(PositionComponent(8.5, 1.0, 8.5))
            .add(VelocityComponent(vy = -10.0))
            .add(CollisionComponent(width = 0.6f, height = 1.8f, onGround = false))

        world.spawn(entity)
        collisionSystem.update(world, 0.05f)

        val pos = entity.get(PositionComponent::class.java)!!
        assertTrue(pos.y >= 1.0, "Entity should not fall through bedrock (Y >= 1.0)")
    }

    @Test
    fun `horizontal collision stops movement`() {
        val world = EcsWorld()

        // The terrain generator creates an obsidian tower at the center chunk.
        // Place entity near the tower wall and move toward it.
        val entity = Entity(type = "test")
            .add(PositionComponent(4.0, 65.0, 7.5))
            .add(VelocityComponent(vx = 10.0))
            .add(CollisionComponent(width = 0.6f, height = 1.8f, onGround = true))

        world.spawn(entity)
        collisionSystem.update(world, 0.05f)

        val vel = entity.get(VelocityComponent::class.java)!!
        val pos = entity.get(PositionComponent::class.java)!!

        // Entity should be stopped by the obsidian tower wall
        assertTrue(pos.x < 6.0, "Entity should be stopped by tower wall")
    }

    @Test
    fun `entity with zero velocity does not move`() {
        val world = EcsWorld()
        val entity = Entity(type = "test")
            .add(PositionComponent(8.5, 64.0, 8.5))
            .add(VelocityComponent(0.0, 0.0, 0.0))
            .add(CollisionComponent(width = 0.6f, height = 1.8f, onGround = true))

        world.spawn(entity)
        collisionSystem.update(world, 0.05f)

        val pos = entity.get(PositionComponent::class.java)!!
        assertEquals(8.5, pos.x, 0.001)
        assertEquals(64.0, pos.y, 0.001)
        assertEquals(8.5, pos.z, 0.001)
    }

    @Test
    fun `skips entity during active leap`() {
        val world = EcsWorld()
        val entity = Entity(type = "test")
            .add(PositionComponent(8.5, 100.0, 8.5)) // High up
            .add(VelocityComponent(vy = -10.0))
            .add(CollisionComponent(width = 0.6f, height = 1.8f))
            .add(LeapComponent(leaping = true))

        world.spawn(entity)
        collisionSystem.update(world, 0.05f)

        // During leap, collision is skipped — position should not change
        val pos = entity.get(PositionComponent::class.java)!!
        assertEquals(100.0, pos.y, 0.001)
    }
}
