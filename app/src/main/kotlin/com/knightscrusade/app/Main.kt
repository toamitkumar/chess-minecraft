package com.knightscrusade.app

import com.knightscrusade.client.GameClient
import com.knightscrusade.server.IntegratedServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.knightscrusade.app.Main")

/**
 * Application entry point for Minecraft Chess: The Knight's Crusade.
 *
 * Launches the integrated server and client in the same process.
 * The server runs on a dedicated thread at 20 TPS, while the client
 * runs on the main thread (required by GLFW/OpenGL on macOS).
 *
 * Communication between client and server happens via Netty LocalChannel,
 * using the same packet protocol that will later support remote multiplayer.
 */
fun main() {
    logger.info("Starting Minecraft Chess: The Knight's Crusade v0.1")

    // Start the integrated server on a background thread
    val server = IntegratedServer()

    val client = GameClient()

    try {
        // Start server first (binds Netty local channel)
        server.start()

        // Initialize client (creates window, OpenGL context)
        client.init()

        // Connect client to integrated server
        client.connectToServer(IntegratedServer.LOCAL_ADDRESS)

        // Run the client game loop (blocks until window is closed)
        client.run()

    } catch (e: Exception) {
        logger.error("Fatal error", e)
        e.printStackTrace()
    } finally {
        client.cleanup()
        server.stop()
    }

    logger.info("Game exited cleanly")
}
