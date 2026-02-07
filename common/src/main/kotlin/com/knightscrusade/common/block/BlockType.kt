package com.knightscrusade.common.block

/**
 * Defines all block types in the game. Each block has rendering and physics properties.
 */
enum class BlockType(
    val id: Int,
    val displayName: String,
    val solid: Boolean = true,
    val transparent: Boolean = false,
    val lightLevel: Int = 0,
    /** Color for flat-shaded rendering (R, G, B) */
    val color: FloatArray = floatArrayOf(1f, 0f, 1f) // Magenta = missing texture
) {
    AIR(0, "Air", solid = false, transparent = true, color = floatArrayOf(0f, 0f, 0f)),
    STONE(1, "Stone", color = floatArrayOf(0.5f, 0.5f, 0.5f)),
    DIRT(2, "Dirt", color = floatArrayOf(0.55f, 0.36f, 0.2f)),
    GRASS(3, "Grass", color = floatArrayOf(0.3f, 0.65f, 0.2f)),
    OBSIDIAN(4, "Obsidian", color = floatArrayOf(0.1f, 0.05f, 0.15f)),
    BLACKSTONE(5, "Blackstone", color = floatArrayOf(0.15f, 0.12f, 0.12f)),
    QUARTZ(6, "Quartz", color = floatArrayOf(0.93f, 0.9f, 0.87f)),
    DARK_OAK_LOG(7, "Dark Oak Log", color = floatArrayOf(0.25f, 0.15f, 0.08f)),
    DARK_OAK_LEAVES(8, "Dark Oak Leaves", transparent = true, color = floatArrayOf(0.1f, 0.35f, 0.05f)),
    BEDROCK(9, "Bedrock", color = floatArrayOf(0.2f, 0.2f, 0.2f)),
    SAND(10, "Sand", color = floatArrayOf(0.85f, 0.8f, 0.6f)),
    WATER(11, "Water", solid = false, transparent = true, color = floatArrayOf(0.2f, 0.4f, 0.8f)),
    GOLD_BLOCK(12, "Gold Block", lightLevel = 2, color = floatArrayOf(1.0f, 0.84f, 0.0f)),
    TORCH(13, "Torch", solid = false, transparent = true, lightLevel = 14, color = floatArrayOf(1.0f, 0.9f, 0.5f));

    companion object {
        private val BY_ID = entries.associateBy { it.id }

        fun fromId(id: Int): BlockType = BY_ID[id] ?: AIR
    }
}
