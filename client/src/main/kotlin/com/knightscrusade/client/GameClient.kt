package com.knightscrusade.client

import com.knightscrusade.client.input.InputManager
import com.knightscrusade.client.net.ClientNetworkHandler
import com.knightscrusade.client.render.Renderer
import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.math.blockRaycast
import com.knightscrusade.common.proto.Packets
import io.netty.channel.local.LocalAddress
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil.NULL
import org.slf4j.LoggerFactory
import kotlin.math.cos
import kotlin.math.sin

/**
 * Main game client handling the window, render loop, input, and networking.
 *
 * Creates a GLFW window with OpenGL 4.1 core context (macOS compatible),
 * initializes the renderer, input manager, and network handler. Connects
 * to the server via Netty and processes the main game loop.
 *
 * Supports both first-person and third-person camera modes (F5 toggle),
 * block break/place with raycasting, and client-side prediction with
 * server reconciliation.
 */
class GameClient {

    private val logger = LoggerFactory.getLogger(GameClient::class.java)

    private var window: Long = NULL
    val renderer = Renderer()
    private lateinit var inputManager: InputManager
    private var networkHandler: ClientNetworkHandler? = null

    /** Input sequence number for client-side prediction */
    private var inputSequence: Long = 0

    private var fps = 0
    private var frameCount = 0
    private var lastFpsTime = 0.0
    private var lastFrameTime = 0.0

    var windowWidth = 1280
        private set
    var windowHeight = 720
        private set

    /** Camera mode: true = first person, false = third person */
    private var firstPerson = true
    private var thirdPersonDistance = 5f

    /** Cooldowns for key-triggered actions (prevent rapid repeats) */
    private var f5Cooldown = 0f
    private var f1Cooldown = 0f
    private var breakCooldown = 0f
    private var placeCooldown = 0f

    /** Block type to place (cycle with scroll wheel later; for now, stone) */
    private var selectedBlockType = BlockType.STONE

    /**
     * Initialize GLFW, create the window, set up OpenGL context,
     * and initialize all client subsystems.
     */
    fun init() {
        logger.info("Initializing Knight's Crusade v0.1")

        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // OpenGL 4.1 core profile (macOS maximum)
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, 4)

        window = glfwCreateWindow(windowWidth, windowHeight, "Knight's Crusade v0.1", NULL, NULL)
        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Center the window
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        if (vidMode != null) {
            glfwSetWindowPos(
                window,
                (vidMode.width() - windowWidth) / 2,
                (vidMode.height() - windowHeight) / 2
            )
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)

        GL.createCapabilities()

        val glVersion = glGetString(GL_VERSION)
        val glRenderer = glGetString(GL_RENDERER)
        logger.info("OpenGL $glVersion on $glRenderer")

        glViewport(0, 0, windowWidth, windowHeight)
        glEnable(GL_MULTISAMPLE)

        glfwSetFramebufferSizeCallback(window) { _, width, height ->
            windowWidth = width
            windowHeight = height
            glViewport(0, 0, width, height)
        }

        // Initialize subsystems
        inputManager = InputManager(window)
        inputManager.init()
        renderer.init()

        lastFrameTime = glfwGetTime()
        lastFpsTime = glfwGetTime()

