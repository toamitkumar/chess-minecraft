# M2 — Alpha

**Timeline:** Months 6-9
**Version:** v0.3 (Alpha)
**Status:** Not Started
**Depends On:** M1

---

## Milestone Goal

Deliver a playable build spanning **Acts I and II** (Rows 1-4, zones a1-h4). The player experiences the first half of the campaign: 6 rescue missions, 5 enemy types, crafting + alchemy, AI-controlled companion combat, and a full settings menu. Entity UUIDs, event bus, chunk streaming, and serializable player data are all operational.

## Working Build at Completion

> **v0.3 Alpha:** The player starts at the Home Base (a1), completes the Act I tutorial, rescues 2 Pawns, crosses into contested territory (Rows 3-4), fights White Pawns, Knights, and Bishops, rescues 3 more Pawns and 1 Bishop, uses AI-controlled Pawn companions in combat, crafts chess-themed weapons, brews 6+ elixirs, and reaches the edge of Row 5. Full save/load, complete settings menu, and all content is playable start-to-finish without crashes.

---

## Deliverables & Tasks

### 1. World — Rows 1-4 (32 Zones)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Implement world generation for all 4 biome types: Dark Castle (Row 1), Shadow Fields (Row 2), Twilight Outposts (Row 3), Contested Plains (Row 4) | Engine | [ ] | Each biome generates distinct terrain with correct block palettes |
| 1.2 | Build primary structures for Row 1 zones (a1-h1) | Level Design | [ ] | 8 zones with castles, armory, training grounds, alchemy stations |
| 1.3 | Build primary structures for Row 2 zones (a2-h2) | Level Design | [ ] | 8 zones with pawn outposts, dark forest camps, supply caches |
| 1.4 | Build primary structures for Row 3 zones (a3-h3) | Level Design | [ ] | 8 zones with watchtowers, scout camps, forward bases |
| 1.5 | Build primary structures for Row 4 zones (a4-h4) | Level Design | [ ] | 8 zones with ruins, trenches, battlefield structures |
| 1.6 | Implement zone border transitions (16-block decorative borders) | Level Design | [ ] | Gradient blending between biomes, chess notation signposts |
| 1.7 | Implement chess coordinate markers for all 32 zones | Art/Level | [ ] | Gold-block engravings at center and borders of each zone |
| 1.8 | Implement locked gates between zone borders (story progression) | Gameplay | [ ] | Gates at Row 2→3 require Act I completion to open |
| 1.9 | Create 100+ block textures (PBR) for Rows 1-4 biomes | Art | [ ] | 32x32 albedo + normal + roughness for all block types |
| 1.10 | Implement underground dungeon generation (1-2 per zone) | Engine | [ ] | Procedural dungeon rooms with loot and enemy spawns |

### 2. Enemy Types — 5 of 6

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | White Pawn — finalize (from M1) | Art/AI | [ ] | Production-quality model, 4 animations, patrol AI |
| 2.2 | White Knight — model, animations, AI | Art/AI | [ ] | L-pattern jumps every 3 sec, AOE strike on landing, unpredictable flanking |
| 2.3 | White Bishop — model, animations, AI | Art/AI | [ ] | Diagonal movement, ranged beam attack, stays on own square color |
| 2.4 | White Rook — model, animations, AI | Art/AI | [ ] | Straight-line charges, ram attack, heavy knockback, breaks weak blocks |
| 2.5 | White Queen — model, animations, AI (mini-boss) | Art/AI | [ ] | All-directional movement, combined Bishop+Rook attacks, very fast |
| 2.6 | Implement AI difficulty scaling per game difficulty setting | AI | [ ] | Peaceful/Easy/Normal/Hard/Grandmaster affect AI responsiveness and damage |
| 2.7 | Implement enemy respawn timer system | AI | [ ] | Defeated enemies respawn after 5 minutes (configurable per zone) |

### 3. Rescue Missions — 6 Missions (Acts I-II)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Mission 1: "The Lost Sentry" — Pawn rescue (a1 dungeon) | Level Design | [ ] | Finalized from M1, polished |
| 3.2 | Mission 2: "Shadow Patrol" — Pawn rescue (Row 2 escort) | Level Design | [ ] | Escort rescued Pawn through Shadow Fields to Home Base |
| 3.3 | Mission 3: "Twilight Assault" — Pawn rescue (Row 3 combat) | Level Design | [ ] | Fight through White Knight patrol to reach imprisoned Pawn |
| 3.4 | Mission 4: "Contested Ground" — Pawn rescue (Row 4 dungeon) | Level Design | [ ] | Multi-room dungeon crawl with Pawns + Knights |
| 3.5 | Mission 5: "Diagonal Trap" — Pawn rescue (Row 4 stealth) | Level Design | [ ] | Sneak past Bishop patrols, avoid diagonal detection beams |
| 3.6 | Mission 6: "The Dark Square" — Bishop rescue (d4 dungeon) | Level Design | [ ] | Boss encounter: defeat White Knight mini-boss, chess fork puzzle, free Bishop |
| 3.7 | Implement mission discovery: clue system (maps, notes, NPC dialogue) | Gameplay | [ ] | Each mission has 2-3 discoverable clues leading to the rescue location |
| 3.8 | Implement mission extraction: rally points | Gameplay | [ ] | Rescued pieces can be directed to rally points instead of full escort back |

