package com.knightscrusade.common.net

import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder

/**
 * Netty encoder that serializes protobuf messages to the wire format.
 *
 * Wire format: [packetId: varint] [length: varint] [protobuf payload: bytes]
 *
 * This format allows the decoder to identify the packet type and read exactly
 * the right number of bytes. VarInt encoding keeps small values compact.
 */
class PacketEncoder : MessageToByteEncoder<MessageLite>() {

    override fun encode(ctx: ChannelHandlerContext, msg: MessageLite, out: ByteBuf) {
        val packetId = PacketRegistry.getPacketId(msg)
        val payload = msg.toByteArray()

        writeVarInt(out, packetId)
        writeVarInt(out, payload.size)
        out.writeBytes(payload)
    }
}

/**
 * Netty decoder that deserializes protobuf messages from the wire format.
 *
 * Reads the packet ID and length as VarInts, then parses the protobuf payload
 * using the appropriate parser from [PacketRegistry].
 *
 * Handles partial reads by checking available bytes before consuming the buffer.
 */
class PacketDecoder : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        while (buf.isReadable) {
            buf.markReaderIndex()

            // Try to read packet ID (varint)
            val packetId = readVarInt(buf)
            if (packetId == null) {
                buf.resetReaderIndex()
                return
            }

            // Try to read payload length (varint)
            val length = readVarInt(buf)
            if (length == null) {
                buf.resetReaderIndex()
                return
            }

            // Check if we have enough bytes for the payload
            if (buf.readableBytes() < length) {
                buf.resetReaderIndex()
                return
            }

            // Read and parse the payload
            val payload = ByteArray(length)
            buf.readBytes(payload)

            val message = PacketRegistry.parsePacket(packetId, payload)
            out.add(message)
        }
    }
}

/**
 * Write a variable-length integer to a ByteBuf.
 * Uses the same VarInt encoding as Minecraft's protocol.
 */
internal fun writeVarInt(buf: ByteBuf, value: Int) {
    var remaining = value
    while (true) {
        if (remaining and 0x7F.inv() == 0) {
            buf.writeByte(remaining)
            return
        }
        buf.writeByte((remaining and 0x7F) or 0x80)
        remaining = remaining ushr 7
    }
}

/**
 * Read a variable-length integer from a ByteBuf.
 * Returns null if not enough bytes are available.
 */
internal fun readVarInt(buf: ByteBuf): Int? {
    var result = 0
    var shift = 0
    val startIndex = buf.readerIndex()

    while (true) {
        if (!buf.isReadable) {
            buf.readerIndex(startIndex)
            return null
        }

        val byte = buf.readByte().toInt()
        result = result or ((byte and 0x7F) shl shift)

        if (byte and 0x80 == 0) {
            return result
        }

        shift += 7
        if (shift >= 32) {
            throw RuntimeException("VarInt too big")
        }
    }
}
