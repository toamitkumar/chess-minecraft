package com.knightscrusade.server

import com.knightscrusade.common.ecs.EcsWorld
import com.knightscrusade.common.net.PacketDecoder
import com.knightscrusade.common.net.PacketEncoder
import com.knightscrusade.server.entity.systems.CollisionSystem
import com.knightscrusade.server.entity.systems.LeapSystem
import com.knightscrusade.server.entity.systems.MovementSystem
import com.knightscrusade.server.net.PlayerSessionManager
import com.knightscrusade.server.net.ServerNetworkHandler
import com.knightscrusade.server.persistence.PlayerDatabase
import com.knightscrusade.server.persistence.RegionManager
import com.knightscrusade.server.world.ServerWorld
import java.nio.file.Path
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalServerChannel
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.LockSupport

/**
 * The integrated game server that runs in the same process as the client.
 *
 * Manages the 20 TPS (ticks per second) game loop, world state, player sessions,
 * and Netty networking. In single-player mode, clients connect via Netty's
 * LocalChannel for zero-overhead in-memory communication.
 *
 * This is the same server that will later run as a standalone dedicated server
 * for multiplayer (M6/M7), making the architecture multiplayer-ready from day one.
 */
class IntegratedServer(
    private val worldDir: Path = Path.of("world")
) {

    private val logger = LoggerFactory.getLogger(IntegratedServer::class.java)

    val world = ServerWorld()
    val ecsWorld = EcsWorld()
    val sessionManager = PlayerSessionManager()
    val regionManager = RegionManager(worldDir)
    val playerDatabase = PlayerDatabase(worldDir)

    /** Netty server channel */
    private var serverChannel: Channel? = null
    private var bossGroup: DefaultEventLoopGroup? = null
    private var workerGroup: DefaultEventLoopGroup? = null

    /** Server tick state */
    @Volatile
    var running: Boolean = false
        private set

    @Volatile
    var currentTps: Float = 0f
        private set

    @Volatile
    var currentTickTimeMs: Float = 0f
        private set

    var tickCount: Long = 0
        private set

    private var serverThread: Thread? = null

    companion object {
        /** Target ticks per second, matching Minecraft */
        const val TARGET_TPS = 20
        const val NANOS_PER_TICK = 1_000_000_000L / TARGET_TPS // 50ms

        /** Local address for in-memory Netty channel */
        val LOCAL_ADDRESS = LocalAddress("knightscrusade")
    }

    /**
     * Start the server: bind Netty channel and begin the tick loop on a dedicated thread.
     */
    fun start() {
        if (running) return

        logger.info("Starting IntegratedServer (target: $TARGET_TPS TPS)")

        // Open player database
        playerDatabase.open()

        // Start Netty local server
        bossGroup = DefaultEventLoopGroup(1)
        workerGroup = DefaultEventLoopGroup(1)

        // Register ECS systems in execution order
        ecsWorld.addSystem(MovementSystem())
        ecsWorld.addSystem(CollisionSystem(world))
        ecsWorld.addSystem(LeapSystem(world))

        val networkHandler = ServerNetworkHandler(world, ecsWorld, sessionManager)

        val bootstrap = ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(LocalServerChannel::class.java)
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.pipeline().addLast(
                        PacketDecoder(),
                        PacketEncoder(),
                        networkHandler
                    )
                }
            })

        serverChannel = bootstrap.bind(LOCAL_ADDRESS).sync().channel()
        logger.info("Netty local server bound to $LOCAL_ADDRESS")

        // Start server tick thread
        running = true
        serverThread = Thread({ tickLoop() }, "Server-Thread").apply {
            isDaemon = true
            start()
        }

        logger.info("IntegratedServer started")
    }

    /**
     * The main server tick loop. Runs at 20 TPS using precise timing.
     *
     * Each tick processes:
     * 1. Incoming packets (handled by Netty event loop)
     * 2. World updates
     * 3. Entity updates
     * 4. Outgoing state broadcasts
     */
    private fun tickLoop() {
        var lastTickTime = System.nanoTime()
        var tpsCounter = 0
        var tpsTimer = System.nanoTime()

        while (running) {
            val tickStart = System.nanoTime()

            try {
                tick()
            } catch (e: Exception) {
                logger.error("Error during server tick $tickCount", e)
            }

            tickCount++
            tpsCounter++

            val tickEnd = System.nanoTime()
            currentTickTimeMs = (tickEnd - tickStart) / 1_000_000f

            // Calculate TPS every second
            if (tickEnd - tpsTimer >= 1_000_000_000L) {
                currentTps = tpsCounter.toFloat()
                tpsCounter = 0
                tpsTimer = tickEnd
            }

            // Sleep until next tick
            val elapsed = tickEnd - tickStart
            val sleepNanos = NANOS_PER_TICK - elapsed
            if (sleepNanos > 0) {
                LockSupport.parkNanos(sleepNanos)
            } else {
                // Tick took longer than budget — we're falling behind
                if (elapsed > NANOS_PER_TICK * 2) {
                    logger.warn("Server tick $tickCount took ${currentTickTimeMs}ms (budget: ${NANOS_PER_TICK / 1_000_000}ms)")
                }
            }

            lastTickTime = tickStart
        }
    }

    /**
     * Execute one server tick. Called 20 times per second.
     */
    private fun tick() {
        val dt = 1.0f / TARGET_TPS  // 0.05 seconds per tick

        // Run ECS systems (movement, collision, leap)
        ecsWorld.tick(dt)

        // Broadcast entity positions to clients
        broadcastEntityState()

        // Auto-save dirty chunks every 5 minutes (6000 ticks)
        if (tickCount > 0 && tickCount % (TARGET_TPS * 300) == 0L) {
            regionManager.saveAllDirty(world.getLoadedChunks())
        }

        // Periodically log status
        if (tickCount > 0 && tickCount % (TARGET_TPS * 30) == 0L) {
            logger.debug("Server tick $tickCount | TPS: $currentTps | Players: ${sessionManager.playerCount()} | Entities: ${ecsWorld.entityCount()} | Chunks: ${world.loadedChunkCount()}")
        }
    }

    /**
     * Broadcast updated entity positions to all connected players.
     */
    private fun broadcastEntityState() {
        val playerEntities = ecsWorld.query(
            com.knightscrusade.common.ecs.components.PositionComponent::class.java,
            com.knightscrusade.common.ecs.components.PlayerComponent::class.java
        )

        for (entity in playerEntities) {
            val pos = entity.get(com.knightscrusade.common.ecs.components.PositionComponent::class.java)!!
            val collision = entity.get(com.knightscrusade.common.ecs.components.CollisionComponent::class.java)

            val movePacket = com.knightscrusade.common.proto.Packets.EntityMovePacket.newBuilder()
                .setEntityId(entity.id.toString())
                .setX(pos.x)
                .setY(pos.y)
                .setZ(pos.z)
                .setYaw(pos.yaw)
                .setPitch(pos.pitch)
                .setOnGround(collision?.onGround ?: false)
                .build()

            // Send to all players (including the owning player for reconciliation)
            for (session in sessionManager.getAllSessions()) {
                session.sendPacket(movePacket)
            }
        }
    }

    /**
     * Stop the server gracefully.
     */
    fun stop() {
        if (!running) return

        logger.info("Stopping IntegratedServer...")
        running = false

        // Save all dirty chunks on shutdown
        regionManager.saveAllDirty(world.getLoadedChunks())
        playerDatabase.close()

        serverThread?.join(5000)
        serverChannel?.close()?.sync()
        workerGroup?.shutdownGracefully()
        bossGroup?.shutdownGracefully()

        logger.info("IntegratedServer stopped (ran ${tickCount} ticks)")
    }
}
