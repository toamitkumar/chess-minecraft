package com.knightscrusade.common.net

import com.knightscrusade.common.proto.Packets
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * Tests for [PacketRegistry] — bidirectional packet ID mapping.
 */
class PacketRegistryTest {

    @Test
    fun `registry has 8 registered packet types`() {
        assertEquals(8, PacketRegistry.registeredCount())
    }

    @Test
    fun `getPacketId returns correct ID for LoginRequest`() {
        val packet = Packets.LoginRequest.newBuilder().setPlayerName("test").build()
        assertEquals(1, PacketRegistry.getPacketId(packet))
    }

    @Test
    fun `getPacketId returns correct ID for LoginResponse`() {
        val packet = Packets.LoginResponse.newBuilder().setPlayerId("id").build()
        assertEquals(2, PacketRegistry.getPacketId(packet))
    }

    @Test
    fun `getPacketId returns correct ID for MovePacket`() {
        val packet = Packets.MovePacket.newBuilder().setPlayerId("id").build()
        assertEquals(3, PacketRegistry.getPacketId(packet))
    }

    @Test
    fun `parsePacket round-trips correctly for all types`() {
        val loginReq = Packets.LoginRequest.newBuilder().setPlayerName("Knight").build()
        val id = PacketRegistry.getPacketId(loginReq)
        val parsed = PacketRegistry.parsePacket(id, loginReq.toByteArray())
        assertEquals(loginReq, parsed)
    }

    @Test
    fun `parsePacket throws for unknown ID`() {
        assertThrows<IllegalArgumentException> {
            PacketRegistry.parsePacket(9999, ByteArray(0))
        }
    }

    @Test
    fun `isRegistered returns true for valid IDs`() {
        assertTrue(PacketRegistry.isRegistered(1))
        assertTrue(PacketRegistry.isRegistered(8))
    }

    @Test
    fun `isRegistered returns false for invalid IDs`() {
        assertFalse(PacketRegistry.isRegistered(0))
        assertFalse(PacketRegistry.isRegistered(99))
    }
}
