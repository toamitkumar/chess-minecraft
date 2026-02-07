# M1 — Vertical Slice

**Timeline:** Months 3-5
**Version:** v0.2 (Internal Vertical Slice)
**Status:** Not Started
**Depends On:** M0

---

## Milestone Goal

Deliver one **fully playable, polished zone** (Home Base at a1) with the Knight possessing core abilities, one complete rescue mission, the key binding system, core rendering with PBR, and a basic alchemy system. This is the "one inch wide, one mile deep" build that proves the game loop works end-to-end.

## Working Build at Completion

> **v0.2 Vertical Slice:** The player launches the game, sees a title screen, configures key bindings (keyboard + mouse), enters the Home Base zone (a1 — Dark Castle biome, 256x256 blocks, full height). The Knight has 5 core abilities (L-Leap, Trample, Knight's Guard, Horse Kick, Shadow Gallop). The player explores the zone, fights White Pawn enemies, solves a chess puzzle, rescues 1 Pawn ally, brews a basic Elixir of Strength, and the rescued Pawn returns to the Home Base. PBR rendering, dynamic shadows, and day/night cycle are active. All gameplay runs through the server-authoritative tick loop.

---

## Deliverables & Tasks

### 1. Home Base Zone (a1) — Full Build

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Design zone layout: Dark Castle at a1 (256x256 blocks) | Level Design | [ ] | Top-down layout approved: castle, courtyard, training ground, alchemy station, Warp Board |
| 1.2 | Build Dark Castle main structure (schematic) | Level Design | [ ] | Multi-room castle with throne room, armory, prison, entrance hall |
| 1.3 | Build courtyard and training ground | Level Design | [ ] | Open area with combat dummies, obstacle course for L-Leap practice |
| 1.4 | Implement structure template system (schematics) | Engine | [ ] | Place pre-designed structures during world generation |
| 1.5 | Implement zone generation for a1 biome (Dark Castle type) | Engine | [ ] | Terrain: dark oak forest floor, obsidian outcrops, deepslate underground |
| 1.6 | Create 30+ block textures for Dark Castle biome (32x32 PBR) | Art | [ ] | Albedo + normal + roughness maps for obsidian, blackstone, dark oak, etc. |
| 1.7 | Implement alternating square color system (a1 is dark) | Engine | [ ] | Block palette shifts based on chess square color |
| 1.8 | Add chess coordinate marker at zone center ("a1" in gold blocks) | Art/Level | [ ] | Visible gold-block engraving at zone center |
| 1.9 | Implement underground dungeon area below castle | Level Design | [ ] | 2-3 room dungeon with enemies, traps, and loot chest |

### 2. Knight — Core Abilities (5 of 9)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Create Knight voxel model (final art, not placeholder) | Art | [ ] | Horse-mounted Knight in dark obsidian armor, horse-head helmet |
| 2.2 | Implement L-Leap with direction control | Gameplay | [ ] | 2+1 block L-pattern, hold F + direction. Cooldown: 3 seconds. Server-validated. |
| 2.3 | Implement Trample (charge attack) | Gameplay | [ ] | Charge 3 blocks forward, damage enemies in path. Server calculates damage. |
| 2.4 | Implement Knight's Guard (shield block) | Gameplay | [ ] | Hold RMB/L to reduce damage by 60%. Server validates damage reduction. |
| 2.5 | Implement Horse Kick (knockback) | Gameplay | [ ] | Rear kick, 5-block knockback on enemies behind. Server validates. |
| 2.6 | Implement Shadow Gallop (sprint buff) | Gameplay | [ ] | 2x speed for 5 seconds, dark particle trail. Stamina cost. |
| 2.7 | Implement stamina system (100 base) | Gameplay | [ ] | Stamina bar on HUD, abilities consume stamina, regenerates over time |
| 2.8 | Implement health system (20 HP / 10 hearts) | Gameplay | [ ] | Heart display on HUD, damage reduces hearts, death triggers respawn |
| 2.9 | Implement basic equipment: Pawn Sword + Knight Helm | Gameplay | [ ] | Craftable items with stat bonuses, visible on Knight model |

### 3. Knight — Full Animation Set

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Implement animation blending (upper/lower body) | Animation | [ ] | Knight can run + attack simultaneously |
| 3.2 | Create idle (mounted) animation — 60 frames | Art | [ ] | Horse shifts weight, Knight looks around |
| 3.3 | Create walk animation — 24 frames | Art | [ ] | Four-beat horse gait |
| 3.4 | Create run/gallop animation — 16 frames | Art | [ ] | Full gallop, cape billows |
| 3.5 | Create L-Leap launch + airborne + land animations | Art | [ ] | 8 + variable + 12 frames, purple particle trail |
| 3.6 | Create primary attack (lance thrust) — 10 frames | Art | [ ] | Visible thrust motion |
| 3.7 | Create secondary attack (heavy swing) — 18 frames | Art | [ ] | Wind-up + sweeping slash |
| 3.8 | Create block/shield raise — 6 frames | Art | [ ] | Shield up, horse turns |
| 3.9 | Create Horse Kick — 14 frames | Art | [ ] | 180° spin, rear kick |
| 3.10 | Create Trample charge — 20 frames | Art | [ ] | Lowered lance, forward charge |
| 3.11 | Create Shadow Gallop loop — 16 frames | Art | [ ] | Enhanced gallop with particle trail |
| 3.12 | Create take damage — 8 frames | Art | [ ] | Flinch + red flash |
| 3.13 | Create death — 36 frames | Art | [ ] | Fall off horse, dissolve to particles |
| 3.14 | Create drink elixir — 18 frames | Art | [ ] | Reach, lift, drink, toss bottle |
| 3.15 | Create interact — 12 frames | Art | [ ] | Extend hand toward object |
| 3.16 | Implement IK foot placement | Animation | [ ] | Horse feet adapt to uneven terrain |

### 4. Combat — White Pawn Enemies

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Create White Pawn voxel model (final art) | Art | [ ] | Humanoid in white quartz armor with short sword + shield |
| 4.2 | Create White Pawn animations: idle, march, attack, death | Art | [ ] | 4 animation sets as per PRD Section 16.4 |
| 4.3 | Implement Pawn AI: patrol forward, attack diagonally | AI | [ ] | Pawns march in lines, attack at diagonal 1-block range |
| 4.4 | Implement server-authoritative combat: damage calculation | Combat | [ ] | Server validates hit, calculates damage via `DamagePacket` |
| 4.5 | Implement enemy spawn system (patrol groups of 2-3 Pawns) | AI | [ ] | Pawns spawn at designated patrol points in the zone |
| 4.6 | Implement enemy health bars (visible above enemy) | UI | [ ] | White bar above Pawn shows remaining HP |
| 4.7 | Implement enemy death: drop loot + Pawn's Tear ingredient | Combat | [ ] | Pawn crumbles into white debris, drops items |
| 4.8 | Implement damage numbers (floating text on hit) | UI | [ ] | "-6" appears above enemy on hit (toggleable in settings) |

### 5. Rescue Mission — First Pawn Rescue

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Implement mission/quest system (server-side state machine) | Gameplay | [ ] | Missions tracked on server: discovery → approach → liberation → extraction |
| 5.2 | Design Pawn rescue mission: "The Lost Sentry" | Level Design | [ ] | Underground dungeon below Dark Castle, 3 rooms, chess puzzle at end |
| 5.3 | Build dungeon rooms with enemies, traps, and puzzle | Level Design | [ ] | Room 1: Pawn patrol, Room 2: pressure plate trap, Room 3: chess puzzle lock |
| 5.4 | Implement chess-themed puzzle: pressure plate checkmate | Gameplay | [ ] | Player must step on correct squares to recreate a checkmate pattern |
| 5.5 | Implement prison cell open animation + Pawn rescue cutscene | Animation | [ ] | Cell door swings open, Pawn steps out, acknowledges Knight (5-sec cutscene) |
| 5.6 | Implement rescued Pawn escort back to Home Base | AI | [ ] | Pawn follows Knight back to castle, enters Home Base |
| 5.7 | Implement quest log UI (N key) | UI | [ ] | Shows active missions, objectives, completion status |
| 5.8 | Implement quest tracker on HUD | UI | [ ] | Top-right corner shows current objective text |

### 6. Alchemy — Basic System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Implement ingredient item type + Ingredient Pouch (30 slots) | Gameplay | [ ] | Ingredients are distinct from regular items, stored in dedicated pouch |
| 6.2 | Implement 5 gatherable ingredients in Home Base zone | Gameplay | [ ] | Obsidian Dust (mining), Shadow Fern (harvesting), Pawn's Tear (enemy drop), Lava Ember (underground), Gold Flake (mining) |
| 6.3 | Build Alchemy Table craftable station | Gameplay | [ ] | Crafted from 4 Obsidian + 2 Gold Ingots + 1 Amethyst + 1 Brewing Stand |
| 6.4 | Implement brewing UI (1 base + 3 modifier + 1 fuel slots) | UI | [ ] | Drag ingredients into slots, press brew, 15-30 second timer |
| 6.5 | Implement Elixir of Strength (first elixir) | Gameplay | [ ] | Recipe: 1 Knight's Horseshoe Fragment + 2 Obsidian Dust + 1 Lava Ember. +50% melee damage for 120 seconds. |
| 6.6 | Implement Elixir of Swiftness (second elixir) | Gameplay | [ ] | Recipe: 2 Shadow Fern + 1 Morning Dew + 1 Gold Flake. +40% speed for 120 seconds. |
| 6.7 | Implement elixir active effects: particle aura + timer on HUD | Rendering/UI | [ ] | Red aura for strength, green for swiftness. Timer countdown visible. |
| 6.8 | Implement 2-elixir active limit | Gameplay | [ ] | Drinking a 3rd replaces the oldest. Server validates. |

### 7. Key Binding & Input System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 7.1 | Implement full keyboard + mouse input manager (GLFW) | Input | [ ] | All 40+ actions mappable, mouse look + LMB/RMB/MMB/scroll captured |
| 7.2 | Implement bindings configuration screen | UI | [ ] | Category tabs, rebind by pressing key/button, conflict detection, reset |
| 7.3 | Implement 5 preset profiles (Default KB+Mouse, Keyboard Only, Left-Handed, Arrow Keys, Compact) | Input | [ ] | Each profile loads correct bindings |
| 7.4 | Implement `bindings.json` save/load | Input | [ ] | Bindings persist across sessions, JSON format matches PRD spec |
| 7.5 | Implement first-time launch flow → bindings screen | UI | [ ] | New players see control scheme selection before gameplay |
| 7.6 | Implement import/export bindings as JSON file | Input | [ ] | Export button saves file; import button loads and applies |
| 7.7 | Implement mouse sensitivity, invert Y, raw input settings | Input | [ ] | All mouse settings adjustable and persisted |

### 8. Core Rendering — PBR Pipeline

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 8.1 | Implement PBR shader (albedo, normal, metallic, roughness, emissive) | Rendering | [ ] | Blocks render with correct PBR response under directional light |
| 8.2 | Implement cascaded shadow maps (CSM) | Rendering | [ ] | Blocks and entities cast dynamic shadows from sunlight |
| 8.3 | Implement SSAO (ambient occlusion) | Rendering | [ ] | Visible darkening in block crevices and corners |
| 8.4 | Implement day/night cycle (20-minute full cycle) | Rendering | [ ] | Sun/moon arc, sky color shifts, light intensity changes |
| 8.5 | Implement dynamic skybox (warm tones for Black side) | Rendering | [ ] | Sky has correct tone for Dark Castle biome |
| 8.6 | Implement basic particle system (dust, block debris) | Rendering | [ ] | Breaking blocks emits debris particles, walking on dirt emits dust |
| 8.7 | Implement TAA (Temporal Anti-Aliasing) | Rendering | [ ] | Voxel edges smooth without blurring textures |
| 8.8 | Implement basic bloom for emissive blocks | Rendering | [ ] | Lava, torches, glowing eyes emit bloom glow |
| 8.9 | Target: 60 FPS at 1080p, 12-chunk render distance | Rendering | [ ] | Benchmark on recommended hardware meets target |

### 9. UI Framework — Core Screens

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 9.1 | Implement title screen (New Game, Continue, Settings, Quit) | UI | [ ] | Keyboard + mouse navigable |
| 9.2 | Implement HUD (health, stamina, hotbar, quest tracker, minimap) | UI | [ ] | All elements from PRD Section 18.1 visible and functional |
| 9.3 | Implement pause menu (Resume, Settings, Save, Quit) | UI | [ ] | Escape opens pause, game pauses (server tick pauses in single-player) |
| 9.4 | Implement inventory screen (B key) | UI | [ ] | Grid inventory with equipment slots, ingredient pouch tab |
| 9.5 | Implement hotbar (1-9 keys, scroll wheel) | UI | [ ] | 9 slots, selected item highlighted, scroll cycles |
| 9.6 | Implement basic settings screen (video, audio, controls) | UI | [ ] | Core settings adjustable: resolution, FPS limit, volume, bindings link |

---

## Exit Criteria

All of the following must be true to close M1:

- [ ] Player can complete the full game loop: launch → configure bindings → explore Home Base → fight Pawns → find dungeon → solve puzzle → rescue Pawn → return to base
- [ ] All 5 Knight core abilities work and are server-validated
- [ ] Knight has full animation set (16 animations) with blending and IK
- [ ] White Pawn enemies patrol, attack, take damage, and die with animations
- [ ] Alchemy Table brews Elixir of Strength and Elixir of Swiftness
- [ ] PBR rendering with dynamic shadows, SSAO, bloom, and day/night cycle
- [ ] Key binding screen fully functional with 5 presets, save/load, import/export
- [ ] HUD displays health, stamina, hotbar, quest tracker, and minimap
- [ ] All gameplay state is server-authoritative (visible via debug overlay)
- [ ] 60 FPS at 1080p on recommended hardware
- [ ] Save/load works: player can quit and resume from the same state

---

## Dependencies

| Dependency | From |
|-----------|------|
| Voxel engine, client-server architecture, ECS framework | M0 |
| Knight placeholder model + movement | M0 |
| Block registry + chunk rendering | M0 |

## Risks

| Risk | Mitigation |
|------|-----------|
| PBR rendering tanks frame rate with voxel meshes | Profile early; implement LOD for distant chunks; scalable quality settings |
| Animation blending system too complex for timeline | Start with simple crossfade; add upper/lower body split as stretch goal |
| Rescue mission pacing feels wrong | Playtest internally weekly from Month 4; adjust room count and puzzle difficulty |
