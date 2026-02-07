package com.knightscrusade.client.render

import com.knightscrusade.client.render.chunk.ClientChunkManager
import com.knightscrusade.client.render.entity.EntityRenderer
import org.joml.Matrix4f
import org.lwjgl.opengl.GL41.*
import org.slf4j.LoggerFactory

/**
 * Top-level render orchestrator.
 *
 * Manages the rendering pipeline, including chunk rendering with greedy meshing,
 * entity rendering, frustum culling, and basic directional lighting. Coordinates
 * between the camera, chunk manager, entity renderer, and shader programs.
 */
class Renderer {

    private val logger = LoggerFactory.getLogger(Renderer::class.java)

    val camera = Camera()
    val chunkManager = ClientChunkManager()
    val entityRenderer = EntityRenderer()

    private var chunkShader: ShaderProgram? = null
    private val identityMatrix = Matrix4f()

    // Stats for debug overlay
    var drawCalls: Int = 0
        private set
    var verticesRendered: Int = 0
        private set

    /**
     * Initialize the rendering pipeline: shaders, GL state, and subsystems.
     */
    fun init() {
        glClearColor(0.6f, 0.7f, 0.85f, 1.0f) // Sky blue
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)

        // Load chunk shader (also used for entities — same vertex format)
        val vertSource = loadShaderResource("shaders/chunk.vert")
        val fragSource = loadShaderResource("shaders/chunk.frag")
        chunkShader = ShaderProgram(vertSource, fragSource)

        // Initialize entity renderer
        entityRenderer.init()

        logger.info("Renderer initialized (waiting for chunk data from server)")
    }

    /**
     * Render one frame. Called every iteration of the game loop.
     *
     * @param windowWidth Current window width in pixels
     * @param windowHeight Current window height in pixels
     */
    fun render(windowWidth: Int, windowHeight: Int) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Rebuild any dirty chunk meshes
        chunkManager.rebuildDirtyMeshes()

        val aspectRatio = windowWidth.toFloat() / windowHeight.toFloat()
        val projection = camera.getProjectionMatrix(aspectRatio)
        val view = camera.getViewMatrix()

        chunkShader?.let { shader ->
            shader.use()
            shader.setMatrix4f("uProjection", projection)
            shader.setMatrix4f("uView", view)

            drawCalls = 0
            verticesRendered = 0

            // Render chunks with identity model matrix
            shader.setMatrix4f("uModel", identityMatrix)
            for (mesh in chunkManager.getMeshes()) {
                if (mesh.uploaded && mesh.vertexCount > 0) {
                    mesh.render()
                    drawCalls++
                    verticesRendered += mesh.vertexCount
                }
            }

            // Render entities
            entityRenderer.render(shader)
        }
    }

    /**
     * Toggle wireframe rendering mode (for debugging).
     */
    fun toggleWireframe() {
        val mode = IntArray(1)
        glGetIntegerv(GL_POLYGON_MODE, mode)
        if (mode[0] == GL_FILL) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
        }
    }

    /**
     * Release all OpenGL resources.
     */
    fun cleanup() {
        chunkShader?.cleanup()
        chunkManager.cleanup()
        entityRenderer.cleanup()
    }

    private fun loadShaderResource(path: String): String {
        return javaClass.classLoader.getResourceAsStream(path)
            ?.bufferedReader()?.readText()
            ?: throw RuntimeException("Cannot find shader resource: $path")
    }
}
