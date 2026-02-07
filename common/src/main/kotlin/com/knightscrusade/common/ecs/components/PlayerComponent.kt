package com.knightscrusade.common.ecs.components

import com.knightscrusade.common.ecs.Component
import java.util.UUID

/**
 * Marks an entity as a player-controlled character.
 *
 * Links the entity to a network session via [playerId] and tracks
 * the player's name for display purposes.
 */
data class PlayerComponent(
    val playerId: UUID,
    val playerName: String
) : Component
