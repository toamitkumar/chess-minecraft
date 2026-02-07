package com.knightscrusade.client.render

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

/**
 * Free-fly camera for Phase 2. Will be attached to the Knight entity in Phase 4.
 */
class Camera {

    val position = Vector3f(0f, 70f, 0f)
    var yaw: Float = -90f    // Looking along -Z initially
    var pitch: Float = -20f

    var fov: Float = 70f
    var nearPlane: Float = 0.1f
    var farPlane: Float = 500f

    var moveSpeed: Float = 10f
    var mouseSensitivity: Float = 0.1f

    private val front = Vector3f()
    private val right = Vector3f()
    private val up = Vector3f(0f, 1f, 0f)
    private val worldUp = Vector3f(0f, 1f, 0f)

    init {
        updateVectors()
    }

    fun getViewMatrix(): Matrix4f {
        val target = Vector3f(position).add(front)
        return Matrix4f().lookAt(position, target, up)
    }

    fun getProjectionMatrix(aspectRatio: Float): Matrix4f {
        return Matrix4f().perspective(
            Math.toRadians(fov.toDouble()).toFloat(),
            aspectRatio,
            nearPlane,
            farPlane
        )
    }

    fun moveForward(deltaTime: Float) {
        val move = Vector3f(front).mul(moveSpeed * deltaTime)
        position.add(move)
    }

    fun moveBackward(deltaTime: Float) {
        val move = Vector3f(front).mul(moveSpeed * deltaTime)
        position.sub(move)
    }

    fun moveLeft(deltaTime: Float) {
        val move = Vector3f(right).mul(moveSpeed * deltaTime)
        position.sub(move)
    }

    fun moveRight(deltaTime: Float) {
        val move = Vector3f(right).mul(moveSpeed * deltaTime)
        position.add(move)
    }

    fun moveUp(deltaTime: Float) {
        position.y += moveSpeed * deltaTime
    }

    fun moveDown(deltaTime: Float) {
        position.y -= moveSpeed * deltaTime
    }

    fun rotate(xOffset: Float, yOffset: Float) {
        yaw += xOffset * mouseSensitivity
        pitch += yOffset * mouseSensitivity

        // Clamp pitch to avoid flipping
        if (pitch > 89f) pitch = 89f
        if (pitch < -89f) pitch = -89f

        updateVectors()
    }

    /**
     * Extract frustum planes from the combined projection-view matrix for culling.
     * Returns array of 6 planes, each as (a, b, c, d) where ax + by + cz + d >= 0 is inside.
     */
    fun getFrustumPlanes(aspectRatio: Float): Array<FloatArray> {
        val pvMatrix = Matrix4f()
        getProjectionMatrix(aspectRatio).mul(getViewMatrix(), pvMatrix)

        val planes = Array(6) { FloatArray(4) }
        val m = FloatArray(16)
        pvMatrix.get(m)

        // Left
        planes[0] = floatArrayOf(m[3] + m[0], m[7] + m[4], m[11] + m[8], m[15] + m[12])
        // Right
        planes[1] = floatArrayOf(m[3] - m[0], m[7] - m[4], m[11] - m[8], m[15] - m[12])
        // Bottom
        planes[2] = floatArrayOf(m[3] + m[1], m[7] + m[5], m[11] + m[9], m[15] + m[13])
        // Top
        planes[3] = floatArrayOf(m[3] - m[1], m[7] - m[5], m[11] - m[9], m[15] - m[13])
        // Near
        planes[4] = floatArrayOf(m[3] + m[2], m[7] + m[6], m[11] + m[10], m[15] + m[14])
        // Far
        planes[5] = floatArrayOf(m[3] - m[2], m[7] - m[6], m[11] - m[10], m[15] - m[14])

        // Normalize each plane
        for (plane in planes) {
            val len = kotlin.math.sqrt(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2])
            if (len > 0f) {
                plane[0] /= len; plane[1] /= len; plane[2] /= len; plane[3] /= len
            }
        }

        return planes
    }

    private fun updateVectors() {
        val yawRad = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())

        front.x = (cos(yawRad) * cos(pitchRad)).toFloat()
        front.y = sin(pitchRad).toFloat()
        front.z = (sin(yawRad) * cos(pitchRad)).toFloat()
        front.normalize()

        front.cross(worldUp, right)
        right.normalize()

        right.cross(front, up)
        up.normalize()
    }
}
