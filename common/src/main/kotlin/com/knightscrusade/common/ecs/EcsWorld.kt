package com.knightscrusade.common.ecs

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The ECS world — a container for all entities and systems.
 *
 * Provides entity lifecycle management (spawn, despawn, query) and
 * system execution. Systems are updated in registration order each tick.
 */
class EcsWorld {

    private val entities = ConcurrentHashMap<UUID, Entity>()
    private val systems = mutableListOf<GameSystem>()

    /**
     * Spawn an entity into the world.
     * @return The spawned entity
     */
    fun spawn(entity: Entity): Entity {
        entities[entity.id] = entity
        return entity
    }

    /**
     * Despawn an entity from the world.
     * @return The removed entity, or null if not found
     */
    fun despawn(id: UUID): Entity? = entities.remove(id)

    /**
     * Get an entity by its UUID.
     */
    fun getEntity(id: UUID): Entity? = entities[id]

    /**
     * Get all entities in the world.
     */
    fun getAllEntities(): Collection<Entity> = entities.values

    /**
     * Query entities that have all the specified component types.
     */
    fun query(vararg componentTypes: Class<out Component>): List<Entity> {
        return entities.values.filter { entity ->
            componentTypes.all { entity.has(it) }
        }
    }

    /**
     * Get the total number of entities in the world.
     */
    fun entityCount(): Int = entities.size

    /**
     * Register a system. Systems are updated in registration order.
     */
    fun addSystem(system: GameSystem) {
        systems.add(system)
    }

    /**
     * Run all systems for one tick.
     * @param deltaTime Time since last tick in seconds
     */
    fun tick(deltaTime: Float) {
        for (system in systems) {
            system.update(this, deltaTime)
        }
    }
}

/** Inline reified helper for querying entities by component types. */
inline fun <reified T : Component> EcsWorld.query(): List<Entity> = query(T::class.java)
