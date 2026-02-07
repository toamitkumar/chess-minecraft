package com.knightscrusade.common.ecs

import com.knightscrusade.common.ecs.components.PositionComponent
import com.knightscrusade.common.ecs.components.VelocityComponent
import com.knightscrusade.common.ecs.components.CollisionComponent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for [Entity] — ECS entity with component bag.
 */
class EntityTest {

    @Test
    fun `entity has unique id and type`() {
        val e = Entity(type = "knight")
        assertNotNull(e.id)
        assertEquals("knight", e.type)
    }

    @Test
    fun `add and get component`() {
        val e = Entity()
        val pos = PositionComponent(1.0, 2.0, 3.0)
        e.add(pos)

        val retrieved = e.get(PositionComponent::class.java)
        assertNotNull(retrieved)
        assertEquals(1.0, retrieved!!.x)
        assertEquals(2.0, retrieved.y)
        assertEquals(3.0, retrieved.z)
    }

    @Test
    fun `get returns null for missing component`() {
        val e = Entity()
        assertNull(e.get(PositionComponent::class.java))
    }

    @Test
    fun `has returns true for present component`() {
        val e = Entity()
        e.add(PositionComponent())
        assertTrue(e.has(PositionComponent::class.java))
    }

    @Test
    fun `has returns false for absent component`() {
        val e = Entity()
        assertFalse(e.has(PositionComponent::class.java))
    }

    @Test
    fun `hasAll checks multiple component types`() {
        val e = Entity()
        e.add(PositionComponent())
        e.add(VelocityComponent())
        assertTrue(e.hasAll(PositionComponent::class.java, VelocityComponent::class.java))
        assertFalse(e.hasAll(PositionComponent::class.java, CollisionComponent::class.java))
    }

    @Test
    fun `remove component returns it`() {
        val e = Entity()
        val pos = PositionComponent(5.0, 6.0, 7.0)
        e.add(pos)

        val removed = e.remove(PositionComponent::class.java)
        assertNotNull(removed)
        assertEquals(5.0, removed!!.x)
        assertFalse(e.has(PositionComponent::class.java))
    }

    @Test
    fun `remove nonexistent component returns null`() {
        val e = Entity()
        assertNull(e.remove(PositionComponent::class.java))
    }

    @Test
    fun `add replaces existing component of same type`() {
        val e = Entity()
        e.add(PositionComponent(1.0, 2.0, 3.0))
        e.add(PositionComponent(10.0, 20.0, 30.0))

        val pos = e.get(PositionComponent::class.java)!!
        assertEquals(10.0, pos.x)
    }

    @Test
    fun `reified helper functions work`() {
        val e = Entity()
        e.add(PositionComponent(1.0, 2.0, 3.0))

        assertTrue(e.has<PositionComponent>())
        assertEquals(1.0, e.get<PositionComponent>()!!.x)

        val removed = e.remove<PositionComponent>()
        assertNotNull(removed)
        assertFalse(e.has<PositionComponent>())
    }

    @Test
    fun `fluent add chaining`() {
        val e = Entity(type = "test")
            .add(PositionComponent())
            .add(VelocityComponent())
            .add(CollisionComponent())

        assertTrue(e.has<PositionComponent>())
        assertTrue(e.has<VelocityComponent>())
        assertTrue(e.has<CollisionComponent>())
    }
}
