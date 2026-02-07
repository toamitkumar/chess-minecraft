package com.knightscrusade.common.ecs

/**
 * Marker interface for all ECS components.
 *
 * Components are pure data holders attached to entities. They contain
 * no logic — behavior is implemented in [GameSystem] implementations
 * that query entities by their component composition.
 */
interface Component
