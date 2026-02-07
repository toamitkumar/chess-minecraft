package com.knightscrusade.client.render.entity

import com.knightscrusade.client.render.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL41.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders entities in the world using the chunk shader.
 *
 * Tracks client-side entity state (positions received from server)
 * and interpolates smoothly between updates. Renders entity models
 * with the same shader used for chunks (shared vertex format).
 */
class EntityRenderer {

    /** Client-side entity state for rendering. */
    data class EntityState(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var z: Double = 0.0,
        var yaw: Float = 0f,
        var pitch: Float = 0f,
        var entityType: String = "knight",
        // Interpolation: previous position for smooth rendering
        var prevX: Double = 0.0,
        var prevY: Double = 0.0,
        var prevZ: Double = 0.0,
        var prevYaw: Float = 0f
    )

    private val entities = ConcurrentHashMap<String, EntityState>()
    private val knightModel = KnightModel()
    private val modelMatrix = Matrix4f()

    /** The local player's entity ID (don't render in first-person). */
    var localEntityId: String? = null

    /** Current camera mode: true = first person, false = third person. */
    var firstPerson: Boolean = true

    fun init() {
        knightModel.init()
    }

    /**
     * Update or add an entity's state from server data.
     */
    fun updateEntity(entityId: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val state = entities.getOrPut(entityId) { EntityState(entityType = "knight") }
        // Store previous position for interpolation
        state.prevX = state.x
        state.prevY = state.y
        state.prevZ = state.z
        state.prevYaw = state.yaw
        // Update to new position
        state.x = x
        state.y = y
        state.z = z
        state.yaw = yaw
        state.pitch = pitch
    }

    /**
     * Add a new entity from a spawn packet.
     */
    fun spawnEntity(entityId: String, entityType: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        entities[entityId] = EntityState(x, y, z, yaw, pitch, entityType, x, y, z, yaw)
    }

    /**
     * Remove an entity.
     */
    fun removeEntity(entityId: String) {
        entities.remove(entityId)
    }

    /**
     * Render all visible entities using the given shader.
     *
     * @param shader The shader program (must already be active with projection/view set)
     */
    fun render(shader: ShaderProgram) {
        for ((entityId, state) in entities) {
            // Skip local player in first-person mode
            if (firstPerson && entityId == localEntityId) continue

            modelMatrix.identity()
                .translate(state.x.toFloat(), state.y.toFloat(), state.z.toFloat())
                .rotateY(Math.toRadians(-state.yaw.toDouble()).toFloat())

            shader.setMatrix4f("uModel", modelMatrix)
            knightModel.render()
        }
    }

    /**
     * Get entity position for camera attachment (third-person mode).
     */
    fun getLocalEntityPosition(): Triple<Double, Double, Double>? {
        val id = localEntityId ?: return null
        val state = entities[id] ?: return null
        return Triple(state.x, state.y, state.z)
    }

    fun getEntityCount(): Int = entities.size

    fun cleanup() {
        knightModel.cleanup()
        entities.clear()
    }
}
