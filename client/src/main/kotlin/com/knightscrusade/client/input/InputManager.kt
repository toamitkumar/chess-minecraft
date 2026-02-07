package com.knightscrusade.client.input

import org.lwjgl.glfw.GLFW.*

/**
 * Manages keyboard and mouse input via GLFW callbacks.
 *
 * Tracks which keys and mouse buttons are currently pressed, and captures
 * mouse movement deltas for camera rotation. Provides a clean interface
 * for game systems to query input state.
 */
class InputManager(private val window: Long) {

    private val keyStates = BooleanArray(GLFW_KEY_LAST + 1)
    private val mouseButtonStates = BooleanArray(GLFW_MOUSE_BUTTON_LAST + 1)

    /** Mouse movement deltas since last frame */
    var mouseDeltaX: Float = 0f
        private set
    var mouseDeltaY: Float = 0f
        private set

    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0
    private var firstMouse: Boolean = true

    /** Whether mouse is captured (hidden and locked to window center) */
    var mouseCaptured: Boolean = false
        private set

    fun init() {
        // Keyboard callback
        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (key in 0..GLFW_KEY_LAST) {
                keyStates[key] = (action == GLFW_PRESS || action == GLFW_REPEAT)
            }
        }

        // Mouse button callback
        glfwSetMouseButtonCallback(window) { _, button, action, _ ->
            if (button in 0..GLFW_MOUSE_BUTTON_LAST) {
                mouseButtonStates[button] = (action == GLFW_PRESS)
            }
        }

        // Mouse position callback
        glfwSetCursorPosCallback(window) { _, xpos, ypos ->
            if (firstMouse) {
                lastMouseX = xpos
                lastMouseY = ypos
                firstMouse = false
            }

            mouseDeltaX = (xpos - lastMouseX).toFloat()
            mouseDeltaY = (lastMouseY - ypos).toFloat() // Inverted Y
            lastMouseX = xpos
            lastMouseY = ypos
        }

        // Capture mouse by default for camera control
        captureMouse()
    }

    /**
     * Check if a key is currently pressed.
     */
    fun isKeyPressed(key: Int): Boolean {
        return if (key in 0..GLFW_KEY_LAST) keyStates[key] else false
    }

    /**
     * Check if a mouse button is currently pressed.
     */
    fun isMouseButtonPressed(button: Int): Boolean {
        return if (button in 0..GLFW_MOUSE_BUTTON_LAST) mouseButtonStates[button] else false
    }

    /**
     * Consume mouse deltas. Call once per frame after processing.
     */
    fun consumeMouseDelta() {
        mouseDeltaX = 0f
        mouseDeltaY = 0f
    }

    /**
     * Capture the mouse cursor (hide + lock for camera control).
     */
    fun captureMouse() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        mouseCaptured = true
        firstMouse = true
    }

    /**
     * Release the mouse cursor (show for UI interaction).
     */
    fun releaseMouse() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        mouseCaptured = false
    }

    /**
     * Toggle mouse capture state.
     */
    fun toggleMouse() {
        if (mouseCaptured) releaseMouse() else captureMouse()
    }
}
