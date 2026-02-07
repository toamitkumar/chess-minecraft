package com.knightscrusade.common.ecs

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * An entity in the ECS world — essentially a UUID with a bag of components.
 *
 * Components can be added, removed, and queried by type. The entity itself
 * holds no game logic; all behavior is driven by [GameSystem] implementations.
 *
 * @param id Unique identifier for this entity
 * @param type Human-readable type name (e.g. "knight", "pawn") for debugging/serialization
 */
class Entity(
    val id: UUID = UUID.randomUUID(),
    val type: String = "unknown"
) {
    private val components = ConcurrentHashMap<Class<out Component>, Component>()

    /**
     * Attach a component to this entity. Replaces any existing component of the same type.
     */
    fun <T : Component> add(component: T): Entity {
        components[component.javaClass] = component
        return this
    }

    /**
     * Remove a component by type.
     * @return The removed component, or null if not present
     */
    fun <T : Component> remove(type: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return components.remove(type) as? T
    }

    /**
     * Get a component by type.
     * @return The component, or null if not present
     */
    fun <T : Component> get(type: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return components[type] as? T
    }

    /**
     * Check if this entity has a component of the given type.
     */
    fun <T : Component> has(type: Class<T>): Boolean = components.containsKey(type)

    /**
     * Check if this entity has all the given component types.
     */
    fun hasAll(vararg types: Class<out Component>): Boolean = types.all { components.containsKey(it) }

    override fun toString(): String = "Entity($type, $id)"
}

/** Inline reified helper for getting components. */
inline fun <reified T : Component> Entity.get(): T? = get(T::class.java)

/** Inline reified helper for checking component presence. */
inline fun <reified T : Component> Entity.has(): Boolean = has(T::class.java)

/** Inline reified helper for removing components. */
inline fun <reified T : Component> Entity.remove(): T? = remove(T::class.java)
