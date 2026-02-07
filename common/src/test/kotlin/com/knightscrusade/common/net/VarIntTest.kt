package com.knightscrusade.common.net

import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for VarInt encoding/decoding used in the packet wire format.
 */
class VarIntTest {

    @Test
    fun `encode and decode small values`() {
        assertVarIntRoundTrip(0)
        assertVarIntRoundTrip(1)
        assertVarIntRoundTrip(127)
    }

    @Test
    fun `encode and decode medium values`() {
        assertVarIntRoundTrip(128)
        assertVarIntRoundTrip(255)
        assertVarIntRoundTrip(16383)
    }

    @Test
    fun `encode and decode large values`() {
        assertVarIntRoundTrip(16384)
        assertVarIntRoundTrip(65535)
        assertVarIntRoundTrip(2097151)
        assertVarIntRoundTrip(Int.MAX_VALUE)
    }

    @Test
    fun `single byte for values 0-127`() {
        val buf = Unpooled.buffer()
        writeVarInt(buf, 0)
        assertEquals(1, buf.readableBytes())
        buf.clear()

        writeVarInt(buf, 127)
        assertEquals(1, buf.readableBytes())
    }

    @Test
    fun `two bytes for values 128-16383`() {
        val buf = Unpooled.buffer()
        writeVarInt(buf, 128)
        assertEquals(2, buf.readableBytes())
    }

    @Test
    fun `readVarInt returns null on empty buffer`() {
        val buf = Unpooled.buffer()
        assertNull(readVarInt(buf))
    }

    @Test
    fun `readVarInt returns null on incomplete varint`() {
        val buf = Unpooled.buffer()
        buf.writeByte(0x80) // High bit set = more bytes expected, but buffer ends
        val result = readVarInt(buf)
        assertNull(result)
    }

    private fun assertVarIntRoundTrip(value: Int) {
        val buf = Unpooled.buffer()
        writeVarInt(buf, value)
        val result = readVarInt(buf)
        assertEquals(value, result, "VarInt round-trip failed for value $value")
        buf.release()
    }
}