### 4. AI-Controlled Companion Combat

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Implement companion summon system (H key, up to 2 companions) | Gameplay | [ ] | Player selects from rescued pieces, companions materialize with summon animation |
| 4.2 | Implement AI behavior modes: Aggressive, Defensive, Strategic, Retreat | AI | [ ] | Each mode changes companion decision-making as per PRD Section 7.4 |
| 4.3 | Implement Pawn companion AI (forward march, diagonal attack, shield wall) | AI | [ ] | Pawns form shield walls when 3+ are nearby, attack diagonally |
| 4.4 | Implement Bishop companion AI (diagonal movement, ranged heal, avoid melee) | AI | [ ] | Bishop heals player from distance, stays on own square color |
| 4.5 | Implement companion command key (G): cycle stance | Gameplay | [ ] | G key cycles Aggressive → Defensive → Strategic → Retreat for all companions |
| 4.6 | Implement rally point (H + direction) | Gameplay | [ ] | Companions regroup at the rally point |
| 4.7 | Implement target ping (Tab on locked target) | Gameplay | [ ] | Companions focus-fire the pinged enemy |
| 4.8 | Implement companion HP bar on HUD (bottom-right) | UI | [ ] | Each summoned companion shows name, HP, and stance icon |
| 4.9 | Implement companion "captured" state (disabled when HP=0) | AI | [ ] | Companion dissolves, unavailable for 2 minutes, then revives at Home Base |
| 4.10 | Implement AI difficulty scaling for companions | AI | [ ] | Companion effectiveness varies by game difficulty per PRD Section 7.4 |

### 5. Crafting System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Implement 3x3 crafting grid (Minecraft-style) | Gameplay | [ ] | Place items in grid, matching recipe produces output |
| 5.2 | Implement recipe registry (data-driven, JSON) | Engine | [ ] | Recipes loaded from JSON files, modifiable via resource packs |
| 5.3 | Implement Pawn Sword recipe (2 Iron + 1 Stick) | Gameplay | [ ] | Craftable, +4 damage, visible on Knight model |
| 5.4 | Implement Knight's Lance recipe (3 Diamond + 2 Obsidian, L-shape) | Gameplay | [ ] | Craftable, +8 damage, extended reach |
| 5.5 | Implement Bishop's Staff recipe (2 Gold + 1 Amethyst, diagonal) | Gameplay | [ ] | Craftable, enables ranged diagonal attack |
| 5.6 | Implement armor tiers: Wood, Stone, Iron, Diamond (helm, plate, greaves, horseshoes) | Gameplay | [ ] | Each tier provides increasing armor rating and visual change |
| 5.7 | Implement resource gathering: mining blocks drops resources | Gameplay | [ ] | Stone → cobblestone, iron ore → iron ingot (via smelting), etc. |
| 5.8 | Implement smelting furnace | Gameplay | [ ] | Fuel + ore → ingot. Server-validated. |

### 6. Alchemy — Full System for Available Ingredients

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Add remaining ingredients available in Rows 1-4 | Gameplay | [ ] | All common/uncommon ingredients from PRD Section 15.2 obtainable |
| 6.2 | Implement 6 additional elixirs (8 total) | Gameplay | [ ] | Elixir of Rook, Bishop, Fury, Shielding, Regeneration, Pawn's Resolve, Knight, Featherfall |
| 6.3 | Implement recipe discovery via scrolls in dungeons | Gameplay | [ ] | Finding a scroll unlocks the recipe in the brewing UI |
| 6.4 | Implement elixir wear-off warning (10-sec flicker + chime) | UI/Audio | [ ] | Particle aura flickers, warning sound plays at 10 seconds remaining |
| 6.5 | Implement companion elixir buffing | Gameplay | [ ] | Via companion command menu, give an elixir to an AI companion |

### 7. Save/Load — Full Implementation

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 7.1 | Implement complete save system: world + player + quests + companions + inventory | Engine | [ ] | All data persisted per PRD Section 10.6 (region files + JSON) |
| 7.2 | Implement auto-save (configurable: 2/5/10/15 min, default 5) | Engine | [ ] | Auto-save triggers silently, saves icon flashes briefly on HUD |
| 7.3 | Implement 3 rotating backup save slots | Engine | [ ] | Last 3 auto-saves preserved, accessible from load screen |
| 7.4 | Implement quick save (F6) / quick load (F7) | Engine | [ ] | Instant save/load with confirmation |
| 7.5 | Implement save slot selection screen (Continue Game) | UI | [ ] | Shows 3 save slots with timestamp, playtime, last zone |
| 7.6 | Implement versioned save schemas | Engine | [ ] | Save files include version number; loading older saves migrates data forward |
| 7.7 | Implement serializable player data (toNBT/fromNBT, toJSON/fromJSON) | Engine | [ ] | All player data classes serialize correctly for disk and future network use |

