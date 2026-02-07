package com.knightscrusade.client.render

import org.lwjgl.opengl.GL41.*
import org.joml.Matrix4f
import org.lwjgl.system.MemoryStack

class ShaderProgram(vertexSource: String, fragmentSource: String) {

    val programId: Int

    init {
        val vertexShader = compileShader(vertexSource, GL_VERTEX_SHADER)
        val fragmentShader = compileShader(fragmentSource, GL_FRAGMENT_SHADER)

        programId = glCreateProgram()
        glAttachShader(programId, vertexShader)
        glAttachShader(programId, fragmentShader)
        glLinkProgram(programId)

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(programId)
            throw RuntimeException("Shader program linking failed:\n$log")
        }

        // Shaders are linked into the program; delete the individual shaders
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        glUseProgram(programId)
    }

    fun setMatrix4f(name: String, matrix: Matrix4f) {
        val location = glGetUniformLocation(programId, name)
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            matrix.get(buffer)
            glUniformMatrix4fv(location, false, buffer)
        }
    }

    fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(programId, name), value)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(programId, name), value)
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        glUniform3f(glGetUniformLocation(programId, name), x, y, z)
    }

    fun cleanup() {
        glDeleteProgram(programId)
    }

    private fun compileShader(source: String, type: Int): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, source)
        glCompileShader(shader)

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(shader)
            val typeName = if (type == GL_VERTEX_SHADER) "vertex" else "fragment"
            throw RuntimeException("$typeName shader compilation failed:\n$log")
        }

        return shader
    }
}
