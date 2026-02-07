package com.knightscrusade.client.render.chunk

import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil

/**
 * Holds the VAO/VBO for one sub-chunk's mesh.
 * Vertex format: position (3f) + color (3f) + normal (3f) = 9 floats per vertex
 */
class ChunkMesh {

    var vao: Int = 0
        private set
    var vbo: Int = 0
        private set
    var vertexCount: Int = 0
        private set
    var uploaded: Boolean = false
        private set

    fun upload(vertices: FloatArray) {
        if (vertices.isEmpty()) {
            vertexCount = 0
            uploaded = false
            return
        }

        if (vao == 0) {
            vao = glGenVertexArrays()
            vbo = glGenBuffers()
        }

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        val buffer = MemoryUtil.memAllocFloat(vertices.size)
        buffer.put(vertices).flip()
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(buffer)

        val stride = FLOATS_PER_VERTEX * 4

        // Position (location = 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L)
        glEnableVertexAttribArray(0)

        // Color (location = 1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3L * 4)
        glEnableVertexAttribArray(1)

        // Normal (location = 2)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, 6L * 4)
        glEnableVertexAttribArray(2)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        vertexCount = vertices.size / FLOATS_PER_VERTEX
        uploaded = true
    }

    fun render() {
        if (!uploaded || vertexCount == 0) return
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
        glBindVertexArray(0)
    }

    fun cleanup() {
        if (vao != 0) {
            glDeleteVertexArrays(vao)
            glDeleteBuffers(vbo)
            vao = 0
            vbo = 0
        }
        uploaded = false
    }

    companion object {
        const val FLOATS_PER_VERTEX = 9 // pos(3) + color(3) + normal(3)
    }
}
