package com.knightscrusade.common.ecs

import com.knightscrusade.common.ecs.components.PositionComponent
import com.knightscrusade.common.ecs.components.VelocityComponent
import com.knightscrusade.common.ecs.components.CollisionComponent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

/**
 * Tests for [EcsWorld] — entity container and system executor.
 */
class EcsWorldTest {

    @Test
    fun `spawn and retrieve entity`() {
        val world = EcsWorld()
        val entity = Entity(type = "test")
        world.spawn(entity)

        assertEquals(1, world.entityCount())
        assertSame(entity, world.getEntity(entity.id))
    }

    @Test
    fun `despawn removes entity`() {
        val world = EcsWorld()
        val entity = Entity(type = "test")
        world.spawn(entity)
        world.despawn(entity.id)

        assertEquals(0, world.entityCount())
        assertNull(world.getEntity(entity.id))
    }

    @Test
    fun `despawn nonexistent entity returns null`() {
        val world = EcsWorld()
        assertNull(world.despawn(UUID.randomUUID()))
    }

    @Test
    fun `query returns entities with matching components`() {
        val world = EcsWorld()

        val e1 = Entity(type = "a").add(PositionComponent()).add(VelocityComponent())
        val e2 = Entity(type = "b").add(PositionComponent())
        val e3 = Entity(type = "c").add(VelocityComponent())

        world.spawn(e1)
        world.spawn(e2)
        world.spawn(e3)

        val withPos = world.query(PositionComponent::class.java)
        assertEquals(2, withPos.size)
        assertTrue(withPos.contains(e1))
        assertTrue(withPos.contains(e2))

        val withBoth = world.query(PositionComponent::class.java, VelocityComponent::class.java)
        assertEquals(1, withBoth.size)
        assertSame(e1, withBoth[0])
    }

    @Test
    fun `query returns empty for no matches`() {
        val world = EcsWorld()
        world.spawn(Entity().add(PositionComponent()))

        val results = world.query(CollisionComponent::class.java)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `reified query helper works`() {
        val world = EcsWorld()
        world.spawn(Entity().add(PositionComponent(1.0, 2.0, 3.0)))
        world.spawn(Entity().add(VelocityComponent()))

        val results = world.query<PositionComponent>()
        assertEquals(1, results.size)
    }

    @Test
    fun `getAllEntities returns all entities`() {
        val world = EcsWorld()
        world.spawn(Entity())
        world.spawn(Entity())
        world.spawn(Entity())

        assertEquals(3, world.getAllEntities().size)
    }

    @Test
    fun `tick executes systems in order`() {
        val world = EcsWorld()
        val order = mutableListOf<String>()

        world.addSystem(object : GameSystem {
            override fun update(world: EcsWorld, deltaTime: Float) {
                order.add("first")
            }
        })
        world.addSystem(object : GameSystem {
            override fun update(world: EcsWorld, deltaTime: Float) {
                order.add("second")
            }
        })

        world.tick(0.05f)

        assertEquals(listOf("first", "second"), order)
    }

    @Test
    fun `tick passes correct deltaTime`() {
        val world = EcsWorld()
        var receivedDt = 0f

        world.addSystem(object : GameSystem {
            override fun update(world: EcsWorld, deltaTime: Float) {
                receivedDt = deltaTime
            }
        })

        world.tick(0.05f)
        assertEquals(0.05f, receivedDt, 0.001f)
    }
}
