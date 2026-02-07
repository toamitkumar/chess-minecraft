package com.knightscrusade.common.proto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Protocol Buffer packet serialization/deserialization round-trips.
 * Validates that all packet types can be correctly serialized to bytes and
 * deserialized back to identical messages.
 */
class PacketSerializationTest {

    @Test
    fun `LoginRequest round-trip serialization`() {
        val original = Packets.LoginRequest.newBuilder()
            .setPlayerName("TestKnight")
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.LoginRequest.parseFrom(bytes)

        assertEquals("TestKnight", deserialized.playerName)
        assertEquals(original, deserialized)
    }

    @Test
    fun `LoginResponse round-trip serialization`() {
        val original = Packets.LoginResponse.newBuilder()
            .setPlayerId("550e8400-e29b-41d4-a716-446655440000")
            .setSpawnX(128.5)
            .setSpawnY(64.0)
            .setSpawnZ(128.5)
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.LoginResponse.parseFrom(bytes)

        assertEquals("550e8400-e29b-41d4-a716-446655440000", deserialized.playerId)
        assertEquals(128.5, deserialized.spawnX, 0.001)
        assertEquals(64.0, deserialized.spawnY, 0.001)
        assertEquals(128.5, deserialized.spawnZ, 0.001)
    }

    @Test
    fun `MovePacket round-trip serialization`() {
        val original = Packets.MovePacket.newBuilder()
            .setPlayerId("player-uuid-123")
            .setX(100.25)
            .setY(65.0)
            .setZ(200.75)
            .setYaw(45.0f)
            .setPitch(-10.0f)
            .setSequence(42L)
            .setOnGround(true)
            .setForward(true)
            .setBackward(false)
            .setLeft(false)
            .setRight(true)
            .setJump(false)
            .setSprint(true)
            .setLLeap(false)
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.MovePacket.parseFrom(bytes)

        assertEquals(original, deserialized)
        assertTrue(deserialized.forward)
        assertFalse(deserialized.backward)
        assertTrue(deserialized.sprint)
        assertFalse(deserialized.lLeap)
        assertEquals(42L, deserialized.sequence)
    }

    @Test
    fun `ChunkDataPacket round-trip serialization`() {
        val blockData = ByteArray(4096) { (it % 10).toByte() }

        val original = Packets.ChunkDataPacket.newBuilder()
            .setChunkX(3)
            .setChunkZ(-2)
            .setBlockData(com.google.protobuf.ByteString.copyFrom(blockData))
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.ChunkDataPacket.parseFrom(bytes)

        assertEquals(3, deserialized.chunkX)
        assertEquals(-2, deserialized.chunkZ)
        assertArrayEquals(blockData, deserialized.blockData.toByteArray())
    }

    @Test
    fun `BlockChangePacket round-trip serialization`() {
        val original = Packets.BlockChangePacket.newBuilder()
            .setX(100)
            .setY(64)
            .setZ(200)
            .setBlockId(3) // GRASS
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.BlockChangePacket.parseFrom(bytes)

        assertEquals(100, deserialized.x)
        assertEquals(64, deserialized.y)
        assertEquals(200, deserialized.z)
        assertEquals(3, deserialized.blockId)
    }

    @Test
    fun `EntitySpawnPacket round-trip serialization`() {
        val original = Packets.EntitySpawnPacket.newBuilder()
            .setEntityId("entity-uuid-456")
            .setEntityType("knight")
            .setX(50.0)
            .setY(65.0)
            .setZ(50.0)
            .setYaw(0.0f)
            .setPitch(0.0f)
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.EntitySpawnPacket.parseFrom(bytes)

        assertEquals("entity-uuid-456", deserialized.entityId)
        assertEquals("knight", deserialized.entityType)
        assertEquals(50.0, deserialized.x, 0.001)
    }

    @Test
    fun `EntityMovePacket round-trip serialization`() {
        val original = Packets.EntityMovePacket.newBuilder()
            .setEntityId("entity-uuid-789")
            .setX(51.5)
            .setY(65.0)
            .setZ(52.3)
            .setYaw(90.0f)
            .setPitch(-5.0f)
            .setOnGround(true)
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.EntityMovePacket.parseFrom(bytes)

        assertEquals(original, deserialized)
        assertTrue(deserialized.onGround)
    }

    @Test
    fun `EntityRemovePacket round-trip serialization`() {
        val original = Packets.EntityRemovePacket.newBuilder()
            .setEntityId("entity-to-remove")
            .build()

        val bytes = original.toByteArray()
        val deserialized = Packets.EntityRemovePacket.parseFrom(bytes)

        assertEquals("entity-to-remove", deserialized.entityId)
    }

    @Test
    fun `empty MovePacket has default values`() {
        val empty = Packets.MovePacket.getDefaultInstance()
        assertEquals("", empty.playerId)
        assertEquals(0.0, empty.x, 0.001)
        assertFalse(empty.forward)
        assertFalse(empty.sprint)
        assertEquals(0L, empty.sequence)
    }
}
