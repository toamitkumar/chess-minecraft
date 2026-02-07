package com.knightscrusade.common.ecs

import com.knightscrusade.common.ecs.components.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

/**
 * Tests for ECS component data classes.
 */
class ComponentsTest {

    @Test
    fun `PositionComponent stores coordinates and look direction`() {
        val pos = PositionComponent(1.0, 2.0, 3.0, 45f, -20f)
        assertEquals(1.0, pos.x)
        assertEquals(2.0, pos.y)
        assertEquals(3.0, pos.z)
        assertEquals(45f, pos.yaw)
        assertEquals(-20f, pos.pitch)
    }

    @Test
    fun `VelocityComponent horizontal speed calculation`() {
        val vel = VelocityComponent(3.0, 0.0, 4.0)
        assertEquals(5.0, vel.horizontalSpeed(), 0.001)
    }

    @Test
    fun `VelocityComponent zero speed`() {
        val vel = VelocityComponent()
        assertEquals(0.0, vel.horizontalSpeed(), 0.001)
    }

    @Test
    fun `CollisionComponent half-width calculation`() {
        val col = CollisionComponent(width = 0.6f, height = 1.8f)
        assertEquals(0.3f, col.halfWidth, 0.001f)
    }

    @Test
    fun `LeapComponent ready state`() {
        val leap = LeapComponent()
        assertTrue(leap.ready, "Should be ready when not leaping and no cooldown")
    }

    @Test
    fun `LeapComponent not ready during cooldown`() {
        val leap = LeapComponent(remainingCooldown = 1.0f)
        assertFalse(leap.ready)
    }

    @Test
    fun `LeapComponent not ready while leaping`() {
        val leap = LeapComponent(leaping = true)
        assertFalse(leap.ready)
    }

    @Test
    fun `PlayerComponent stores player identity`() {
        val uuid = UUID.randomUUID()
        val player = PlayerComponent(uuid, "TestKnight")
        assertEquals(uuid, player.playerId)
        assertEquals("TestKnight", player.playerName)
    }

    @Test
    fun `InputComponent defaults to all false`() {
        val input = InputComponent()
        assertFalse(input.forward)
        assertFalse(input.backward)
        assertFalse(input.jump)
        assertFalse(input.sprint)
        assertFalse(input.lLeap)
    }
}