### 8. Settings Menu — Full Implementation

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 8.1 | Implement complete video settings (all options from PRD 12.2) | UI | [ ] | Resolution, display mode, VSync, FPS limit, render distance, graphics quality, etc. |
| 8.2 | Implement complete audio settings (all options from PRD 12.3) | UI | [ ] | Master, music, SFX, ambient, voice, weather volumes + subtitles |
| 8.3 | Implement complete gameplay settings (all options from PRD 12.4) | UI | [ ] | Difficulty, auto-save, HUD, minimap, chess notation hints level, etc. |
| 8.4 | Implement complete accessibility settings (all options from PRD 12.5) | UI | [ ] | Colorblind mode, high contrast, camera shake, flash effects, text size, etc. |
| 8.5 | Implement Chess Notation Hints — 3 levels (Off/Basic/Detailed) | UI/Gameplay | [ ] | Basic: minimap coordinates + tooltips. Detailed: movement diagrams + concept tutorials. |
| 8.6 | Implement Chess Guide Overlay (F8) | UI | [ ] | Board & Notation, Piece Movement, Special Moves, Tactical Concepts pages |
| 8.7 | Implement settings persistence (options.json) | Engine | [ ] | All settings saved and loaded across sessions |

### 9. Entity & Event Architecture

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 9.1 | Verify all entities use UUID-based identification | Engine | [ ] | Audit: no entity referenced by local index; all UUID-based |
| 9.2 | Implement typed event bus with 15+ event types | Engine | [ ] | `PlayerDamageEvent`, `BlockBreakEvent`, `EntityDeathEvent`, `QuestCompleteEvent`, `ItemCraftEvent`, etc. |
| 9.3 | Implement event serialization for future network replay | Engine | [ ] | Events can be serialized to protobuf for logging/replay |
| 9.4 | Verify chunk streaming protocol works across all 32 zones | Network | [ ] | Player can travel from a1 to h4 seamlessly; chunks stream on demand |
| 9.5 | Stress test: 50 entities on screen at 60 FPS | Engine | [ ] | Combat scenario with Knight + 2 companions + 8 enemies + particles runs smoothly |

### 10. Audio — First Pass

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 10.1 | Implement 3D positional audio (OpenAL) | Audio | [ ] | Sounds pan based on source position relative to player |
| 10.2 | Create block interaction sounds (10 material types) | Audio | [ ] | Walk-on, break, and place sounds for stone, wood, obsidian, quartz, etc. |
| 10.3 | Create combat sounds: sword swing, hit, block, enemy death | Audio | [ ] | Each action has distinct sound |
| 10.4 | Create music tracks: Home Base (calm), Exploration (ambient), Combat (fast) | Audio | [ ] | 3 music tracks, context-switching based on game state |
| 10.5 | Create Knight ability sounds: L-Leap, Trample, Horse Kick, Shadow Gallop | Audio | [ ] | Each ability has a unique sound cue |

---

## Exit Criteria

- [ ] Player can complete Acts I and II (6 missions) start-to-finish without crashes
- [ ] 32 zones (Rows 1-4) are explorable with distinct biomes and structures
- [ ] 5 enemy types fight with chess-accurate movement patterns
- [ ] AI-controlled companions fight alongside the player using 4 behavior modes
- [ ] Crafting produces 5+ weapon/armor items from gathered resources
- [ ] 8 elixirs are brewable with visual effects and active limits
- [ ] Full settings menu with all categories operational and persisted
- [ ] Chess Guide Overlay provides piece movement diagrams and tactical concepts
- [ ] Save/load preserves all game state including mid-mission progress
- [ ] All entities use UUIDs; event bus handles 15+ event types
- [ ] 60 FPS at 1080p on recommended hardware with 50 entities on screen

---

## Dependencies

| Dependency | From |
|-----------|------|
| Fully functional Home Base zone, Knight abilities, PBR rendering, input system | M1 |
| Client-server architecture, Netty packet protocol, ECS with UUIDs | M0-M1 |

## Risks

| Risk | Mitigation |
|------|-----------|
| 32 zones is heavy content workload | Use procedural generation heavily; reserve hand-built for primary structures only |
| AI companion behavior feels dumb or frustrating | Weekly playtests from Month 7; tune aggressiveness and reaction delays |
| Save file corruption with complex state | Integrity checksums on every save; automated load-after-save test in CI |
