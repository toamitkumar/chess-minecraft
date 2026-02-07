package com.knightscrusade.common.ecs

/**
 * Interface for ECS systems that process entities each tick.
 *
 * Systems contain all game logic. Each system queries the [EcsWorld]
 * for entities that match its required component set, then processes them.
 *
 * Systems run in a defined order within the server tick loop (20 TPS).
 */
interface GameSystem {

    /**
     * Process one tick. Called 20 times per second by the server.
     *
     * @param world The ECS world containing all entities
     * @param deltaTime Time since last tick in seconds (typically 0.05)
     */
    fun update(world: EcsWorld, deltaTime: Float)
}
