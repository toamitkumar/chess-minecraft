package com.knightscrusade.client.render.entity

import org.lwjgl.opengl.GL41.*

/**
 * Placeholder Knight model built from colored boxes.
 *
 * A multi-box model approximating a chess knight piece:
 * - Body: dark purple central box
 * - Head: angular horse-head shaped box
 * - Four legs + two arms (thin boxes)
 *
 * All geometry is flat-shaded with vertex colors. Will be replaced
 * with a proper mesh/skeleton in Phase 6 (animation).
 */
class KnightModel {

    private var vao: Int = 0
    private var vbo: Int = 0
    private var vertexCount: Int = 0
    private var initialized = false

    // Knight colors (dark purple/gray theme)
    private val bodyColor = floatArrayOf(0.25f, 0.15f, 0.30f)   // Dark purple
    private val headColor = floatArrayOf(0.30f, 0.20f, 0.35f)   // Lighter purple
    private val limbColor = floatArrayOf(0.20f, 0.12f, 0.25f)   // Darker purple
    private val eyeColor = floatArrayOf(0.8f, 0.2f, 0.2f)       // Red eyes

    fun init() {
        val vertices = mutableListOf<Float>()

        // Body (centered at origin, 0.5 wide, 0.7 tall, 0.3 deep)
        addBox(vertices, -0.25f, 0.5f, -0.15f, 0.5f, 0.7f, 0.3f, bodyColor)

        // Head (horse-head shape, angled forward)
        addBox(vertices, -0.2f, 1.2f, -0.25f, 0.4f, 0.5f, 0.4f, headColor)
        // Snout
        addBox(vertices, -0.1f, 1.2f, -0.45f, 0.2f, 0.25f, 0.25f, headColor)
        // Eyes
        addBox(vertices, -0.21f, 1.45f, -0.15f, 0.06f, 0.06f, 0.06f, eyeColor)
        addBox(vertices, 0.15f, 1.45f, -0.15f, 0.06f, 0.06f, 0.06f, eyeColor)

        // Ears
        addBox(vertices, -0.15f, 1.7f, -0.15f, 0.08f, 0.15f, 0.08f, headColor)
        addBox(vertices, 0.07f, 1.7f, -0.15f, 0.08f, 0.15f, 0.08f, headColor)

        // Right front leg
        addBox(vertices, -0.2f, 0.0f, -0.15f, 0.12f, 0.5f, 0.12f, limbColor)
        // Left front leg
        addBox(vertices, 0.08f, 0.0f, -0.15f, 0.12f, 0.5f, 0.12f, limbColor)
        // Right back leg
        addBox(vertices, -0.2f, 0.0f, 0.05f, 0.12f, 0.5f, 0.12f, limbColor)
        // Left back leg
        addBox(vertices, 0.08f, 0.0f, 0.05f, 0.12f, 0.5f, 0.12f, limbColor)

        // Arms
        addBox(vertices, -0.37f, 0.7f, -0.06f, 0.12f, 0.5f, 0.12f, limbColor)
        addBox(vertices, 0.25f, 0.7f, -0.06f, 0.12f, 0.5f, 0.12f, limbColor)

        vertexCount = vertices.size / 9  // 9 floats per vertex (pos + color + normal)

        vao = glGenVertexArrays()
        vbo = glGenBuffers()

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        val data = vertices.toFloatArray()
        val buffer = org.lwjgl.system.MemoryUtil.memAllocFloat(data.size)
        buffer.put(data).flip()
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        org.lwjgl.system.MemoryUtil.memFree(buffer)

        // Position (3 floats)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * 4, 0L)
        glEnableVertexAttribArray(0)
        // Color (3 floats)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 9 * 4, 3L * 4)
        glEnableVertexAttribArray(1)
        // Normal (3 floats)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * 4, 6L * 4)
        glEnableVertexAttribArray(2)

        glBindVertexArray(0)
        initialized = true
    }

    fun render() {
        if (!initialized) return
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
        glBindVertexArray(0)
    }

    fun cleanup() {
        if (initialized) {
            glDeleteBuffers(vbo)
            glDeleteVertexArrays(vao)
            initialized = false
        }
    }

    /**
     * Add a box to the vertex list with position, color, and normals.
     * Box is defined by its minimum corner (x, y, z) and dimensions (w, h, d).
     */
    private fun addBox(
        vertices: MutableList<Float>,
        x: Float, y: Float, z: Float,
        w: Float, h: Float, d: Float,
        color: FloatArray
    ) {
        val x2 = x + w
        val y2 = y + h
        val z2 = z + d

        // Front face (towards -Z)
        addQuad(vertices, x, y, z, x2, y, z, x2, y2, z, x, y2, z, 0f, 0f, -1f, color)
        // Back face (towards +Z)
        addQuad(vertices, x2, y, z2, x, y, z2, x, y2, z2, x2, y2, z2, 0f, 0f, 1f, color)
        // Left face (towards -X)
        addQuad(vertices, x, y, z2, x, y, z, x, y2, z, x, y2, z2, -1f, 0f, 0f, color)
        // Right face (towards +X)
        addQuad(vertices, x2, y, z, x2, y, z2, x2, y2, z2, x2, y2, z, 1f, 0f, 0f, color)
        // Top face (towards +Y)
        addQuad(vertices, x, y2, z, x2, y2, z, x2, y2, z2, x, y2, z2, 0f, 1f, 0f, color)
        // Bottom face (towards -Y)
        addQuad(vertices, x, y, z2, x2, y, z2, x2, y, z, x, y, z, 0f, -1f, 0f, color)
    }

    /**
     * Add two triangles forming a quad. Vertices should be in counter-clockwise order.
     */
    private fun addQuad(
        vertices: MutableList<Float>,
        x0: Float, y0: Float, z0: Float,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        nx: Float, ny: Float, nz: Float,
        color: FloatArray
    ) {
        // Triangle 1: v0, v1, v2
        addVertex(vertices, x0, y0, z0, color, nx, ny, nz)
        addVertex(vertices, x1, y1, z1, color, nx, ny, nz)
        addVertex(vertices, x2, y2, z2, color, nx, ny, nz)
        // Triangle 2: v0, v2, v3
        addVertex(vertices, x0, y0, z0, color, nx, ny, nz)
        addVertex(vertices, x2, y2, z2, color, nx, ny, nz)
        addVertex(vertices, x3, y3, z3, color, nx, ny, nz)
    }

    private fun addVertex(
        vertices: MutableList<Float>,
        x: Float, y: Float, z: Float,
        color: FloatArray,
        nx: Float, ny: Float, nz: Float
    ) {
        vertices.add(x); vertices.add(y); vertices.add(z)
        vertices.add(color[0]); vertices.add(color[1]); vertices.add(color[2])
        vertices.add(nx); vertices.add(ny); vertices.add(nz)
    }
}