        logger.info("GameClient initialized successfully")
    }

    /**
     * Connect to the integrated server and log in.
     * Chunk data will stream from the server via packets.
     *
     * @param address Netty local address of the integrated server
     */
    fun connectToServer(address: LocalAddress) {
        networkHandler = ClientNetworkHandler(renderer.chunkManager, renderer.entityRenderer)
        networkHandler?.connect(address)
        networkHandler?.login("Knight")

        // Wait briefly for login response and initial chunks
        Thread.sleep(500)

        // Set camera to spawn position
        networkHandler?.let { handler ->
            if (handler.connected) {
                renderer.camera.position.set(
                    handler.spawnX.toFloat(),
                    handler.spawnY.toFloat() + 1.62f, // Eye height
                    handler.spawnZ.toFloat()
                )
                logger.info("Connected to server. Received ${renderer.chunkManager.getChunkCount()} chunks")
            } else {
                logger.warn("Failed to connect to server, using local world")
                renderer.chunkManager.generateFlatWorld(radius = 8)
            }
        }
    }

    /**
     * Main game loop: processes input, sends state to server, renders frame.
     */
    fun run() {
        while (!glfwWindowShouldClose(window)) {
            val currentTime = glfwGetTime()
            val deltaTime = (currentTime - lastFrameTime).toFloat()
            lastFrameTime = currentTime

            // FPS counter
            frameCount++
            if (currentTime - lastFpsTime >= 1.0) {
                fps = frameCount
                frameCount = 0
                lastFpsTime = currentTime

                val handler = networkHandler
                val netInfo = if (handler != null) {
                    "Sent: ${handler.packetsSent} | Recv: ${handler.packetsReceived}"
                } else ""

                glfwSetWindowTitle(window,
                    "Knight's Crusade v0.1 | FPS: $fps | " +
                    "Chunks: ${renderer.chunkManager.getChunkCount()} | " +
                    "Entities: ${renderer.entityRenderer.getEntityCount()} | " +
                    netInfo)
            }

            // Tick cooldowns
            if (f5Cooldown > 0) f5Cooldown -= deltaTime
            if (f1Cooldown > 0) f1Cooldown -= deltaTime
            if (breakCooldown > 0) breakCooldown -= deltaTime
            if (placeCooldown > 0) placeCooldown -= deltaTime

            processInput(deltaTime)
            reconcileWithServer()
            sendPlayerState()
            renderer.render(windowWidth, windowHeight)

            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }

    /**
     * Process keyboard and mouse input.
     */
    private fun processInput(deltaTime: Float) {
        if (inputManager.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true)
            return
        }

        // Camera movement (client-side prediction)
        val moveSpeed = if (inputManager.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) 30f else 10f
        renderer.camera.moveSpeed = moveSpeed

        if (inputManager.isKeyPressed(GLFW_KEY_W)) renderer.camera.moveForward(deltaTime)
        if (inputManager.isKeyPressed(GLFW_KEY_S)) renderer.camera.moveBackward(deltaTime)
        if (inputManager.isKeyPressed(GLFW_KEY_A)) renderer.camera.moveLeft(deltaTime)
        if (inputManager.isKeyPressed(GLFW_KEY_D)) renderer.camera.moveRight(deltaTime)
        if (inputManager.isKeyPressed(GLFW_KEY_SPACE)) renderer.camera.moveUp(deltaTime)
        if (inputManager.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) renderer.camera.moveDown(deltaTime)

        // Mouse look
        if (inputManager.mouseCaptured) {
            renderer.camera.rotate(inputManager.mouseDeltaX, inputManager.mouseDeltaY)
        }
        inputManager.consumeMouseDelta()

        // F5: Toggle first-person / third-person
        if (inputManager.isKeyPressed(GLFW_KEY_F5) && f5Cooldown <= 0) {
            firstPerson = !firstPerson
            renderer.entityRenderer.firstPerson = firstPerson
            f5Cooldown = 0.3f
            logger.info("Camera mode: ${if (firstPerson) "First Person" else "Third Person"}")
        }

        // F1: Toggle wireframe
        if (inputManager.isKeyPressed(GLFW_KEY_F1) && f1Cooldown <= 0) {
            renderer.toggleWireframe()
            f1Cooldown = 0.3f
        }

        // Tab: Toggle mouse capture
        if (inputManager.isKeyPressed(GLFW_KEY_TAB)) {
            inputManager.toggleMouse()
        }

        // Left click: Break block
        if (inputManager.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT) && breakCooldown <= 0) {
            attemptBlockBreak()
            breakCooldown = 0.25f
        }

        // Right click: Place block
        if (inputManager.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT) && placeCooldown <= 0) {
            attemptBlockPlace()
            placeCooldown = 0.25f
        }
    }

    /**
     * Reconcile client position with server-authoritative position.
     * In this M0 phase, we snap to server position to keep things simple.
     */
    private fun reconcileWithServer() {
        val handler = networkHandler ?: return
        // For now, we use client-side free-fly — server reconciliation
        // will be more meaningful once ECS physics is running.
        // The server position is available at handler.serverX/Y/Z for future use.
    }

    /**
     * Raycast to find the block the player is looking at and break it.
     */
    private fun attemptBlockBreak() {
        val cam = renderer.camera
        val hit = performBlockRaycast() ?: return

        // Don't allow breaking bedrock client-side (server will also reject)
        val block = getBlockAt(hit.blockX, hit.blockY, hit.blockZ)
        if (block == BlockType.BEDROCK) return

        // Send block change to server (set to AIR = break)
        val packet = Packets.BlockChangePacket.newBuilder()
            .setX(hit.blockX)
            .setY(hit.blockY)
            .setZ(hit.blockZ)
            .setBlockId(BlockType.AIR.id)
            .build()
        networkHandler?.sendPacket(packet)
    }

    /**
     * Raycast to find the face the player is looking at and place a block.
     */
    private fun attemptBlockPlace() {
        val hit = performBlockRaycast() ?: return

        val packet = Packets.BlockChangePacket.newBuilder()
            .setX(hit.placeX)
            .setY(hit.placeY)
            .setZ(hit.placeZ)
            .setBlockId(selectedBlockType.id)
            .build()
        networkHandler?.sendPacket(packet)
    }

    /**
     * Perform a raycast from the camera to find the targeted block.
     */
    private fun performBlockRaycast(): com.knightscrusade.common.math.RaycastHit? {
        val cam = renderer.camera
        val yawRad = Math.toRadians(cam.yaw.toDouble())
        val pitchRad = Math.toRadians(cam.pitch.toDouble())

        val dirX = (cos(yawRad) * cos(pitchRad)).toFloat()
        val dirY = sin(pitchRad).toFloat()
        val dirZ = (sin(yawRad) * cos(pitchRad)).toFloat()

        return blockRaycast(
            cam.position.x, cam.position.y, cam.position.z,
            dirX, dirY, dirZ,
            maxDistance = 6f
        ) { x, y, z -> getBlockAt(x, y, z).solid }
    }

    /**
     * Get the block type at world coordinates from the client's chunk data.
     */
    private fun getBlockAt(x: Int, y: Int, z: Int): BlockType {
        if (y < 0 || y >= 256) return BlockType.AIR
        val chunkPos = com.knightscrusade.common.chunk.ChunkPos.fromWorldPos(x, z)
        val chunk = renderer.chunkManager.getChunk(chunkPos) ?: return BlockType.AIR
        val localX = ((x % 16) + 16) % 16
        val localZ = ((z % 16) + 16) % 16
        return chunk.getBlock(localX, y, localZ)
    }

    /**
     * Send the current player state to the server via MovePacket.
     */
    private fun sendPlayerState() {
        val handler = networkHandler ?: return
        val pid = handler.playerId ?: return

        inputSequence++

        val packet = Packets.MovePacket.newBuilder()
            .setPlayerId(pid)
            .setX(renderer.camera.position.x.toDouble())
            .setY(renderer.camera.position.y.toDouble() - 1.62) // Convert eye pos to feet pos
            .setZ(renderer.camera.position.z.toDouble())
            .setYaw(renderer.camera.yaw)
            .setPitch(renderer.camera.pitch)
            .setSequence(inputSequence)
            .setOnGround(false) // Free-fly mode for now
            .setForward(inputManager.isKeyPressed(GLFW_KEY_W))
            .setBackward(inputManager.isKeyPressed(GLFW_KEY_S))
            .setLeft(inputManager.isKeyPressed(GLFW_KEY_A))
            .setRight(inputManager.isKeyPressed(GLFW_KEY_D))
            .setJump(inputManager.isKeyPressed(GLFW_KEY_SPACE))
            .setSprint(inputManager.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            .setLLeap(inputManager.isKeyPressed(GLFW_KEY_F))
            .build()

        handler.sendPacket(packet)
    }

    /**
     * Clean up all resources.
     */
    fun cleanup() {
        logger.info("Shutting down GameClient")
        networkHandler?.disconnect()
        renderer.cleanup()
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }
}
