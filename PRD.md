# Product Requirements Document (PRD)

## Minecraft Chess: The Knight's Crusade

**Version:** 1.4
**Date:** 2026-02-07
**Status:** Draft

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Product Vision & Objectives](#2-product-vision--objectives)
3. [Target Audience](#3-target-audience)
4. [Game World & Theme](#4-game-world--theme)
5. [Core Narrative & Gameplay](#5-core-narrative--gameplay)
6. [Player Character: The Knight](#6-player-character-the-knight)
7. [Rescue Mission & Fellow Chess Pieces](#7-rescue-mission--fellow-chess-pieces)
8. [Antagonist: The Evil White King](#8-antagonist-the-evil-white-king)
9. [Visual Graphics & Art Direction](#9-visual-graphics--art-direction)
10. [Technology Stack & Architecture](#10-technology-stack--architecture)
11. [Player Controls & Customizable Bindings](#11-player-controls--customizable-bindings)
12. [Player Options & Settings](#12-player-options--settings)
13. [Level Design & World Structure](#13-level-design--world-structure)
14. [Combat & Mechanics System](#14-combat--mechanics-system)
15. [Alchemy & Elixir System](#15-alchemy--elixir-system)
16. [Animation System](#16-animation-system)
17. [Audio Design](#17-audio-design)
18. [UI/UX Design](#18-uiux-design)
19. [Performance Requirements](#19-performance-requirements)
20. [Installation & Distribution](#20-installation--distribution)
21. [Post-Campaign: Chess vs Computer](#21-post-campaign-chess-vs-computer)
22. [Post-Campaign: Online Multiplayer Mode (FUTURE)](#22-post-campaign-online-multiplayer-mode-future--v20)
23. [Milestones & Release Plan](#23-milestones--release-plan)
24. [Success Metrics](#24-success-metrics)
25. [Risks & Mitigations](#25-risks--mitigations)
26. [Appendix](#26-appendix)

---

## 1. Executive Summary

**Minecraft Chess: The Knight's Crusade** is an adventure game that fuses the blocky, voxel-based aesthetic and sandbox mechanics of Minecraft with the strategic world of chess. The player assumes the role of a Black Knight who must traverse a vast, chess-themed Minecraft world to rescue captured allied chess pieces from the tyrannical Evil White King.

The game is **installed locally** on the player's machine as a standalone application and launches as a single-player experience. It is **architected from the ground up for multiplayer** — using a client-server model where even single-player runs an integrated local server — so that online co-op and competitive multiplayer can be enabled seamlessly in post-launch updates. Built with Java 21 + Kotlin, it delivers high-fidelity visuals with advanced rendering and supports both **keyboard and mouse controls** with fully customizable bindings.

---

## 2. Product Vision & Objectives

### Vision Statement

> Deliver an immersive, story-driven adventure where the strategic elegance of chess meets the creative exploration of Minecraft, wrapped in high-quality visuals and accessible controls — built on a multiplayer-ready architecture from day one so the journey can eventually be shared with others.

### Objectives

| ID | Objective | Measure of Success |
|----|-----------|-------------------|
| O1 | Build a fully explorable Minecraft-style chess world | World contains 8 distinct biome-zones mapped to an 8x8 board |
| O2 | Deliver a compelling rescue narrative | Player completes 15 rescue missions across the campaign |
| O3 | Achieve high-quality visual graphics | Runs at 60 FPS with PBR materials, dynamic lighting, and volumetric effects |
| O4 | Use modern Minecraft-aligned technology | Built on Java 21 / Kotlin with a custom voxel engine or Minecraft modding framework |
| O5 | Provide rich player options and settings | Feature parity with Minecraft's options screen (video, audio, controls, accessibility) |
| O6 | Support keyboard + mouse controls | All actions performable via keyboard with optional mouse support; pre-game binding configuration screen for both input methods |
| O7 | Local-first installation | Game installs and runs fully offline on the player's local machine; online multiplayer is a future post-launch feature |
| O8 | Multiplayer-ready architecture | All core systems (ECS, networking, game state, saves) built using client-server separation from v1.0 so multiplayer can be enabled without rewriting the engine |

---

## 3. Target Audience

| Segment | Description |
|---------|-------------|
| **Primary** | Minecraft players (ages 10-35) who enjoy adventure/story modes |
| **Secondary** | Chess enthusiasts looking for a creative, gamified chess experience |
| **Tertiary** | Indie game fans who appreciate unique genre mashups |

### Player Personas

**Persona 1 - "Block Builder Ben" (Age 14)**
Plays Minecraft daily. Loves exploration, building, and combat. Has basic chess knowledge. Wants an adventure mode with clear objectives.

**Persona 2 - "Strategic Sara" (Age 27)**
Avid chess player (1500+ ELO). Plays indie games on weekends. Drawn to the chess theme and strategic combat mechanics. Values customizable controls.

**Persona 3 - "Casual Charlie" (Age 22)**
Plays games casually. Attracted by the unique concept. Needs intuitive onboarding and accessible difficulty settings.

---

## 4. Game World & Theme

### 4.1 World Concept

The game world is a gigantic **8x8 chessboard** rendered in Minecraft's voxel art style, where each square is an expansive biome-zone that the player can freely explore. The world blends chess iconography with Minecraft block construction.

### 4.2 Chessboard Layout

```
     a      b      c      d      e      f      g      h
  +------+------+------+------+------+------+------+------+
8 | a8   | b8   | c8   | d8   | e8   | f8   | g8   | h8   |  8 (White Back Rank - Enemy Territory)
  | R    | Kn   | B    | Q    | K    | B    | Kn   | R    |
  +------+------+------+------+------+------+------+------+
7 | a7   | b7   | c7   | d7   | e7   | f7   | g7   | h7   |  7 (White Pawn Line - Fortified Wall)
  | P    | P    | P    | P    | P    | P    | P    | P    |
  +------+------+------+------+------+------+------+------+
6 | a6   | b6   | c6   | d6   | e6   | f6   | g6   | h6   |  6 (White Forward Posts)
  |      |      |      |      |      |      |      |      |
  +------+------+------+------+------+------+------+------+
5 | a5   | b5   | c5   | d5   | e5   | f5   | g5   | h5   |  5 (Neutral Zone - Mixed Terrain)
  |      |      |      |      |      |      |      |      |
  +------+------+------+------+------+------+------+------+
4 | a4   | b4   | c4   | d4   | e4   | f4   | g4   | h4   |  4 (Neutral Zone - Mixed Terrain)
  |      |      |      |      |      |      |      |      |
  +------+------+------+------+------+------+------+------+
3 | a3   | b3   | c3   | d3   | e3   | f3   | g3   | h3   |  3 (Black Forward Posts)
  |      |      |      |      |      |      |      |      |
  +------+------+------+------+------+------+------+------+
2 | a2   | b2   | c2   | d2   | e2   | f2   | g2   | h2   |  2 (Black Pawn Line - Home Defense)
  | P    | P    | P    | P    | P    | P    | P    | P    |
  +------+------+------+------+------+------+------+------+
1 | a1   | b1   | c1   | d1   | e1   | f1   | g1   | h1   |  1 (Black Back Rank - Home Base)
  | R    | Kn   | B    | Q    | K    | B    | Kn   | R    |
  +------+------+------+------+------+------+------+------+
     a      b      c      d      e      f      g      h
```

Each square is labeled using standard chess algebraic notation (file letter + rank number, e.g., `e4`, `d7`). These coordinates are displayed in-game as gold-block engravings at the center and borders of every zone, serving as navigation landmarks for the player.

### 4.3 Biome-Zone Types

Each square on the board is a unique explorable zone (approximately 256x256x128 blocks). The biome type is determined by board position:

| Zone Type | Board Position | Minecraft Biome Equivalent | Description |
|-----------|---------------|---------------------------|-------------|
| **Dark Castle** | Row 1 (a1-h1, Black Back Rank) | Deep Dark / Stronghold | The player's home base. Gothic black-stone castles, obsidian towers, dark oak forests. Starting area. |
| **Shadow Fields** | Row 2 (a2-h2, Black Pawns) | Dark Forest | Dense dark forests with fortified pawn outposts. Training grounds and supply caches. |
| **Twilight Outposts** | Row 3 (a3-h3) | Taiga / Spruce Forest | Forward operating bases at the edge of friendly territory. Watchtowers and scout camps. |
| **Contested Plains** | Rows 4-5 (a4-h5) | Plains / Savanna | Open battlefields with ruins, trenches, and scattered resources. Dynamic weather and day/night cycles heavily affect gameplay. |
| **Scorched Frontier** | Row 6 (a6-h6) | Badlands / Mesa | White's forward posts. Advance camps, siege weaponry, and forward patrols of the White army. Craters, destroyed structures, and hostile mobs. High risk, high reward. |
| **Marble Ramparts** | Row 7 (a7-h7, White Pawns) | Snowy Taiga / Ice Spikes | Fortified white-stone walls and pawn garrisons. The first major defensive line of the enemy. |
| **Ivory Citadel** | Row 8 (a8-h8, White Back Rank) | End-like / Custom | The Evil White King's domain. Towering white quartz and marble structures. Final confrontation area. |

### 4.4 Alternating Square Colors

- **Light squares** (e.g., a2, b1, c4, d3, e2, f1...): Built with quartz, sandstone, birch wood, and white concrete. Following standard chess coloring, a1 is dark and h1 is light.
- **Dark squares** (e.g., a1, b2, c3, d4, e5, f6...): Built with deepslate, blackstone, dark oak, and black concrete.
- Every zone border features its **algebraic coordinate** (e.g., "e4", "d7") engraved in gold blocks on all four entry points, matching standard chess notation. Coordinate markers are also embedded into the ground at the center of each zone as a navigational landmark.

### 4.5 World Generation Rules

- The world is **procedurally generated** within each zone using Minecraft-style terrain generation algorithms (Perlin/Simplex noise).
- Key structures (castles, dungeons, rescue locations) are placed via **structure templates** — pre-designed schematics placed during generation.
- The board perimeter is bounded by an **impassable void** (bedrock walls + void fog) styled as the edge of the chessboard — a cliff dropping into an infinite abyss.
- Vertical dimension is used extensively: underground dungeons, sky-high towers, and multi-level castles.

---

## 5. Core Narrative & Gameplay

### 5.1 Story Synopsis

The Black Kingdom has fallen. In a devastating surprise attack, the **Evil White King** and his army stormed across the board, capturing every piece of the Black side — Rooks, Bishops, the Queen, Pawns, and even the Black King himself. Only one piece escaped: **you, the Black Knight**.

Hidden in the ruins of the Dark Castle, you emerge as the last hope. Armed with your L-shaped agility and an unbreakable spirit, you must infiltrate enemy territory, free your allies one by one, and ultimately dethrone the Evil White King to restore balance to the board.

### 5.2 Narrative Structure

The story unfolds across **5 Acts**, each escalating in difficulty and narrative stakes:

| Act | Title | Zones | Objectives | Pieces Rescued |
|-----|-------|-------|------------|----------------|
| **Act I** | Awakening | Rows 1-2 | Tutorial, gather resources, learn abilities | 2 Pawns |
| **Act II** | Into the Fray | Rows 3-4 | Cross into contested territory, first major battles | 3 Pawns, 1 Bishop |
| **Act III** | Behind Enemy Lines | Rows 5-6 | Stealth missions, puzzle dungeons, ambushes | 3 Pawns, 1 Rook, 1 Knight |
| **Act IV** | Siege of the Ramparts | Row 7 | Full-scale assault on White Pawn defenses | 1 Bishop, 1 Rook |
| **Act V** | Checkmate | Row 8 | Confront the Evil White King, rescue the Queen & Black King | Queen, Black King |

### 5.3 Mission Types

Each rescue mission is a self-contained gameplay experience:

| Mission Type | Description | Example |
|-------------|-------------|---------|
| **Dungeon Crawl** | Navigate a multi-room dungeon with enemies, traps, and puzzles | Rescue the Rook from the White King's underground prison |
| **Stealth Infiltration** | Avoid detection while navigating enemy strongholds | Sneak past White Pawn patrols to free the Bishop |
| **Boss Battle** | Defeat a powerful White piece guarding a prisoner | Fight the White Queen to rescue the Black Queen |
| **Puzzle Challenge** | Solve chess-themed puzzles to unlock prison cells | Recreate a famous chess checkmate pattern on a pressure-plate board |
| **Escort Mission** | Protect a rescued piece as you guide them back to safety | Lead the freed Pawns through the Scorched Frontier |
| **Siege** | Command rescued allies in a large-scale battle | Storm the Marble Ramparts with your assembled army |

### 5.4 Progression System

- **Rescued pieces become allies** that provide passive bonuses and can be summoned as combat companions in later missions.
- **Chess-themed skill tree**: Abilities unlock as you rescue pieces (e.g., rescuing a Bishop grants diagonal dash attacks).
- **Resource gathering**: Mine blocks, craft weapons/armor themed after chess pieces, and build defenses at your home base.

---

## 6. Player Character: The Knight

### 6.1 Character Overview

The player controls the **Black Knight**, a nimble, horse-mounted warrior with unique movement capabilities inspired by the chess knight's L-shaped movement pattern.

### 6.2 Visual Design

- **Base form:** A humanoid figure in dark obsidian-black armor with horse-head helmet, rendered in Minecraft's blocky style but with higher polygon detail.
- **Mount:** A blocky warhorse made of dark oak and blackstone blocks, with glowing purple eyes (similar to Minecraft's horse entity but stylized).
- **Animations:** Fluid block-based animations for running, jumping, attacking, and the signature "L-leap".

### 6.3 Core Abilities

| Ability | Key Mechanic | Description | Unlock |
|---------|-------------|-------------|--------|
| **L-Leap** | Signature Move | Jump in an L-shaped pattern (2 blocks forward + 1 block sideways, or vice versa). Can leap over obstacles and enemies. | Default |
| **Trample** | Melee Attack | Charge forward, damaging enemies in a straight line (3 blocks). | Default |
| **Knight's Guard** | Defense | Raise shield to block incoming damage. Reduces damage by 60%. | Default |
| **Horse Kick** | Knockback | Powerful backward kick that sends enemies flying 5 blocks. | Default |
| **Shadow Gallop** | Movement | Sprint at 2x speed for 5 seconds. Leaves a trail of dark particles. | Act I |
| **Fork Strike** | AOE Attack | Attack two enemies simultaneously (chess fork reference). Unlocked by rescuing the first Bishop. | Act II |
| **Diagonal Slash** | Ranged Attack | Send a blade of energy diagonally (Bishop power). Unlocked by rescuing both Bishops. | Act III |
| **Castle Charge** | Ultimate | Swap positions with a summoned Rook ally, dealing massive damage at both locations (castling reference). | Act IV |
| **Queen's Wrath** | Ultimate | Temporarily gain the Queen's power — move and attack in all 8 directions with devastating force. | Act V |

### 6.4 Health & Stats

| Stat | Base Value | Max (Fully Upgraded) |
|------|-----------|---------------------|
| Health (HP) | 20 (10 hearts) | 40 (20 hearts) |
| Armor Rating | 8 | 20 |
| Attack Damage | 6 | 15 |
| Movement Speed | 4.3 blocks/sec | 7.0 blocks/sec |
| L-Leap Range | 3 blocks | 6 blocks |
| Stamina | 100 | 200 |

### 6.5 Equipment Slots

| Slot | Items |
|------|-------|
| Head | Knight Helm (various tiers: Wood, Stone, Iron, Diamond, Obsidian) |
| Chest | Knight Plate |
| Legs | Knight Greaves |
| Feet | Horse Shoes (affect speed & L-Leap distance) |
| Main Hand | Lance / Sword (various tiers) |
| Off Hand | Shield / Banner |

---

## 7. Rescue Mission & Fellow Chess Pieces

### 7.1 Rescuable Pieces Overview

Each rescued piece becomes an NPC ally with unique abilities that aid the player:

| Piece | Count | Rescue Location | Ability Granted to Player | Companion Power |
|-------|-------|----------------|--------------------------|-----------------|
| **Pawns** | 8 | Scattered across Rows 2-6 | +1 Attack per Pawn rescued | Form defensive walls, can promote to stronger units |
| **Bishop (Left)** | 1 | Row 4, Dark Square Dungeon | Diagonal Slash attack | Heals player from a distance |
| **Bishop (Right)** | 1 | Row 7, Light Square Cathedral | Enhanced Diagonal Slash | Provides light-based AOE damage |
| **Rook (Left)** | 1 | Row 6, Fortress Prison | Castle Charge ability | Provides ranged artillery support |
| **Rook (Right)** | 1 | Row 7, Watchtower | Enhanced Castle Charge | Creates stone walls for cover |
| **Knight (Ally)** | 1 | Row 5, Captured Stable | +2 L-Leap range | Fights alongside player in combat |
| **Queen** | 1 | Row 8, Ivory Dungeon | Queen's Wrath ultimate | The most powerful ally — all-directional attacks |
| **Black King** | 1 | Row 8, Throne Room | Victory Condition | Restores the kingdom once freed |

### 7.2 Rescue Mechanics

1. **Discovery Phase:** The player must find clues (maps, notes, NPC dialogue) revealing the prisoner's location.
2. **Approach Phase:** Navigate to the prison zone, dealing with environmental hazards and enemy patrols.
3. **Liberation Phase:** Complete the mission-specific challenge (combat, puzzle, stealth) to free the piece.
4. **Extraction Phase:** Safely escort the rescued piece back to a rally point or the home base.

### 7.3 Piece Interaction System

- Rescued pieces reside at the **Home Base** (Row 1, a1-h1 Dark Castle) when not deployed.
- The player can **summon up to 2 companion pieces** at a time during missions.
- Companions have their own health bars and can be "captured" (temporarily disabled) if they take too much damage.
- Companions can be issued simple commands: **Follow**, **Hold Position**, **Attack Target**, **Return to Base**.

### 7.4 AI-Controlled Black Pieces in Combat

During combat encounters, **all rescued Black pieces on the battlefield are controlled by the computer AI**, not the player. This creates a dynamic, chess-like battlefield where the player controls only the Knight while the AI operates allies strategically.

#### AI Companion Behavior System

| Behavior Mode | Description | When Active |
|--------------|-------------|-------------|
| **Aggressive** | AI actively seeks out and attacks the nearest White enemy using the piece's chess-inspired movement pattern. | Default when enemies are within 16 blocks |
| **Defensive** | AI holds position and only attacks enemies that come within melee range. Prioritizes protecting the player. | When player HP drops below 30% |
| **Strategic** | AI attempts to form chess-like formations — Rooks control lanes, Bishops control diagonals, Pawns form shield walls. | Boss encounters and siege missions |
| **Retreat** | AI pulls back toward the player and avoids engagement. Used when a companion is low HP. | When companion HP drops below 20% |

#### AI Decision-Making per Piece Type

| Piece | AI Movement Logic | AI Combat Logic | AI Special Behavior |
|-------|------------------|-----------------|-------------------|
| **Pawns** | Move forward toward enemies in a line. Never retreat voluntarily. | Attack diagonally at 1-block range, mimicking chess Pawn captures. | Auto-form shield walls when 3+ Pawns are near each other. Will attempt promotion if near Row 8. |
| **Bishops** | Move diagonally, stay on their assigned square color. Maintain distance from enemies. | Cast ranged diagonal beam attacks. Heal the player when not attacking. | Avoid melee range; reposition if cornered. |
| **Rooks** | Move in straight lines (N/S/E/W). Hold chokepoints and corridors. | Ram-charge enemies in straight lines. Provide artillery support from range. | Create stone-wall cover at strategic positions. |
| **Knight (Ally)** | Jump in L-patterns to flank enemies. Unpredictable positioning. | Strike on landing with AOE damage. | Coordinate L-leaps with the player's attacks for combo damage. |
| **Queen** | Move freely in all 8 directions. The most aggressive AI companion. | Use combination Bishop + Rook attacks. Prioritize high-value targets (White Queen, White King). | Will sacrifice position to protect the player from lethal damage. |

#### AI Difficulty Scaling

The intelligence and effectiveness of AI-controlled Black pieces scales with the game difficulty:

| Difficulty | AI Companion Behavior |
|-----------|----------------------|
| Peaceful | Companions are invulnerable; AI is highly effective. |
| Easy | Companions take 50% reduced damage; AI makes good tactical decisions. |
| Normal | Standard companion HP and AI intelligence. Occasional suboptimal moves. |
| Hard | Companions are fragile; AI is less responsive (0.5s reaction delay). |
| Grandmaster | Companions can be permanently lost if captured; AI follows strict chess movement rules only. |

#### Player Influence Over AI

While the computer fully controls companion movement and attacks, the player can influence overall AI behavior through:

- **Companion Command key** (`G`): Cycle the global AI stance — Aggressive / Defensive / Strategic / Retreat.
- **Rally Point** (`H` + direction): Set a rally point where companions will regroup.
- **Target Ping** (`Tab` on a locked target): Suggest a priority target for all companions to focus.

The player **never directly controls** companion movement or attacks during combat. This preserves the feeling of commanding an army of chess pieces that move according to their own rules.

### 7.5 Pawn Promotion Mechanic

When a rescued Pawn reaches Row 8 (the enemy's back rank) during a mission, the Pawn can **promote** into a Bishop, Rook, or Knight — granting the player an additional companion of that type. This mirrors the chess promotion rule and adds strategic depth.

---

## 8. Antagonist: The Evil White King

### 8.1 Character Overview

The **Evil White King** is the primary antagonist — a towering, menacing figure clad in gleaming white quartz armor with a crown of gold and diamond blocks. He rules from the **Ivory Citadel** at Row 8 and commands the entire White army.

### 8.2 Motivation

The White King believes in absolute order and seeks to eliminate the Black side entirely, considering them chaotic and unworthy of existing on the board. His obsession with "purity" and control drives his conquest.

### 8.3 White Army Hierarchy

| Enemy Rank | Type | Behavior | Difficulty |
|-----------|------|----------|------------|
| **White Pawns** | Foot Soldiers | Patrol in lines, attack forward only. Can promote if they reach Row 1. | Easy |
| **White Knights** | Cavalry | Leap in L-patterns, flank the player. Fast and unpredictable. | Medium |
| **White Bishops** | Mages | Attack diagonally with ranged magic. Stay on their square color. | Medium |
| **White Rooks** | Siege Units | Move in straight lines, deal heavy damage. Slow but devastating. | Hard |
| **White Queen** | Boss / General | Moves in all directions, extremely fast and powerful. Mini-boss encounter. | Very Hard |
| **White King** | Final Boss | Moves one square at a time but has massive HP, summons reinforcements, and uses devastating area attacks. | Extreme |

### 8.4 Final Boss Fight: The White King

The final encounter is a multi-phase battle:

- **Phase 1 — The Court:** Fight through the King's personal guard (elite White pieces) in the Throne Room.
- **Phase 2 — The King Attacks:** The King enters the fight. He moves slowly but creates shockwaves, summons Pawn walls, and calls in Rook artillery strikes.
- **Phase 3 — Checkmate:** The player must maneuver rescued allies into specific board positions to create a "checkmate" formation, trapping the King. This requires both combat skill and chess knowledge.

---

## 9. Visual Graphics & Art Direction

### 9.1 Art Style

The game employs a **high-fidelity voxel art style** — retaining Minecraft's iconic blocky aesthetic while pushing visual quality significantly higher through modern rendering techniques.

### 9.2 Rendering Pipeline

| Feature | Implementation | Details |
|---------|---------------|---------|
| **Physically Based Rendering (PBR)** | All block textures use PBR materials | Albedo, normal, metallic, roughness, and emissive maps per block type |
| **Ray-Traced Global Illumination** | Hybrid ray tracing (optional) | Real-time GI for accurate light bouncing in dungeons and interiors |
| **Volumetric Lighting** | God rays, fog, and atmospheric scattering | Dramatic lighting in cathedrals, dungeons, and open battlefields |
| **Dynamic Shadows** | Cascaded shadow maps (CSM) | All entities and blocks cast real-time shadows |
| **Screen-Space Reflections (SSR)** | Water and polished surfaces | Reflective marble floors in the Ivory Citadel, water in moats |
| **Ambient Occlusion** | SSAO / GTAO | Adds depth and realism to block geometry |
| **Bloom & HDR** | Post-processing pipeline | Glowing emissive blocks (redstone, lava, enchantment effects) |
| **Particle Systems** | GPU-accelerated particles | Dust, sparks, magic effects, block breaking debris, weather |
| **LOD System** | Level of Detail for distant terrain | Smooth transitions from full-detail to simplified geometry |
| **Anti-Aliasing** | TAA (Temporal Anti-Aliasing) | Smooths jagged voxel edges while preserving sharpness |

### 9.3 Texture & Block Quality

| Specification | Value |
|--------------|-------|
| Base texture resolution | 32x32 pixels per block face (2x Minecraft default) |
| High-res texture pack (optional) | 64x64 or 128x128 per block face |
| Unique block types | 200+ chess-themed blocks |
| Normal map detail | Full per-pixel normal mapping for surface detail |
| Animation frames | Up to 32 frames for animated textures (lava, water, magic) |

### 9.4 Chess-Themed Visual Elements

| Element | Visual Treatment |
|---------|-----------------|
| **Black Pieces** | Dark obsidian, blackstone, crying obsidian, purple/dark particle effects |
| **White Pieces** | Quartz, diorite, calcite, snow, gold accents, white/yellow particle effects |
| **Board Squares** | Alternating light/dark biomes with clear borders and coordinate markers |
| **Piece Models** | Custom 3D voxel models for each chess piece type (higher poly than standard mobs) |
| **Sky** | Dynamic skybox that shifts: warm tones on Black side, cold/stark on White side |
| **Transitions** | Gradient blending between biome-zones with chess-notation signposts |

### 9.5 Visual Effects for Abilities

| Ability | VFX |
|---------|-----|
| L-Leap | Purple trail particles, ground impact ring, camera shake on landing |
| Trample | Dust clouds, screen shake, block debris flying outward |
| Diagonal Slash | Golden energy arc cutting diagonally, sliced blocks crumble |
| Castle Charge | Rook and Knight swap with a burst of stone particles and a flash of light |
| Queen's Wrath | Radiant golden aura, 8-directional energy beams, screen tint to gold |

### 9.6 Minimum Visual Quality Targets

| Metric | Target |
|--------|--------|
| Frame Rate | 60 FPS at 1080p on recommended hardware |
| Draw Distance | 16 chunks (256 blocks) minimum |
| Texture Filtering | Anisotropic (16x) |
| Color Depth | HDR (10-bit output support) |
| Resolution Support | 720p to 4K, with dynamic resolution scaling |

---

## 10. Technology Stack & Architecture

### 10.1 Core Technology Alignment with Minecraft

The game uses modern technologies that align with Minecraft's current and upcoming technical direction:

| Component | Technology | Minecraft Alignment |
|-----------|-----------|-------------------|
| **Programming Language** | Java 21 (LTS) + Kotlin | Minecraft Java Edition is built on Java; Kotlin provides modern language features while maintaining JVM compatibility |
| **Rendering Engine** | Custom OpenGL 4.6 / Vulkan renderer | Minecraft is transitioning toward modern graphics APIs; Vulkan support aligns with future direction |
| **Voxel Engine** | Custom chunk-based engine | Same 16x16x16 sub-chunk architecture as Minecraft |
| **World Generation** | Multi-octave Simplex noise | Same noise-based generation approach as Minecraft's terrain |
| **Entity System** | ECS (Entity Component System) | Modern game architecture pattern; Minecraft Bedrock uses a data-driven entity system |
| **Data Formats** | JSON + NBT (Named Binary Tag) | Minecraft uses JSON for resource packs and NBT for world data |
| **Resource System** | Resource pack architecture | Same structure as Minecraft resource packs (textures, models, sounds, languages) |
| **Networking** | Netty-based packet system (active from v1.0) | Same networking library as Minecraft. Single-player uses an integrated local server; same protocol serves remote multiplayer later. |
| **Auth & Accounts (future)** | OAuth2 / custom auth service | Player accounts for online identity, friends, ELO persistence. Offline play uses a local profile. |
| **Build System** | Gradle with Kotlin DSL | Same build system as modern Minecraft modding |
| **Physics** | Custom AABB collision + JBullet | Block-based collision consistent with Minecraft's physics model |

### 10.2 System Architecture (Multiplayer-Ready from Day One)

The architecture follows a **client-server model even in single-player**. In v1.0, the client and an integrated server run in the same process (same as Minecraft's "Open to LAN" model). This means every system is already split across the client/server boundary, making future networked multiplayer a configuration change rather than a rewrite.

```
+====================================================================+
|                     CLIENT (Player's Machine)                       |
+====================================================================+
|  Presentation Layer (Client-Only)                                   |
|  +-------------------+  +------------------+  +------------------+ |
|  | Rendering Engine  |  | UI Framework     |  | Audio Engine     | |
|  | (OpenGL/Vulkan)   |  | (ImGui / Custom) |  | (OpenAL)         | |
|  +-------------------+  +------------------+  +------------------+ |
|  +-------------------+  +------------------+                       |
|  | Input Manager     |  | Animation Player |                       |
|  | (GLFW: KB+Mouse)  |  | (Client-side)    |                       |
|  +-------------------+  +------------------+                       |
+--------------------------------------------------------------------+
|  Client Game Layer                                                  |
|  +-------------------+  +------------------+  +------------------+ |
|  | Prediction Engine |  | Interpolation    |  | Chunk Mesh Cache | |
|  | (Client-side pred)|  | (Entity smoothing)|  | (Client render) | |
|  +-------------------+  +------------------+  +------------------+ |
+====================================================================+
         |  Packets (Netty)  |         |  Packets (Netty)  |
         |  (localhost:25565  |         |  (remote server   |
         |   in single-player)|         |   in multiplayer)  |
+====================================================================+
|                     SERVER (Authoritative Game State)                |
+====================================================================+
|  Game Logic Layer (Server-Authoritative)                            |
|  +-------------------+  +------------------+  +------------------+ |
|  | ECS World         |  | Mission System   |  | AI System        | |
|  | (Entities, Comps) |  | (Quest Engine)   |  | (Enemy + Ally AI)| |
|  +-------------------+  +------------------+  +------------------+ |
|  +-------------------+  +------------------+  +------------------+ |
|  | Combat System     |  | Skill Tree       |  | Dialogue System  | |
|  | (Dmg, Abilities,  |  | (Progression)    |  | (NPC state)      | |
|  |  Hit Validation)  |  |                  |  |                  | |
|  +-------------------+  +------------------+  +------------------+ |
|  +-------------------+  +------------------+  +------------------+ |
|  | Trade System      |  | ELO / Ranking    |  | Player Manager   | |
|  | (Server-validated)|  | (Server-only)    |  | (Auth, sessions) | |
|  +-------------------+  +------------------+  +------------------+ |
+--------------------------------------------------------------------+
|  World Layer (Server-Authoritative)                                 |
|  +-------------------+  +------------------+  +------------------+ |
|  | Chunk Manager     |  | World Generator  |  | Block Registry   | |
|  | (Load/Unload)     |  | (Noise + Structs)|  | (Block Types)    | |
|  +-------------------+  +------------------+  +------------------+ |
|  +-------------------+  +------------------+                       |
|  | Structure Placer  |  | Lighting Engine  |                       |
|  | (Schematics)      |  | (Block + Sky)    |                       |
|  +-------------------+  +------------------+                       |
+--------------------------------------------------------------------+
|  Platform Layer                                                     |
|  +-------------------+  +------------------+  +------------------+ |
|  | Tick Scheduler    |  | File I/O         |  | Config / Save    | |
|  | (20 TPS loop)     |  | (NBT + JSON)     |  | System           | |
|  +-------------------+  +------------------+  +------------------+ |
+====================================================================+

Single-Player:  Client + Integrated Server in ONE process (localhost)
Co-op (v1.5):   Client connects to a player-hosted dedicated server
Online (v2.0):  Client connects to cloud-hosted dedicated servers
```

### 10.3 Multiplayer-Ready Design Principles

Every system in the codebase must follow these principles from the first line of code:

| Principle | Description | Enforcement |
|-----------|-------------|-------------|
| **Server-Authoritative State** | The server owns all game state: entity positions, HP, inventory, block data, quest progress. The client is a thin view layer that sends inputs and receives state updates. | Code review: no game-state mutations on the client side. |
| **Deterministic Game Logic** | All game logic (combat damage, AI decisions, crafting results) must produce identical results given identical inputs, regardless of which machine runs it. | Unit tests verify determinism; no use of `System.currentTimeMillis()` in game logic — use server tick count. |
| **Entity UUIDs** | Every entity (player, NPC, mob, dropped item) is identified by a UUID, not a local object reference. This allows entities to be referenced across network boundaries. | ECS framework enforces UUID-based entity lookup. |
| **Packet-Based Communication** | All client-server interaction goes through a defined packet protocol (Netty). Even in single-player, the client sends input packets to the integrated server thread and receives state packets back. | No direct method calls between client and server code. Shared interfaces communicate only via packets. |
| **Tick-Based Simulation** | The server runs at a fixed 20 TPS (ticks per second), matching Minecraft. All game logic is tick-driven, not frame-driven. The client interpolates between ticks for smooth rendering. | Server loop is independent of frame rate. Client-side rendering interpolates entity positions between the last two received server states. |
| **Stateless Client Rendering** | The client renders based on the last received server state. It does not maintain its own "truth" about the world. Client-side prediction is used only for player movement (rolled back if server disagrees). | Client-side prediction limited to local player movement, camera, and UI. |
| **Serializable Inventories & State** | All player data (inventory, equipment, quest state, ELO, skills) is serializable to JSON/NBT and can be transmitted over the network or stored in a database. | All data classes implement a `Serializable` interface with `toNBT()` / `fromNBT()` methods. |
| **Player Session Abstraction** | The server tracks players via a `PlayerSession` object that is agnostic to whether the player is local or remote. In single-player, there is one session. In multiplayer, there are many. | `PlayerSession` is the only way server code interacts with player connections. |
| **Chunk Streaming Protocol** | Chunks are sent from server to client via a streaming protocol. The client requests chunks within render distance; the server sends them as compressed packets. | Same protocol in single-player (in-memory) and multiplayer (over network). The only difference is transport latency. |
| **Event Bus for Cross-System Communication** | Systems communicate via a typed event bus (e.g., `PlayerDamageEvent`, `BlockBreakEvent`, `ItemTradeEvent`). Events can be serialized for network replay and logging. | All inter-system communication goes through events, not direct calls. |

### 10.4 Key Libraries & Frameworks

| Library | Purpose | Version | Used From |
|---------|---------|---------|-----------|
| **LWJGL 3** | OpenGL/Vulkan bindings, GLFW windowing, OpenAL audio | 3.3.x | v1.0 |
| **GLFW** | Window creation, keyboard + mouse input handling | Via LWJGL | v1.0 |
| **OpenAL Soft** | 3D positional audio | Via LWJGL | v1.0 |
| **Gson / Jackson** | JSON serialization for configs, resource packs, bindings, player data | Latest | v1.0 |
| **Netty** | Client-server packet communication (localhost in v1.0, remote in v1.5+) | 4.1.x | **v1.0** |
| **JBullet** | Physics simulation | 2.x | v1.0 |
| **FastNoise Lite** | World generation noise functions | Latest | v1.0 |
| **Dear ImGui (Java)** | Debug UI and development tools | Latest | v1.0 |
| **Protocol Buffers (protobuf)** | Efficient binary serialization for network packets | 3.x | **v1.0** |
| **SQLite / H2** | Lightweight embedded database for player profiles, ELO history, trade logs | Latest | v1.0 (local), v2.0 (server-side PostgreSQL) |
| **BCrypt** | Password hashing for player accounts (online mode) | Latest | v2.0 |
| **JWT (jjwt)** | Authentication tokens for online sessions | Latest | v2.0 |

### 10.5 Chunk & Rendering Architecture

```
World (Server-side)
 +-- Region (32x32 chunks, stored on server/disk)
      +-- Chunk Column (16-wide, world-height, 16-deep)
           +-- Sub-Chunk (16x16x16 blocks)
                +-- Block Palette (compressed block storage)
                +-- Light Data (block light + sky light, 4 bits each)

Client Chunk Cache (Client-side)
 +-- Received chunks (within render distance)
      +-- Sub-Chunk
           +-- Block Palette (decompressed from server packet)
           +-- Light Data (received from server)
           +-- Mesh (generated client-side via greedy meshing)
```

- **Greedy meshing** reduces draw calls by merging adjacent same-type block faces (client-side only).
- **Frustum culling** skips rendering chunks outside the camera view (client-side).
- **Occlusion culling** skips chunks fully hidden behind terrain (client-side).
- **Multi-threaded chunk loading**: Mesh building runs on client worker threads; world generation runs on server worker threads.
- **Chunk streaming**: Server sends chunk data to the client as compressed packets. In single-player, this is an in-memory transfer (zero latency). In multiplayer, packets are sent over the network and prioritized by distance to the player.
- **Delta updates**: After initial chunk send, only block changes within a chunk are transmitted (not the full chunk), reducing bandwidth for multiplayer.

### 10.6 Save System & Data Architecture

All persistent data is structured for both local storage (v1.0) and future server-side database storage (v2.0):

| Data Type | Local Storage (v1.0) | Server Storage (v2.0) | Sync Strategy |
|-----------|---------------------|----------------------|---------------|
| **World blocks** | Region files (Anvil-like format) on disk | Same region files on dedicated server disk | Server-authoritative; clients never write world data |
| **Player profile** | `player.json` — name, stats, campaign progress, ELO | PostgreSQL `players` table | Local profile uploads to server on first online login; server is authoritative thereafter |
| **Inventory & equipment** | `inventory.nbt` — items, elixirs, ingredients | PostgreSQL `inventories` table (serialized NBT blob) | Server validates all inventory changes; client displays server state |
| **Quest / mission state** | `quests.json` — active, completed, progress | PostgreSQL `quest_progress` table | Single-player: local. Co-op: server-authoritative with shared quest state. |
| **Rescued pieces** | `companions.json` — rescued list, companion levels, AI stance | PostgreSQL `companions` table | Per-player in single-player. Shared pool in co-op. |
| **Key bindings** | `bindings.json` — keyboard + mouse config | Local only (never synced to server) | Always local. Players bring their own bindings. |
| **Settings** | `options.json` — video, audio, gameplay, accessibility | Local only | Always local. |
| **ELO rating** | `elo.json` — rating, history, tier | PostgreSQL `elo_ratings` table with full match history | Local in v1.0 (for chess vs computer). Server-authoritative in v2.0 for PvP. |
| **Trade history** | Not applicable in v1.0 | PostgreSQL `trade_log` table with rollback support | Server-only. Full audit trail for dispute resolution. |

- Auto-save every 5 minutes with up to 3 rotating backup slots.
- All save data uses **versioned schemas** — save files include a format version number so older saves can be migrated forward.
- Player data classes implement `Serializable` with `toNBT()` / `fromNBT()` and `toJSON()` / `fromJSON()` methods, usable for both disk I/O and network transmission.

### 10.7 Networking Protocol

Even though v1.0 is single-player, the networking protocol is implemented from the start:

| Packet Category | Direction | Examples | Used In |
|----------------|-----------|----------|---------|
| **Handshake** | Client → Server | `LoginPacket`, `ProtocolVersionPacket` | v1.0+ |
| **Player Input** | Client → Server | `MovePacket`, `AttackPacket`, `InteractPacket`, `UseItemPacket` | v1.0+ |
| **World State** | Server → Client | `ChunkDataPacket`, `BlockChangePacket`, `LightUpdatePacket` | v1.0+ |
| **Entity State** | Server → Client | `EntitySpawnPacket`, `EntityMovePacket`, `EntityRemovePacket`, `EntityAnimationPacket` | v1.0+ |
| **Player State** | Server → Client | `HealthUpdatePacket`, `InventoryUpdatePacket`, `QuestUpdatePacket`, `ELOUpdatePacket` | v1.0+ |
| **Combat** | Bidirectional | `DamagePacket`, `AbilityUsePacket`, `DeathPacket`, `RespawnPacket` | v1.0+ |
| **Chat / Social** | Bidirectional | `ChatMessagePacket`, `EmotePacket` | v1.5+ |
| **Trade** | Bidirectional | `TradeRequestPacket`, `TradeOfferPacket`, `TradeConfirmPacket`, `TradeCancelPacket` | v2.0 |
| **PvP** | Server → Client | `DuelRequestPacket`, `DuelResultPacket`, `PvPZonePacket` | v2.0 |
| **Matchmaking** | Client → Server | `QueuePacket`, `MatchFoundPacket` | v2.0 |

In single-player, all packets are routed through an in-memory channel (zero serialization overhead). When multiplayer is enabled, the same packets are serialized with Protocol Buffers and sent over TCP via Netty.

---

## 11. Player Controls & Customizable Bindings

### 11.1 Overview

The game supports **both keyboard and mouse controls**, matching the standard Minecraft control scheme that players are familiar with. The keyboard handles movement, actions, and hotbar selection, while the mouse handles camera look, primary/secondary actions, and UI interaction. Before starting a new game, the player is presented with a **Bindings Configuration Screen** where every keyboard and mouse action can be remapped.

Players can choose between two control schemes:

| Scheme | Description | Best For |
|--------|-------------|----------|
| **Keyboard + Mouse (Default)** | Mouse controls camera look and primary/secondary attacks. Keyboard handles movement, abilities, and inventory. Matches standard Minecraft controls. | Most players; familiar and intuitive |
| **Keyboard Only** | All actions including camera control mapped to keyboard. Mouse disabled during gameplay. | Accessibility; players without mouse; laptop trackpad avoidance |

### 11.2 Default Mouse Bindings

| Action | Default Binding | Description |
|--------|----------------|-------------|
| **Look / Aim** | Mouse Movement | Rotate camera horizontally and vertically. Sensitivity adjustable in settings. |
| **Primary Attack** | Left Mouse Button (LMB) | Basic melee attack with equipped weapon (same as keyboard `J`) |
| **Secondary Attack / Block** | Right Mouse Button (RMB) | Heavy/charged attack, or raise shield when no weapon ability is active (same as keyboard `K`/`L`) |
| **Pick Block / Interact** | Middle Mouse Button (MMB) | Interact with targeted object, NPC, or door (same as keyboard `E`) |
| **Hotbar Scroll** | Scroll Wheel | Scroll through hotbar slots 1-9 |
| **Zoom** | Scroll Wheel (while holding `Left Alt`) | Zoom camera in/out (adjust FOV) |
| **L-Leap Target** | LMB (while holding `F`) | Click a valid L-Leap destination to execute the leap toward that point |

#### Mouse Settings

| Setting | Options | Default |
|---------|---------|---------|
| Mouse Sensitivity | 1-200% (slider) | 100% |
| Invert Y-Axis | Off / On | Off |
| Raw Input | Off / On | On |
| Mouse Acceleration | Off / On | Off |
| Scroll Sensitivity | 1-10 | 5 |
| LMB/RMB Swap | Off / On | Off |

### 11.3 Default Keyboard Bindings

#### Movement

| Action | Default Key | Description |
|--------|------------|-------------|
| Move Forward | `W` | Move the character forward |
| Move Backward | `S` | Move the character backward |
| Move Left | `A` | Strafe left |
| Move Right | `D` | Strafe right |
| Jump | `Space` | Jump (1 block height) |
| Sprint | `Left Ctrl` | Toggle sprint mode (1.5x speed) |
| Crouch / Sneak | `Left Shift` | Slow movement, prevents falling off edges |
| L-Leap | `F` | Execute the Knight's signature L-shaped jump |
| L-Leap Direction | `W/A/S/D` (during L-Leap) | Determines the direction of the L-Leap |

#### Camera (Keyboard-Only Mode / Alternative Keys)

In Keyboard + Mouse mode, camera look is handled by the mouse. These keys serve as alternatives or are used in Keyboard-Only mode:

| Action | Default Key | Description |
|--------|------------|-------------|
| Look Up | `Up Arrow` | Rotate camera upward (keyboard-only mode, or fine adjustment) |
| Look Down | `Down Arrow` | Rotate camera downward |
| Look Left | `Left Arrow` | Rotate camera left |
| Look Right | `Right Arrow` | Rotate camera right |
| Toggle Camera Mode | `F5` | Cycle: First-person / Third-person back / Third-person front |
| Zoom In | `=` (Equals) | Zoom camera in (reduce FOV) |
| Zoom Out | `-` (Minus) | Zoom camera out (increase FOV) |

#### Combat

| Action | Default Key | Mouse Alternative | Description |
|--------|------------|-------------------|-------------|
| Primary Attack | `J` | LMB | Basic melee attack with equipped weapon |
| Secondary Attack | `K` | — | Heavy/charged attack |
| Block / Shield | `L` | RMB (hold) | Raise shield to block incoming attacks |
| Ability 1 | `U` | First special ability (contextual) |
| Ability 2 | `I` | Second special ability (contextual) |
| Ability 3 | `O` | Third special ability (contextual) |
| Ultimate Ability | `P` | Ultimate ability (when charged) |
| Lock-On Target | `Tab` | Lock camera onto nearest enemy |
| Switch Target | `Q` | Cycle through nearby enemies while locked on |

#### Interaction

| Action | Default Key | Description |
|--------|------------|-------------|
| Interact / Use | `E` | Interact with objects, NPCs, doors, levers |
| Open Inventory | `B` | Open inventory screen |
| Open Map | `M` | Open the world map (chessboard overview) |
| Open Quest Log | `N` | View active and completed missions |
| Open Skill Tree | `T` | View and upgrade abilities |
| Companion Command | `G` | Cycle companion command (Follow/Hold/Attack/Return) |
| Summon Companion | `H` | Open companion summon menu |
| Quick Save | `F6` | Save game immediately |
| Quick Load | `F7` | Load most recent save |
| Pause Menu | `Escape` | Open pause / settings menu |

#### Hotbar & Items

| Action | Default Key | Description |
|--------|------------|-------------|
| Hotbar Slot 1-9 | `1` - `9` | Select item in hotbar slot |
| Cycle Hotbar Left | `[` | Move hotbar selection left |
| Cycle Hotbar Right | `]` | Move hotbar selection right |
| Drop Item | `R` | Drop the currently selected item |
| Use Item | `V` | Use/consume the selected item |

### 11.4 Bindings Configuration Screen

The bindings configuration screen is presented in the following scenarios:

1. **First-time launch** — Before the first game starts, the player is guided through the control scheme selection (Keyboard + Mouse or Keyboard Only) and bindings screen.
2. **Main Menu** — Accessible via `Settings > Controls > Bindings`.
3. **Pause Menu** — Accessible at any time during gameplay via `Escape > Settings > Controls`.

#### Configuration Screen Features

| Feature | Description |
|---------|-------------|
| **Control Scheme Toggle** | Switch between "Keyboard + Mouse" and "Keyboard Only" at the top of the screen |
| **Category Tabs** | Actions grouped by category: Movement, Camera, Combat, Interaction, Items, Mouse |
| **Current Binding Display** | Shows the current key/button assigned to each action |
| **Rebind Button** | Click/select an action, then press the desired key or mouse button to rebind |
| **Mouse Sensitivity Slider** | Adjust mouse look sensitivity directly from the controls screen |
| **Conflict Detection** | If a key/button is already assigned to another action, highlight both in red and prompt the player to resolve |
| **Reset to Default** | Button to reset all bindings to default values |
| **Reset Category** | Button to reset a single category to defaults |
| **Preset Profiles** | Pre-configured layouts: "Default (KB+Mouse)", "Keyboard Only", "Left-Handed", "Compact", "Arrow Keys" |
| **Save Profile** | Save custom binding profiles with a name |
| **Load Profile** | Load a previously saved profile |
| **Import/Export** | Export bindings as JSON file; import from JSON file for sharing |

#### Preset Profiles

**Default (KB+Mouse) Profile:** Standard WASD + Mouse layout as described above. Matches Minecraft's default controls.

**Keyboard Only Profile:** All mouse actions remapped to keyboard. Camera look on arrow keys, primary attack on `J`, secondary/block on `K`/`L`. No mouse input processed during gameplay.

**Left-Handed Profile:**

| Action | Key |
|--------|-----|
| Move Forward | `I` |
| Move Backward | `K` |
| Move Left | `J` |
| Move Right | `L` |
| Jump | `Right Shift` |
| Sprint | `Right Ctrl` |
| Primary Attack | `A` |
| Secondary Attack | `S` |
| Block | `D` |

**Arrow Keys Profile:**

| Action | Key |
|--------|-----|
| Move Forward | `Up Arrow` |
| Move Backward | `Down Arrow` |
| Move Left | `Left Arrow` |
| Move Right | `Right Arrow` |
| Look Up / Down / Left / Right | `W/S/A/D` |

**Compact Profile:** Minimizes hand spread, clusters all actions within a tight key region around the left hand.

### 11.5 Binding Persistence

```json
{
  "version": 2,
  "profile_name": "My Custom Layout",
  "control_scheme": "KEYBOARD_MOUSE",
  "mouse": {
    "sensitivity": 100,
    "invert_y": false,
    "raw_input": true,
    "acceleration": false,
    "scroll_sensitivity": 5,
    "swap_buttons": false
  },
  "mouse_bindings": {
    "mouse.look": "MOUSE_MOVE",
    "mouse.primary_attack": "LMB",
    "mouse.secondary_action": "RMB",
    "mouse.interact": "MMB",
    "mouse.hotbar_scroll": "SCROLL",
    "mouse.zoom": "LEFT_ALT+SCROLL",
    "mouse.l_leap_target": "F+LMB"
  },
  "keyboard_bindings": {
    "movement.forward": "W",
    "movement.backward": "S",
    "movement.left": "A",
    "movement.right": "D",
    "movement.jump": "SPACE",
    "movement.sprint": "LEFT_CTRL",
    "movement.crouch": "LEFT_SHIFT",
    "movement.l_leap": "F",
    "camera.look_up": "UP",
    "camera.look_down": "DOWN",
    "camera.look_left": "LEFT",
    "camera.look_right": "RIGHT",
    "camera.toggle_mode": "F5",
    "combat.primary_attack": "J",
    "combat.secondary_attack": "K",
    "combat.block": "L",
    "combat.ability_1": "U",
    "combat.ability_2": "I",
    "combat.ability_3": "O",
    "combat.ultimate": "P",
    "combat.lock_on": "TAB",
    "combat.switch_target": "Q",
    "interact.use": "E",
    "interact.inventory": "B",
    "interact.map": "M",
    "interact.quest_log": "N",
    "interact.skill_tree": "T",
    "interact.companion_cmd": "G",
    "interact.summon_companion": "H",
    "system.quick_save": "F6",
    "system.quick_load": "F7",
    "system.pause": "ESCAPE",
    "hotbar.slot_1": "1",
    "hotbar.slot_2": "2",
    "hotbar.slot_3": "3",
    "hotbar.slot_4": "4",
    "hotbar.slot_5": "5",
    "hotbar.slot_6": "6",
    "hotbar.slot_7": "7",
    "hotbar.slot_8": "8",
    "hotbar.slot_9": "9",
    "hotbar.cycle_left": "LEFT_BRACKET",
    "hotbar.cycle_right": "RIGHT_BRACKET",
    "items.drop": "R",
    "items.use": "V"
  }
}
```

### 11.6 Accessibility Considerations

| Feature | Description |
|---------|-------------|
| **Hold vs Toggle** | All hold-actions (sprint, crouch, block) can be set to toggle mode |
| **Key Repeat Rate** | Adjustable key repeat sensitivity |
| **Input Buffering** | 100ms input buffer so rapid key presses are not lost |
| **Combo Timeout** | Adjustable window for multi-key combos (L-Leap + direction) |
| **On-Screen Key Display** | Optional HUD overlay showing current key bindings for each context |
| **Colorblind Indicators** | Key conflict highlighting uses both color and icon indicators |

---

## 12. Player Options & Settings

### 12.1 Options Menu Structure

The settings menu mirrors Minecraft's options layout with additional game-specific categories:

```
Settings
 +-- Video Settings
 +-- Audio Settings
 +-- Controls
 |    +-- Key Bindings
 |    +-- Input Settings
 +-- Gameplay
 +-- Accessibility
 +-- Language
 +-- Resource Packs
```

### 12.2 Video Settings

| Setting | Options | Default |
|---------|---------|---------|
| Resolution | 720p / 1080p / 1440p / 4K / Custom | Desktop Native |
| Display Mode | Windowed / Fullscreen / Borderless | Borderless |
| VSync | On / Off | On |
| Max Framerate | 30 / 60 / 120 / 144 / 240 / Unlimited | 60 |
| Render Distance | 2-32 chunks (slider) | 12 |
| Graphics Quality | Fast / Fancy / Fabulous | Fancy |
| Smooth Lighting | Off / Minimum / Maximum | Maximum |
| Clouds | Off / Fast / Fancy | Fancy |
| Particles | All / Decreased / Minimal | All |
| Entity Shadows | On / Off | On |
| Mipmap Levels | 0-4 | 4 |
| Biome Blend | 1x1 to 7x7 | 5x5 |
| FOV | 30-110 (slider) | 70 |
| Ray Tracing | Off / On (if supported) | Off |
| Anti-Aliasing | Off / FXAA / TAA | TAA |
| Ambient Occlusion | Off / SSAO / GTAO | SSAO |
| Bloom | Off / Low / High | Low |
| Dynamic Resolution | Off / On (target FPS) | Off |
| GUI Scale | Auto / 1-4 | Auto |
| Brightness | 0-100% (slider) | 50% |

### 12.3 Audio Settings

| Setting | Options | Default |
|---------|---------|---------|
| Master Volume | 0-100% | 100% |
| Music Volume | 0-100% | 70% |
| SFX Volume | 0-100% | 100% |
| Ambient Volume | 0-100% | 80% |
| Voice / Dialogue Volume | 0-100% | 100% |
| Weather Sounds | 0-100% | 60% |
| Subtitles | Off / On | Off |
| Directional Audio | Stereo / Surround | Stereo |

### 12.4 Gameplay Settings

| Setting | Options | Default |
|---------|---------|---------|
| Difficulty | Peaceful / Easy / Normal / Hard / Grandmaster | Normal |
| Auto-Save Interval | Off / 2 / 5 / 10 / 15 min | 5 min |
| Show Tutorial Tips | On / Off | On |
| HUD Opacity | 0-100% | 80% |
| Minimap | Off / Corner / Expanded | Corner |
| Compass Display | Off / On | On |
| Damage Numbers | Off / On | On |
| Health Bar Style | Hearts / Bar / Numeric | Hearts |
| Coordinate Display | Off / On | Off |
| Chess Notation Hints | Off / Basic / Detailed | Basic |
| Chess Guide Overlay | Off / On | On |

### 12.5 Accessibility Settings

| Setting | Options | Default |
|---------|---------|---------|
| Colorblind Mode | Off / Protanopia / Deuteranopia / Tritanopia | Off |
| High Contrast UI | Off / On | Off |
| Screen Reader Support | Off / On | Off |
| Camera Shake | Off / Reduced / Full | Full |
| Flash Effects | Off / Reduced / Full | Full |
| Text Size | Small / Medium / Large / Extra Large | Medium |
| Input Assist (auto-aim) | Off / Low / High | Off |
| One-Handed Mode | Off / Left / Right | Off |
| Auto-Crouch at Edges | Off / On | Off |

### 12.6 Language Support

Support for at minimum the following languages at launch:

- English (US/UK)
- Spanish (ES/LATAM)
- French
- German
- Portuguese (BR)
- Japanese
- Korean
- Simplified Chinese
- Traditional Chinese
- Russian
- Italian
- Polish

All in-game text, UI labels, dialogue, and quest descriptions must be externalized into language files (JSON format, same as Minecraft's `lang` files).

### 12.7 Chess Notation Hints & Guide System

The game includes a comprehensive chess education system designed to help players who are unfamiliar with chess understand the notation, piece movements, and strategic concepts woven throughout the game. This system is always accessible and never punishes the player for using it.

#### Hint Levels

| Level | What It Shows | Target Player |
|-------|--------------|---------------|
| **Off** | No chess hints displayed. Zone coordinates (a1-h8) still visible on the world but no explanatory overlays. | Experienced chess players who want full immersion. |
| **Basic** (Default) | Zone coordinates highlighted on the minimap and HUD. Piece movement patterns shown as brief tooltips when encountering a new enemy type. Board position indicator shows current zone in chess notation. | Players with some chess knowledge who want light guidance. |
| **Detailed** | Everything in Basic, plus: full movement diagrams for every piece type, strategic concept explanations (fork, pin, skewer, check, checkmate) shown as pop-up tutorials when relevant situations occur in gameplay, and a persistent "Chess Compass" HUD widget. | Players with little to no chess knowledge. |

#### Chess Guide Overlay (`F8` to toggle)

A dedicated overlay screen accessible at any time during gameplay. Fully keyboard and mouse navigable.

| Guide Section | Content |
|--------------|---------|
| **Board & Notation** | Interactive 8x8 board diagram showing algebraic notation. Player's current position highlighted. Explains file (a-h), rank (1-8), and square naming (e.g., "You are on e4 — file 'e', rank '4'"). |
| **Piece Movement** | Visual diagrams for each chess piece's movement: Pawn (forward + diagonal capture), Knight (L-shape), Bishop (diagonal), Rook (straight lines), Queen (all directions), King (one square any direction). Each diagram animates the movement path. |
| **Special Moves** | Explains castling (with animated diagram), en passant, and pawn promotion — both as chess rules and how they manifest in gameplay (e.g., "When your Pawn ally reaches Row 8, they promote — just like in real chess!"). |
| **Tactical Concepts** | Illustrated guides for: **Fork** (one piece attacks two), **Pin** (piece can't move without exposing a more valuable piece), **Skewer** (attack through one piece to another), **Discovered Attack**, **Check & Checkmate**. Each concept includes a gameplay example of how it appears in combat. |
| **Enemy Behavior** | For each White piece type: shows their chess movement pattern alongside their in-game AI behavior. E.g., "White Bishops only move diagonally and stay on their square color — use light/dark squares to avoid their attacks!" |
| **Campaign Progress Board** | A chessboard view showing which zones are liberated (Black), contested (gray), and enemy-held (White). Rescued pieces shown at their home squares. |

#### In-World Chess Notation Features

| Feature | Description |
|---------|-------------|
| **Zone Coordinate Markers** | Gold-block engravings at zone centers and borders showing the algebraic coordinate (e.g., "e4"). Always visible regardless of hint level setting. |
| **Board-Edge Rank/File Labels** | At the world perimeter, large decorative signs display the file letters (a-h) along the bottom/top edges and rank numbers (1-8) along the left/right edges. |
| **Movement Trail Guides** | When the "Detailed" hint level is active, rescued companion pieces leave a faint glowing trail showing their chess-legal movement pattern as they navigate the world. |
| **NPC Chess Tutor** | An NPC at the Home Base (c1, "The Grandmaster") who offers interactive chess lessons. Players can ask about any piece, any rule, or any concept. Dialogue is fully voiced and includes animated board demonstrations. |
| **Loading Screen Tips** | Loading screens display chess facts, notation tips, and strategic advice. E.g., "Did you know? The Knight is the only piece that can jump over others — just like your L-Leap!" |
| **Chess Puzzle Hint Stones** | Before each chess-themed puzzle in the game, a hint stone nearby explains the relevant chess concept needed to solve it. |

---

## 13. Level Design & World Structure

### 13.1 World Dimensions

| Parameter | Value |
|-----------|-------|
| Total world size | 2048 x 2048 blocks (8x8 squares of 256x256 each) |
| World height | 384 blocks (-64 to 320, matching Minecraft 1.18+) |
| Square (zone) size | 256 x 256 blocks |
| Border width between squares | 16 blocks (decorative transition zone) |
| Underground depth | 128 blocks of dungeon/cave space below surface |

### 13.2 Points of Interest Per Zone

Each zone contains a mix of the following:

| POI Type | Count Per Zone | Purpose |
|----------|---------------|---------|
| Primary Structure | 1 | Castle, fortress, cathedral — mission-critical location |
| Secondary Structures | 2-4 | Outposts, camps, houses — side content |
| Dungeons | 1-2 | Underground challenge areas with loot and puzzles |
| Resource Nodes | 5-10 | Mining spots, chests, crafting stations |
| NPC Encounters | 2-5 | Dialogue, lore, side quests, trading |
| Enemy Patrols | 3-8 | Roaming groups of White piece enemies |
| Environmental Puzzles | 1-3 | Chess-themed block puzzles, pressure plates, redstone contraptions |
| Hidden Secrets | 1-2 | Easter eggs, rare loot, lore fragments |

### 13.3 Zone Connectivity

- Zones are connected by **roads, bridges, and gates** at their borders.
- Some borders have **locked gates** requiring story progression or keys to open.
- Underground passages can connect non-adjacent zones for shortcut discovery.
- The Home Base (a1) has a **Warp Board** that allows fast travel to any previously visited zone using chess coordinate selection (e.g., select "e4" to warp to that zone).

---

## 14. Combat & Mechanics System

### 14.1 Combat Overview

Combat is real-time, keyboard-driven, and inspired by action-adventure games adapted to a block-based world. The system emphasizes the chess-inspired movement patterns of both the player and enemies.

### 14.2 Damage System

| Damage Type | Source | Effective Against |
|-------------|--------|------------------|
| **Physical** | Swords, lances, trample | Pawns, Knights |
| **Diagonal** | Bishop abilities | Enemies on adjacent diagonals |
| **Linear** | Rook abilities | Enemies in straight lines |
| **Holy** | Queen/King abilities | Dark-enhanced enemies |
| **Shadow** | Knight special abilities | White-piece enemies (bonus damage) |

### 14.3 Enemy AI Behavior

| Piece | Movement Pattern | Attack Pattern | Special Behavior |
|-------|-----------------|----------------|-----------------|
| Pawn | Walks forward only, turns at obstacles | Attacks diagonally (1 block range) | Promotes at Row 1; groups form shield walls |
| Knight | Jumps in L-patterns every 3 seconds | Strikes on landing (AOE) | Unpredictable; can jump over walls |
| Bishop | Slides diagonally, stays on own color | Ranged diagonal beam attack | Cannot cross to other square color; use this strategically |
| Rook | Charges in straight lines (N/S/E/W) | Ram attack deals heavy damage + knockback | Can break through weak blocks |
| Queen | Moves in all 8 directions freely | Combination of Bishop + Rook attacks | Fastest and most versatile; mini-boss level |
| King | Moves 1 block in any direction | Ground slam AOE, summons reinforcements | High HP; surrounded by elite guards |

### 14.4 Crafting System

Chess-themed crafting expands on Minecraft's grid crafting:

| Item | Recipe (simplified) | Effect |
|------|---------------------|--------|
| Pawn Sword | 2 Iron + 1 Stick (vertical) | Basic melee weapon, +4 damage |
| Knight's Lance | 3 Diamond + 2 Obsidian (L-shape) | +8 damage, extended reach |
| Bishop's Staff | 2 Gold + 1 Amethyst (diagonal) | Enables ranged diagonal attack |
| Rook's Hammer | 3 Netherite + 2 Stone (cross) | +12 damage, destroys blocks |
| Queen's Scepter | 1 Knight's Lance + 1 Bishop's Staff + 1 Nether Star | Ultimate weapon, all-directional |
| Checkmate Armor Set | Rare drops from each White piece type | Full set grants +50% damage to White King |

---

## 15. Alchemy & Elixir System

### 15.1 Overview

Players can gather ingredients found throughout the chessboard world and brew them into powerful **elixirs** — temporary potions that grant combat buffs, movement enhancements, defensive bonuses, and utility effects. The system encourages exploration and resource management, rewarding players who venture into dangerous zones for rare ingredients.

### 15.2 Ingredient Gathering

Ingredients are found through multiple sources:

| Source | Examples | Rarity |
|--------|----------|--------|
| **Block Mining** | Obsidian Dust, Quartz Shard, Deepslate Essence, Gold Flake | Common |
| **Plant Harvesting** | Shadow Fern, Ivory Bloom, Midnight Moss, Checkered Mushroom | Common - Uncommon |
| **Enemy Drops** | Pawn's Tear, Knight's Horseshoe Fragment, Bishop's Prism, Rook's Core Stone | Uncommon - Rare |
| **Boss Drops** | Queen's Radiance, King's Crown Shard | Legendary |
| **Chest Loot** | Bottled Moonlight, Ender Pearl Residue, Concentrated Board Dust | Uncommon - Rare |
| **Environmental** | Morning Dew (dawn only), Void Mist (board edges), Lava Ember (underground) | Varies by time/location |

### 15.3 Brewing Station

- **Alchemy Table**: A craftable station placed at the Home Base or carried as a portable item (3 uses before breaking).
- **Recipe**: 4 Obsidian + 2 Gold Ingots + 1 Amethyst Block + 1 Brewing Stand (Minecraft-style crafting grid).
- **Slots**: 1 base ingredient slot, up to 3 modifier ingredient slots, 1 fuel slot (Blaze Powder or Ender Pearl Residue).
- **Brew time**: 15-30 seconds per elixir depending on complexity.

### 15.4 Elixir Catalog

#### Combat Elixirs

| Elixir | Ingredients | Duration | Effect |
|--------|------------|----------|--------|
| **Elixir of Strength** | 1 Knight's Horseshoe Fragment + 2 Obsidian Dust + 1 Lava Ember | 120 seconds | +50% melee attack damage |
| **Elixir of the Rook** | 1 Rook's Core Stone + 2 Deepslate Essence + 1 Iron Nugget | 90 seconds | +40% armor rating, attacks cannot be interrupted |
| **Elixir of the Bishop** | 1 Bishop's Prism + 2 Quartz Shard + 1 Bottled Moonlight | 90 seconds | +60% diagonal ability damage, ranged attacks pierce through 1 additional enemy |
| **Elixir of Fury** | 2 Pawn's Tear + 1 Lava Ember + 1 Gold Flake | 60 seconds | +30% attack speed, each kill extends duration by 5 seconds |
| **Elixir of the Queen** | 1 Queen's Radiance + 1 Bishop's Prism + 1 Rook's Core Stone | 45 seconds | All attacks hit in 8 directions simultaneously (rare, powerful) |

#### Defense Elixirs

| Elixir | Ingredients | Duration | Effect |
|--------|------------|----------|--------|
| **Elixir of Shielding** | 2 Deepslate Essence + 1 Quartz Shard + 1 Iron Nugget | 120 seconds | Absorbs the next 20 HP of damage, then shatters |
| **Elixir of Regeneration** | 2 Ivory Bloom + 1 Morning Dew + 1 Shadow Fern | 180 seconds | Regenerate 1 HP every 3 seconds |
| **Elixir of Pawn's Resolve** | 3 Pawn's Tear + 1 Checkered Mushroom | 90 seconds | Cannot be knocked back, immune to stagger effects |
| **Elixir of Fortification** | 1 Rook's Core Stone + 2 Gold Flake + 1 Deepslate Essence | 60 seconds | Summons a temporary stone barrier (3 blocks wide) around the player |

#### Movement Elixirs

| Elixir | Ingredients | Duration | Effect |
|--------|------------|----------|--------|
| **Elixir of Swiftness** | 2 Shadow Fern + 1 Morning Dew + 1 Gold Flake | 120 seconds | +40% movement speed |
| **Elixir of the Knight** | 1 Knight's Horseshoe Fragment + 2 Midnight Moss + 1 Ender Pearl Residue | 90 seconds | L-Leap cooldown reduced by 50%, L-Leap range +2 blocks |
| **Elixir of Phasing** | 1 Void Mist + 1 Ender Pearl Residue + 1 Bottled Moonlight | 30 seconds | Pass through solid blocks (except bedrock). High rarity, short duration. |
| **Elixir of Featherfall** | 2 Ivory Bloom + 1 Morning Dew | 120 seconds | Negate all fall damage, slow descent |

#### Utility Elixirs

| Elixir | Ingredients | Duration | Effect |
|--------|------------|----------|--------|
| **Elixir of True Sight** | 1 Bishop's Prism + 2 Bottled Moonlight + 1 Midnight Moss | 180 seconds | Reveal hidden passages, invisible enemies, and secret chests within 32 blocks |
| **Elixir of the Board** | 1 Concentrated Board Dust + 1 Gold Flake + 1 Quartz Shard | Instant | Instantly reveals the full map of the current zone |
| **Elixir of Checkmate** | 1 King's Crown Shard + 1 Queen's Radiance + 1 Concentrated Board Dust | 30 seconds | All White enemies in a 16-block radius are frozen in place (legendary, one-time craft per campaign) |

### 15.5 Elixir Rules & Constraints

| Rule | Detail |
|------|--------|
| **Active Limit** | Maximum 2 elixirs active simultaneously. Drinking a 3rd replaces the oldest active elixir. |
| **Stacking** | Elixirs of the same type do NOT stack. Drinking the same elixir refreshes its duration. |
| **Inventory** | Player can carry up to 9 brewed elixirs (1 full hotbar row). |
| **Companion Elixirs** | Some elixirs can be given to AI-controlled companions (via the Companion Command menu) to buff them. |
| **Ingredient Storage** | A dedicated Ingredient Pouch (30 slots) in the inventory, separate from the main inventory. |
| **Recipe Discovery** | Recipes are discovered by: (a) finding recipe scrolls in dungeons, (b) experimenting with ingredient combinations, or (c) trading with NPCs. |

### 15.6 Elixir Visual & Audio Feedback

| Event | Feedback |
|-------|----------|
| **Drinking** | Character tilts head back, bottle-drinking animation, gulp sound effect |
| **Active effect** | Colored particle aura around player matching elixir type (red = strength, blue = defense, green = movement, purple = utility) |
| **Wearing off** | Particle aura flickers, warning chime at 10 seconds remaining |
| **Expired** | Glass-shatter sound, particles dissipate |

---

## 16. Animation System

### 16.1 Overview

The game features a comprehensive animation system that brings the voxel world to life. All entities — the player Knight, enemy White pieces, rescued Black companions, NPCs, and environmental elements — are animated using a **skeletal animation system adapted for block-based characters**, delivering fluid, expressive movement while preserving the Minecraft aesthetic.

### 16.2 Animation Technology

| Component | Implementation | Details |
|-----------|---------------|---------|
| **Skeletal Rigging** | Hierarchical bone system per entity model | Each voxel model has an internal skeleton with joints (e.g., shoulder, elbow, hip, knee for humanoids; neck, legs for the horse) |
| **Keyframe Interpolation** | Linear and cubic Bezier interpolation | Smooth transitions between keyframe poses at 30-60 FPS animation rate |
| **Animation Blending** | Layered blend trees | Upper body and lower body can play different animations simultaneously (e.g., running + attacking) |
| **Inverse Kinematics (IK)** | Foot placement and head tracking | Feet adapt to uneven block terrain; head tracks locked-on enemies |
| **State Machine** | Per-entity animation state machine | Handles transitions between idle, walk, run, attack, hurt, death, and special ability states |
| **Procedural Animation** | Physics-driven secondary motion | Capes, banners, horse mane, and loose armor pieces react to movement and wind |

### 16.3 Player Knight Animations

| Animation | Frames | Description | Trigger |
|-----------|--------|-------------|---------|
| **Idle (Mounted)** | 60 (loop) | Knight sits atop the horse, horse shifts weight, tail sways, Knight looks around subtly | No input for 2 seconds |
| **Walk** | 24 (loop) | Horse walks with a four-beat gait, Knight sways gently in the saddle | Movement input at normal speed |
| **Run / Gallop** | 16 (loop) | Horse breaks into a full gallop, Knight leans forward, cape billows behind | Sprint input active |
| **L-Leap Launch** | 8 | Horse crouches, Knight braces, upward burst with purple particle trail | L-Leap key pressed |
| **L-Leap Airborne** | Variable | Knight and horse soar through the air in the L-path, legs tucked | During L-Leap travel |
| **L-Leap Land** | 12 | Horse lands with impact, Knight absorbs shock, ground cracks with particles | L-Leap destination reached |
| **Jump** | 18 | Horse rears up slightly, pushes off, airborne pose, landing | Jump key pressed |
| **Primary Attack (Lance Thrust)** | 10 | Knight thrusts lance forward with one arm, horse leans into the strike | Primary attack key |
| **Secondary Attack (Heavy Swing)** | 18 | Knight winds up, delivers a sweeping horizontal slash with the lance/sword | Secondary attack key |
| **Block / Shield Raise** | 6 (hold) | Knight raises shield to the left side, horse turns slightly to present armored flank | Block key held |
| **Horse Kick** | 14 | Horse spins 180°, delivers a powerful two-legged backward kick, spins back | Horse Kick ability |
| **Trample** | 20 | Horse charges forward, Knight lowers lance, enemies are trampled underfoot | Trample ability |
| **Shadow Gallop** | 16 (loop) | Enhanced gallop with dark particle trail, horse's eyes glow bright purple | Shadow Gallop active |
| **Fork Strike** | 12 | Knight strikes in two directions simultaneously — lance left, sword right | Fork Strike ability |
| **Diagonal Slash** | 14 | Knight raises weapon overhead, slashes diagonally sending an energy arc | Diagonal Slash ability |
| **Castle Charge** | 24 | Knight glows, teleportation wind-up, vanishes, reappears at Rook's location with a burst | Castle Charge ability |
| **Queen's Wrath** | 30 | Golden aura engulfs Knight, crown of light appears, 8-directional energy burst | Ultimate ability |
| **Take Damage** | 8 | Knight flinches, horse staggers, red flash overlay | Receiving damage |
| **Death** | 36 | Knight slumps, falls off horse, horse collapses, both dissolve into dark particles | HP reaches 0 |
| **Drink Elixir** | 18 | Knight reaches to belt, lifts bottle, tilts head back, tosses empty bottle | Elixir consumed |
| **Interact** | 12 | Knight extends hand toward object/NPC | Interact key near interactable |
| **Crouch / Sneak (Mounted)** | 20 (loop) | Horse lowers its body, moves with slow, careful steps, Knight hunches | Crouch key held |

### 16.4 Enemy White Piece Animations

| Piece | Idle | Movement | Attack | Death |
|-------|------|----------|--------|-------|
| **White Pawn** | Stands at attention, shifts weight, shield arm twitches | Marches forward in a rigid, lockstep gait | Diagonal thrust with short sword, shield bash | Crumbles into white block debris |
| **White Knight** | Horse stamps and snorts, rider surveys area | L-shaped leap (crouch, jump, airborne arc, land with impact) | Overhead lance slam on landing, sweeping slash | Horse rears up, rider falls, both shatter into quartz shards |
| **White Bishop** | Floats slightly above ground, robes billow, staff glows | Glides diagonally in a smooth, ethereal movement | Raises staff, fires diagonal beam of light, recoil animation | Spins, robes unravel, dissolves into light particles |
| **White Rook** | Looms motionless like a tower, subtle stone-grinding idle vibration | Slides in a straight line with heavy stone-grinding effect, leaves cracks in ground | Charges forward with massive impact, overhead hammer smash | Crumbles top-down like a collapsing tower, block by block |
| **White Queen** | Regal stance, crown pulses with light, cape flows dynamically | Glides smoothly in any direction, leaves a faint light trail | Rapid multi-directional strikes, beam sweep, teleport-slash combo | Dramatic implosion of light, crown shatters last |
| **White King** | Sits on portable throne, stands up slowly when engaged | Takes deliberate, heavy single steps in any direction, ground trembles | Ground slam (shockwave rings), scepter beam, summon guards gesture | Multi-phase: armor cracks, falls to knees, crown rolls away, dissolves (see Section 8.4) |

### 16.5 AI Companion (Black Piece) Animations

Rescued Black pieces have the same animation categories as their White counterparts but with distinct visual flair:

| Distinction | Detail |
|-------------|--------|
| **Color palette** | Dark obsidian, blackstone, purple particle effects instead of white/gold |
| **Idle personality** | Each companion has unique idle quirks (Rook cracks knuckles, Bishop reads a tome, Pawn polishes sword) |
| **Victory pose** | Each companion has a celebratory animation played after combat ends |
| **Summoning animation** | When summoned, companions materialize from a swirl of dark chess-piece-shaped particles |
| **Dismissed animation** | When sent back to base, they salute the player and dissolve into particles |

### 16.6 Environmental & World Animations

| Element | Animation | Details |
|---------|-----------|---------|
| **Water** | Flowing surface with wave displacement | Multi-frame animated texture + vertex displacement shader |
| **Lava** | Slow churning flow with ember particles | Animated texture + rising particle emitters |
| **Foliage** | Leaves and grass sway in wind | Vertex shader wind animation, intensity varies with weather |
| **Torches / Lanterns** | Flickering light with flame animation | Animated sprite + dynamic light radius oscillation |
| **Doors / Gates** | Swing open and closed with hinge rotation | Bone-based rotation animation, triggered by interact |
| **Redstone Contraptions** | Pistons extend, pressure plates depress, levers flip | Mechanical animations synced with game logic |
| **Weather** | Rain drops, snow fall, lightning strikes | Particle systems + screen flash for lightning + camera shake for thunder |
| **Day/Night Cycle** | Sun and moon arc, sky color gradients, star appearance | Continuous skybox animation, 20-minute full cycle |
| **Chess Piece Statues** | Decorative statues in zones subtly breathe and shift | Very slow idle animation loop, gives the world an eerie living-board feeling |
| **Board Edge Void** | Swirling dark particles at the world boundary | Continuous particle vortex with depth fog |

### 16.7 Cutscene Animations

| Cutscene | Trigger | Description |
|----------|---------|-------------|
| **Opening Cinematic** | New Game start | The White army's invasion shown in dramatic sweeping camera shots. The Black pieces fall one by one. The Knight escapes. |
| **Piece Rescue** | Each rescue completion | Unique 5-10 second animation of the prison breaking open, the piece stepping out, and acknowledging the Knight |
| **Act Transitions** | Between each Act | Brief narrative cutscene showing the board state — which zones are liberated, enemy movements |
| **Final Boss Intro** | Entering the Throne Room (e8) | The White King rises from his throne, draws his scepter, the room transforms into a battle arena |
| **Victory Cinematic** | White King defeated | The board restores, Black pieces march home, the Knight is celebrated, leads into the Post-Campaign chess mode |

### 16.8 Animation Performance Budgets

| Metric | Budget |
|--------|--------|
| Max bones per entity | 32 |
| Max animated entities on screen | 50 |
| Animation update rate | 30 Hz (interpolated to display refresh rate) |
| Blend tree evaluation | < 0.5ms per entity |
| Total animation system budget | < 4ms per frame |
| Cutscene rendering | Pre-rendered at build time OR real-time at locked 30 FPS |

---

## 17. Audio Design

### 17.1 Music

| Context | Style | Instruments |
|---------|-------|-------------|
| Home Base | Calm, medieval, hopeful | Lute, soft strings, harp |
| Exploration (Black zones) | Ambient, mysterious | Low strings, woodwinds, subtle percussion |
| Exploration (Neutral zones) | Tense, watchful | Plucked strings, distant drums |
| Exploration (White zones) | Ominous, oppressive | Pipe organ, brass, military drums |
| Combat (standard) | Fast-paced, rhythmic | Driving percussion, strings, brass |
| Combat (boss) | Epic, orchestral | Full orchestra with choir |
| Victory | Triumphant fanfare | Trumpets, strings swell |
| Defeat | Somber, brief | Single low cello note fading |
| Menu | Elegant, chess-inspired | Piano, classical guitar |

### 17.2 Sound Effects

- All block interactions (place, break, walk-on) have unique sounds per material type.
- Chess pieces have distinctive movement sounds (Knight clip-clop, Rook stone grinding, Bishop swoosh).
- Spatial audio: all sounds are 3D-positioned using OpenAL for directional awareness.
- Environmental ambient loops per biome-zone.

---

## 18. UI/UX Design

### 18.1 HUD Elements

```
+---------------------------------------------------------------+
| [Heart/HP Bar]         [Quest Tracker]        [Minimap/Board] |
|                                                                |
|                                                                |
|                                                                |
|                    [Crosshair / Target]                        |
|                                                                |
|                                                                |
|                                                                |
| [Stamina Bar]                              [Companion Status] |
| [Ability Cooldowns: U I O P]                                  |
| [===== Hotbar (1-9) =====]                                    |
+---------------------------------------------------------------+
```

### 18.2 Menu Flow

```
Launch
 +-> Title Screen
      +-> New Game
      |    +-> Key Bindings Setup (first-time)
      |    +-> Difficulty Selection
      |    +-> Character Name Entry
      |    +-> Intro Cutscene
      |    +-> Gameplay Begins
      +-> Continue Game
      |    +-> Select Save Slot
      |    +-> Gameplay Resumes
      +-> Settings
      |    +-> (All settings categories)
      +-> Credits
      +-> Quit
```

### 18.3 In-Game Menus (Keyboard Navigable)

All menus are fully navigable using:
- `Arrow Keys` or `W/A/S/D` — Navigate between options
- `Enter` or `E` — Select / Confirm
- `Escape` or `Backspace` — Back / Cancel
- `Tab` — Switch between tabs/categories

---

## 19. Performance Requirements

### 19.1 Hardware Targets

| Tier | CPU | GPU | RAM | Target Performance |
|------|-----|-----|-----|--------------------|
| **Minimum** | Intel i5-4690 / AMD Ryzen 3 1200 | GTX 960 / RX 560 | 8 GB | 30 FPS @ 720p, 8 chunk render distance |
| **Recommended** | Intel i7-9700 / AMD Ryzen 5 3600 | RTX 2060 / RX 5700 | 16 GB | 60 FPS @ 1080p, 16 chunk render distance |
| **High-End** | Intel i9-12900 / AMD Ryzen 9 5900X | RTX 3080 / RX 6800 XT | 32 GB | 60 FPS @ 4K, 32 chunk render distance, ray tracing |

### 19.2 Performance Budgets

| System | Budget |
|--------|--------|
| Frame time | < 16.6ms (60 FPS target) |
| Chunk meshing | < 5ms per chunk |
| World generation | < 50ms per chunk |
| Input latency | < 1 frame (< 16.6ms from key press to response) |
| Memory usage | < 4 GB (minimum tier), < 8 GB (recommended) |
| Disk space | < 2 GB (base install), < 5 GB (with high-res textures) |
| Save file size | < 50 MB per world save |
| Load time (initial) | < 15 seconds on recommended hardware |
| Load time (zone transition) | < 3 seconds (seamless streaming preferred) |

---

## 20. Installation & Distribution

### 20.1 Local Installation

The game is a **standalone, locally-installed application**. It runs entirely offline — no internet connection is required for the single-player campaign, chess vs computer mode, or any core gameplay features.

| Specification | Detail |
|--------------|--------|
| **Distribution** | Downloadable installer via official website, Steam, and itch.io |
| **Platform** | Windows 10+, macOS 12+, Linux (Ubuntu 20.04+, Fedora 36+, Arch) |
| **Install Size** | ~2 GB (base), ~5 GB (with high-res texture pack) |
| **Runtime** | Bundled JRE (Java 21) — no separate Java installation required by the player |
| **Offline Play** | 100% offline. Campaign, chess mode, and all game features work without internet. |
| **Updates** | Optional auto-updater checks for patches on launch (if connected). Can be disabled. Manual download always available. |
| **Save Location** | Local filesystem: `~/.minecraft-chess/saves/` (configurable) |
| **Mod Support (Future)** | Resource pack system for textures/sounds at launch. Full mod API planned post-launch. |

### 20.2 Multiplayer Roadmap

The game is **architecturally multiplayer from v1.0** (client-server split, packet protocol, server-authoritative state — see Section 10.2-10.7). What changes across versions is which features are user-facing:

| Phase | Version | What Ships | What's Already Built Underneath |
|-------|---------|-----------|-------------------------------|
| **Phase 1** | **v1.0** | Single-player only. Local installation. Integrated server runs on localhost. No internet required. | Full client-server packet protocol, server-authoritative game state, tick-based simulation, chunk streaming, serializable player data, ECS with UUIDs, event bus. |
| **Phase 2** | **v1.5** | **Co-op campaign** (2-4 players). Player-hosted dedicated servers. Players share the same world and quest state. Each player is a different Black piece (Knight, Rook, Bishop, promoted Pawn). | Same server binary deployed standalone. Netty transport switches from in-memory to TCP. Player session manager expanded from 1 to N sessions. Shared quest state and companion pool. |
| **Phase 3** | **v2.0** | **Online multiplayer** — post-campaign open world with trading, PvP combat, ELO ranking, leaderboards, social features (as described in Section 22). Cloud-hosted dedicated servers. Up to 32 players per server. | Auth service, PostgreSQL player database, trade validation, ELO computation, matchmaking queue, anti-cheat hardening, chat moderation. |

### 20.3 What v1.0 Builds That Multiplayer Needs

To prevent a costly rewrite when multiplayer launches, the following systems are implemented in v1.0 even though they only serve one player:

| System | v1.0 Usage | Multiplayer Usage |
|--------|-----------|------------------|
| **Netty packet pipeline** | Client sends input packets to localhost server thread; receives state packets back. Packets go through in-memory channel. | Same packets travel over TCP to a remote server. Only the transport layer changes. |
| **Server tick loop (20 TPS)** | Runs game logic on the integrated server thread at a fixed rate, independent of client frame rate. | Same tick loop runs on a dedicated server process. |
| **Player session abstraction** | One `PlayerSession` object for the local player. | One `PlayerSession` per connected player. The server code does not distinguish between local and remote sessions. |
| **Server-authoritative combat** | All damage, ability, and hit validation happens server-side. Client sends `AttackPacket`; server responds with `DamagePacket`. | Same logic, but now the server validates inputs from potentially untrusted remote clients. |
| **Entity UUIDs** | Every entity has a UUID. Client references entities by UUID in packets. | UUIDs are globally unique, so entities can be referenced by any connected client. |
| **Chunk streaming** | Server generates chunks and sends them to the client via `ChunkDataPacket`. In single-player, this is instant (in-memory). | Same packets are compressed and sent over the network. Client caches and meshes received chunks. |
| **Event bus** | Systems communicate via events (`BlockBreakEvent`, `EntityDamageEvent`). | Events can be serialized for network broadcast, replay, and server-side logging/anti-cheat. |
| **Serializable player data** | Player inventory, quest state, companions all saved as JSON/NBT to local disk. | Same serialization is used to store player data in PostgreSQL and to transmit state over the network. |
| **Embedded database (SQLite/H2)** | Stores local ELO history (chess vs computer), game stats. | Migrates to PostgreSQL on dedicated servers. Schema is identical; only the driver changes. |

---

## 21. Post-Campaign: Chess vs Computer

### 21.1 Overview

Upon defeating the Evil White King and completing the main campaign, the player unlocks a **full chess game mode** where they can play standard chess against the computer. This serves as the ultimate reward — the board that was once a battlefield becomes an actual chessboard, and the pieces the player fought alongside come to life as their chess army.

### 21.2 Unlock Condition

- **Trigger:** Complete Act V — defeat the White King in the final boss fight and rescue the Black King.
- **Presentation:** After the victory cinematic, the board resets. The Black King addresses the Knight: *"The war is over, but the game... the game never ends. Shall we play?"* The camera pulls back to reveal the full 8x8 board from above, pieces taking their starting positions.
- **Access afterward:** Available from the Main Menu under "Chess" and from the Home Base via the **Grand Board** table in the Throne Room (e1).

### 21.3 Chess Game Mode: Core Specifications

| Feature | Specification |
|---------|--------------|
| **Rule Set** | Standard FIDE chess rules (castling, en passant, pawn promotion, stalemate, fifty-move rule, threefold repetition) |
| **Difficulty** | Fixed at **Medium** — designed to challenge casual-to-intermediate players (approximately 1200-1400 ELO equivalent) |
| **AI Engine** | Minimax algorithm with alpha-beta pruning, iterative deepening, and a tuned evaluation function |
| **Search Depth** | 6-8 ply (adjusted dynamically to maintain < 3 second move time) |
| **Opening Book** | Built-in database of 500+ common openings (up to 10 moves deep) |
| **Endgame Tables** | Syzygy-style endgame tablebases for positions with 5 or fewer pieces |
| **Player Color** | Player always plays as **Black** (consistent with the campaign narrative). No option to play as White. |
| **Time Control** | Untimed (casual, relaxed play). No clock pressure. |
| **Undo** | Player may undo up to 3 moves per game |
| **Hints** | **Unlimited** — the player can request a hint on any move, any number of times. Each hint highlights 1-2 strong candidate moves with a brief explanation (e.g., "Controls the center", "Threatens fork on c7"). Toggled via the `T` key. No penalty or cooldown for using hints. |

### 21.4 Medium Difficulty AI Behavior

The AI is calibrated to provide a satisfying, winnable challenge without being trivially easy or frustratingly hard:

| Behavior Aspect | Medium Difficulty Setting |
|----------------|--------------------------|
| **Tactical awareness** | Recognizes forks, pins, skewers, and discovered attacks up to 4 moves ahead |
| **Positional play** | Values center control, piece development, and king safety. Avoids obvious positional blunders. |
| **Intentional imperfections** | The AI occasionally (15-20% of moves) selects the 2nd or 3rd best move instead of the optimal move, simulating a human-like play style |
| **Blunder rate** | ~5% chance per move of making a minor inaccuracy (not a game-losing blunder) |
| **Opening variety** | Rotates through multiple opening systems to prevent predictability |
| **Endgame skill** | Competent at basic endgames (K+Q vs K, K+R vs K) but may struggle with complex pawn endgames |
| **Aggression** | Moderately aggressive — will launch attacks when it has an advantage but will not sacrifice material recklessly |

### 21.5 Visual Presentation

The chess game is played **within the Minecraft voxel world**, not as a 2D overlay:

| Visual Element | Description |
|---------------|-------------|
| **Board** | The camera transitions to a top-down isometric view of the full 8x8 chessboard world. Each zone (256x256 blocks) represents one square. |
| **Pieces** | The voxel chess piece models from the campaign are used. They stand at the center of their square at full scale. |
| **Move animation** | When a piece moves, it physically walks/slides/leaps across the board to its destination square using its campaign movement animation (Knight L-leaps, Bishop glides diagonally, Rook charges straight, etc.) |
| **Capture animation** | When a piece captures, it performs its attack animation. The captured piece plays its death animation and crumbles. |
| **Check indicator** | The King's square pulses red. A dramatic sound cue plays. |
| **Checkmate** | Full cinematic: the winning side's pieces celebrate, the losing King's crown falls. |
| **Square highlighting** | When a piece is selected, valid destination squares glow with a colored overlay (green = move, red = capture, blue = special move like castling/en passant). |
| **Move history** | A side panel shows algebraic notation of all moves played (e.g., "1. e4 e5 2. Nf3 Nc6..."). |
| **Captured pieces** | Displayed as miniatures along the side of the board, grouped by type. |

### 21.6 Controls (Chess Mode)

All chess controls are keyboard-driven and use the player's configured key bindings:

| Action | Default Key | Description |
|--------|------------|-------------|
| Move cursor | `W/A/S/D` or Arrow Keys | Move the selection cursor across the board squares |
| Select piece / Confirm move | `E` or `Enter` | Select a piece to move, then select destination to confirm |
| Cancel selection | `Escape` or `Backspace` | Deselect the currently selected piece |
| Undo move | `Z` | Undo the last move (up to 3 per game) |
| Request hint | `T` | Request an AI-suggested move (unlimited, no cooldown). Press again to cycle between candidate moves. |
| Resign | `R` (hold 3 seconds) | Forfeit the game (with confirmation prompt) |
| New game | `N` (from end screen) | Start a new chess game |
| Return to campaign | `Escape` (from menu) | Exit chess mode and return to the game world |

### 21.7 Post-Game Features

| Feature | Description |
|---------|-------------|
| **Game review** | After each game, the player can step through all moves with the AI's evaluation shown per move (good move, inaccuracy, mistake, blunder) |
| **Win/Loss record** | Persistent record of games played, wins, losses, and draws stored in the save file |
| **Achievements** | Special achievements for chess mode: "First Victory", "Scholar's Mate" (win in 4 moves), "Endgame Master" (win with only King + Pawn), "Stalemate Escape" |
| **Piece commentary** | Rescued pieces make brief animated comments during the game (e.g., the Queen says "Excellent move!" when you execute a strong combination) |

---

## 22. Post-Campaign: Online Multiplayer Mode (FUTURE — v2.0)

> **Note:** This entire section describes a **post-launch feature** planned for v2.0 (Months 22-28). It is NOT included in the v1.0 local release. However, the underlying architecture (client-server split, packet protocol, server-authoritative state, serializable player data — see Section 10.2-10.7 and Section 20.3) is **built from v1.0**, so enabling these features requires adding server-side services and UI, not rewriting the engine. The game must be installed locally and the campaign completed before online features become available. All systems described here require an internet connection and are additive — the core game remains fully playable offline.

### 22.1 Overview

Upon defeating the Evil White King and completing the main campaign, the game world **opens up to online multiplayer**. Players who have completed the campaign can connect to shared servers, explore the chessboard world together, **trade items and elixirs**, **fight each other in PvP combat**, and compete on a global **ELO leaderboard** based on enemies and other players defeated.

### 22.2 Unlock Condition

- **Trigger:** Same as the chess mode — complete Act V and defeat the White King.
- **Presentation:** After the victory cinematic, a new option appears in the Main Menu: **"Go Online"**. The Black King tells the player: *"The board is free now. But there are other Knights out there, other champions. Seek them out."*
- **Access:** Main Menu > "Go Online", or from Home Base via the **Portal Arch** structure at square b1.

### 22.3 Online World Structure

| Feature | Specification |
|---------|--------------|
| **Server Model** | Dedicated servers hosted by the game service. Players connect via matchmaking or direct server browser. |
| **World Persistence** | Each server hosts a persistent chessboard world. Block changes, placed items, and structures persist. |
| **Player Capacity** | Up to **32 players** per server. |
| **World State** | The world is in its post-campaign state — all zones unlocked, White enemies respawn on a timer for farming. |
| **Player Appearance** | Each player appears as their Knight character with customizable armor colors and banners to differentiate. |
| **Communication** | Text chat with global, team, and whisper channels. Keyboard shortcut: `Enter` to open chat. |
| **Networking** | Client-server architecture using Netty. Server-authoritative for anti-cheat. Tick rate: 20 TPS (matching Minecraft). |

### 22.4 Item Trading System

Players can trade items, elixirs, ingredients, equipment, and crafted goods with each other.

#### Trade Mechanics

| Feature | Specification |
|---------|--------------|
| **Initiate Trade** | Walk up to another player and press `E` (Interact) to send a trade request. |
| **Trade Window** | Both players see a split-screen trade window. Left side: your offered items. Right side: their offered items. |
| **Confirm Trade** | Both players must press `Enter` to confirm. A 5-second countdown ensures both parties agree. Either can cancel during countdown. |
| **Trade Limit** | Up to **27 item stacks** per trade (3 rows of 9 — one inventory section). |
| **Tradeable Items** | All inventory items: weapons, armor, elixirs, ingredients, crafted items, resource blocks. |
| **Non-Tradeable Items** | Quest-critical items (rescue keys, story scrolls), the Checkmate Armor Set (soulbound), and the Queen's Scepter (soulbound). |
| **Trade Log** | All trades are logged in the player's journal with timestamp, partner name, and items exchanged. |

#### Trade UI

```
+---------------------------------------------------------------+
|                        TRADE                                   |
|---------------------------------------------------------------|
|   Your Offer              |          Their Offer               |
|  +---+---+---+---+---+   |   +---+---+---+---+---+           |
|  |   |   |   |   |   |   |   |   |   |   |   |   |           |
|  +---+---+---+---+---+   |   +---+---+---+---+---+           |
|  |   |   |   |   |   |   |   |   |   |   |   |   |           |
|  +---+---+---+---+---+   |   +---+---+---+---+---+           |
|  |   |   |   |   |   |   |   |   |   |   |   |   |           |
|  +---+---+---+---+---+   |   +---+---+---+---+---+           |
|                           |                                    |
|  [Confirm]  [Cancel]      |      [Waiting...]  [Cancel]       |
+---------------------------------------------------------------+
```

Navigation: `W/A/S/D` to move between slots, `E` to place/remove item, `Enter` to confirm, `Escape` to cancel.

### 22.5 PvP Combat System

Players can engage in direct combat with each other, using the same combat mechanics from the campaign.

#### PvP Rules

| Feature | Specification |
|---------|--------------|
| **Opt-In PvP** | PvP is **enabled by default** in the online world. Players can toggle a **Peaceful flag** at their Home Base to become immune to PvP (but cannot attack others either). |
| **PvP Zones** | All zones on rows 3-6 (the contested/neutral/forward zones) are **always PvP-enabled**, regardless of Peaceful flag. Rows 1-2 and 7-8 are safe zones where Peaceful flag is respected. |
| **Combat Mechanics** | Identical to campaign combat — all abilities, weapons, elixirs, and equipment function the same way. |
| **Death Penalty** | On PvP death, the player drops **25% of carried non-equipped items** (random selection). Equipped gear is never dropped. The dropped items can be looted by the killer or any nearby player. |
| **Respawn** | Respawn at Home Base (a1) after a 10-second delay. All equipped gear is retained. |
| **Cooldown** | After being killed by the same player, a **5-minute PvP immunity** is granted against that specific player (prevents spawn-camping). |
| **Dueling** | Players can send formal **duel requests** (`G` key near another player). Duels are 1v1, take place in an instanced arena, and have no item drop penalty. Duels always affect ELO. |
| **Team Combat** | Players can form **teams of up to 4** via the social menu. Team members cannot damage each other. |

#### PvP Combat Balancing

| Consideration | Implementation |
|--------------|----------------|
| **Gear normalization** | In formal duels, gear stats are normalized to prevent gear-gap stomping. Open-world PvP uses actual stats. |
| **Elixir limits** | In formal duels, each player can bring a maximum of 3 elixirs. No limit in open-world PvP. |
| **L-Leap in PvP** | L-Leap can be used to dodge attacks and reposition, but landing on another player deals reduced damage (50% of PvE trample) to prevent one-shot leaps. |
| **Companion restriction** | AI-controlled Black piece companions are **disabled** in PvP combat. The fight is strictly between players. |

### 22.6 ELO Rating System

An ELO-based rating system tracks player combat performance, creating a competitive ranking across the online player base.

#### ELO Specifications

| Parameter | Value |
|-----------|-------|
| **Base ELO** | **1000** — every player starts at 1000 upon entering online mode. |
| **Maximum ELO** | **3000** — the hard cap. No player can exceed 3000 ELO. |
| **Minimum ELO** | **0** — ELO cannot drop below 0. |
| **K-Factor** | Dynamic: K=40 for ELO < 1200, K=20 for 1200-2000, K=10 for ELO > 2000 (higher K means faster rating changes for newer players). |

#### ELO Gain/Loss Sources

| Event | ELO Impact | Details |
|-------|-----------|---------|
| **Defeat a White enemy (PvE)** | +1 to +5 | Scales by enemy type: Pawn +1, Knight/Bishop +2, Rook +3, Queen +4, King +5. Diminishing returns: after 50 kills of the same type per day, gains halve. |
| **Win a PvP duel** | +10 to +30 | Based on ELO difference: beating a higher-rated player grants more. Calculated using standard ELO formula. |
| **Lose a PvP duel** | -10 to -30 | Losing to a lower-rated player costs more. Calculated using standard ELO formula. |
| **Open-world PvP kill** | +5 to +15 | Reduced compared to duels since open-world has gear/level advantages. |
| **Open-world PvP death** | -5 to -15 | Reduced penalty compared to duels. |
| **Win a chess game (vs computer)** | +3 | Small bonus for chess victories, encouraging players to engage with the chess mode. |
| **Team PvP victory** | +8 to +20 | All winning team members gain ELO (slightly less than 1v1 to prevent boosting). |
| **Inactivity decay** | -5 per week (after 2 weeks offline) | Prevents inactive high-rated players from permanently holding top ranks. Minimum decay floor: 1000 (cannot decay below base). |

#### ELO Formula

For PvP encounters, the standard ELO calculation is used:

```
Expected Score:  E_a = 1 / (1 + 10^((R_b - R_a) / 400))
New Rating:      R_a' = R_a + K * (S_a - E_a)

Where:
  R_a = Player A's current rating
  R_b = Player B's current rating
  S_a = Actual score (1 for win, 0 for loss, 0.5 for draw)
  K   = K-factor (40, 20, or 10 based on rating tier)
```

#### ELO Tiers & Titles

| ELO Range | Tier Title | Visual Badge | Perks |
|-----------|-----------|-------------|-------|
| 0 - 999 | Pawn | White pawn icon | None |
| 1000 - 1299 | Knight Initiate | Bronze horse icon | Default starting tier |
| 1300 - 1599 | Bishop's Guard | Silver diagonal icon | Access to exclusive trade channel |
| 1600 - 1899 | Rook Warden | Gold tower icon | Unique gold-trimmed armor skin |
| 1900 - 2199 | Queen's Champion | Platinum crown icon | Unique purple particle trail |
| 2200 - 2599 | Grandmaster | Diamond star icon | Unique Grandmaster title displayed above name, exclusive emotes |
| 2600 - 3000 | King's Conqueror | Animated obsidian crown icon | Unique legendary weapon skin, name displayed in gold in chat, access to Conqueror-only server zone |

#### Leaderboard

| Feature | Specification |
|---------|--------------|
| **Global Leaderboard** | Top 100 players by ELO, viewable from the Main Menu > "Online" > "Leaderboard" or from the Home Base notice board. |
| **Regional Leaderboard** | Top 100 per region (NA, EU, Asia, SA, OCE). |
| **Friends Leaderboard** | Ranking among players on your friends list. |
| **Season System** | ELO seasons last **3 months**. At season end, players receive rewards based on their peak ELO that season. ELO soft-resets (compressed toward 1000 by 50%). |
| **Display** | Player's ELO, tier, and badge are displayed: above their character in-game, in the trade window, in chat, and on the pause menu profile card. |

#### Anti-Boosting & Integrity

| Measure | Description |
|---------|-------------|
| **Win-trading detection** | Repeated duels between the same two players with alternating outcomes are flagged. After 5 duels between the same pair in 24 hours, ELO gains are reduced by 90%. |
| **AFK detection** | Players who do not input commands for 30 seconds during a duel forfeit the match with no ELO change for either party. |
| **Smurf prevention** | Online mode requires campaign completion, which takes significant time, naturally preventing throwaway accounts. |
| **Report system** | Players can report suspected cheaters. Server-side validation catches impossible damage values, teleportation, and speed hacks. |

### 22.7 Online Social Features

| Feature | Specification |
|---------|--------------|
| **Friends List** | Add friends by username. See online status, current zone, and ELO. |
| **Party System** | Form parties of up to 4. Party members see each other on the minimap, can fast-travel to each other's zones, and share a private chat channel. |
| **Player Profiles** | View any player's profile: ELO, tier, win/loss record, campaign completion stats, equipped gear. |
| **Emotes** | Keyboard-triggered emotes (`F1`-`F4`): Wave, Bow, Challenge (sword raise), and Victory (piece celebration). Higher ELO tiers unlock additional emotes. |
| **Block/Mute** | Block specific players from trading, dueling, or messaging you. |

### 22.8 Online Mode Controls

| Action | Default Key | Description |
|--------|------------|-------------|
| Open chat | `Enter` | Open text chat input |
| Trade request | `E` (near player) | Send trade request to nearby player |
| Duel request | `G` (near player) | Send formal duel request |
| Open social menu | `O` | View friends list, party, leaderboard |
| Toggle PvP flag | Via Home Base menu | Enable/disable Peaceful mode |
| Open player profile | `Tab` (looking at player) | View targeted player's profile |
| Emote 1-4 | `F1` - `F4` | Perform emote animations |

---

## 23. Milestones & Release Plan

| Milestone | Target | Deliverables |
|-----------|--------|-------------|
| **M0 — Pre-Production** | Months 1-2 | PRD finalized, concept art, tech stack validated, prototype of voxel engine + knight movement, animation system prototype, **client-server architecture prototype with Netty packet pipeline on localhost** |
| **M1 — Vertical Slice** | Months 3-5 | One complete zone (Home Base), Knight with core abilities and full animation set, 1 rescue mission playable, key binding system, core rendering, basic alchemy system. **All gameplay runs through the server-authoritative tick loop and packet protocol.** |
| **M2 — Alpha** | Months 6-9 | Acts I-II playable (Rows 1-4), 6 missions, 5 enemy types, crafting + alchemy system, AI-controlled companion combat, full settings menu, save/load, all entity animations. **Entity UUIDs, event bus, chunk streaming, and serializable player data all operational.** |
| **M3 — Beta** | Months 10-13 | Full campaign (Acts I-V), all 15 missions, all enemy types, boss fights, skill tree, companion AI system, full elixir catalog, cutscene animations, post-campaign Chess vs Computer mode |
| **M4 — Polish** | Months 14-16 | Bug fixes, performance optimization, visual + animation polish, chess AI tuning (medium difficulty calibration), audio finalization, accessibility pass, localization |
| **M5 — Release (v1.0)** | Month 17 | v1.0 local-install launch: single-player campaign, chess vs computer, all offline features. Distributed via Steam, itch.io, and official website. |
| **M6 — Co-op Update (v1.5)** | Months 18-21 | Co-op campaign multiplayer (2-4 players), server infrastructure, Netty networking, anti-cheat foundation |
| **M7 — Online Mode (v2.0)** | Months 22-28 | Post-campaign online multiplayer: trading system, PvP combat, ELO rating system, leaderboards, social features, seasonal rewards, community feedback integration |

---

## 24. Success Metrics

| Metric | Target |
|--------|--------|
| Player retention (Day 7) | > 40% |
| Campaign completion rate | > 25% |
| Average play session length | > 45 minutes |
| Player rating (Steam/equivalent) | > 80% positive |
| Critical bugs at launch | 0 P0, < 5 P1 |
| Performance (60 FPS on recommended) | > 95% of play time |
| Key binding customization usage | > 30% of players customize at least 1 key |
| Online mode adoption (post-campaign) | > 60% of campaign completers try online mode |
| Daily active online players (DAU) | > 5,000 within 3 months of online launch |
| Average trades per online session | > 2 per player per session |
| PvP engagement rate | > 40% of online players participate in duels or open-world PvP |
| Average ELO distribution | Bell curve centered around 1200-1400 (healthy matchmaking) |
| Accessibility feature usage | Tracked for future improvement |

---

## 25. Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| **Scope creep** in world design | High | High | Strict zone-by-zone development; cut content before cutting quality |
| **Performance issues** with voxel rendering + PBR | Medium | High | Early profiling; scalable quality settings; LOD system from day 1 |
| **Controls feel clunky** | Low | High | **MITIGATED:** Mouse + keyboard support added (matching Minecraft defaults); keyboard-only mode available as alternative; input buffering; auto-aim assist option; multiple preset profiles |
| **Chess mechanics too complex** for casual players | Low | Medium | **MITIGATED:** Three-level Chess Notation Hints system (Off/Basic/Detailed); dedicated Chess Guide overlay (F8); NPC Chess Tutor at Home Base; loading screen tips; in-game puzzle hint stones; Peaceful difficulty skips combat |
| **Art asset production bottleneck** | Medium | Medium | Modular block-based art; procedural decoration; prioritize unique blocks over quantity |
| **Narrative pacing issues** | Low | Medium | Playtesting each Act independently; adjustable difficulty affects encounter density |
| **Save corruption** | Low | High | Rotating backup saves; integrity checksums; cloud save support (future) |
| **ELO boosting / win-trading** | Medium | Medium | Win-trade detection algorithm; diminishing returns on same-pair duels; campaign-completion gate prevents smurfs |
| **Server infrastructure costs** | Medium | High | Start with regional servers; scale based on DAU; consider player-hosted dedicated servers as fallback |
| **PvP balance (gear gap)** | High | Medium | Gear normalization in formal duels; ELO-based matchmaking for arena modes; regular balance patches |
| **Online toxicity** | Medium | Medium | Report/block system; text chat filtering; temporary bans for repeated offenses; mute functionality |
| **Item duplication exploits** | Low | High | Server-authoritative inventory; trade transaction logging with rollback capability; item integrity checksums |
| **Multiplayer-first architecture slows v1.0 development** | Medium | Medium | The client-server split adds initial complexity but prevents a costly rewrite later. Mitigate by using Minecraft's proven integrated-server pattern. Keep in-memory transport for single-player (zero overhead). Budget 2 extra weeks in M0 for architecture setup. |
| **Single-player latency from server-authoritative model** | Low | Low | In-memory packet transport has near-zero latency. Client-side prediction for player movement ensures responsiveness. Only becomes a factor with remote servers (v1.5+). |

---

## 26. Appendix

### 26.1 Glossary

| Term | Definition |
|------|-----------|
| **Voxel** | A volumetric pixel; the 3D equivalent of a pixel. Each block in the game is a voxel. |
| **Chunk** | A 16x16x(world height) column of blocks, the fundamental unit of world loading and rendering. |
| **Sub-Chunk** | A 16x16x16 section within a chunk, used for efficient storage and meshing. |
| **PBR** | Physically Based Rendering — a shading model that simulates real-world light behavior. |
| **ECS** | Entity Component System — an architectural pattern separating data (components) from behavior (systems). |
| **L-Leap** | The Knight's signature movement ability, inspired by the chess knight's L-shaped move. |
| **Greedy Meshing** | An algorithm that merges adjacent identical block faces to reduce polygon count. |
| **NBT** | Named Binary Tag — a binary data format used by Minecraft for world and entity data. |
| **CSM** | Cascaded Shadow Maps — a technique for rendering shadows at multiple distance levels. |
| **Elixir** | A temporary consumable potion brewed from gathered ingredients, granting time-limited buffs. |
| **Alchemy Table** | A crafting station specifically for brewing elixirs from gathered ingredients. |
| **Skeletal Animation** | An animation technique where a mesh is deformed by an underlying bone hierarchy. |
| **IK** | Inverse Kinematics — a technique for computing joint positions to reach a target (e.g., foot placement on terrain). |
| **Blend Tree** | A data structure that combines multiple animations based on parameters (speed, direction) for smooth transitions. |
| **Minimax** | A decision-making algorithm for two-player games that minimizes the maximum possible loss; used for the chess AI. |
| **Alpha-Beta Pruning** | An optimization of minimax that eliminates branches of the search tree that cannot influence the final decision. |
| **ELO** | A rating system for measuring relative skill level. Used for the chess AI (1200-1400) and the online PvP ranking system (1000-3000 range). |
| **Algebraic Notation** | Standard chess notation using file letters (a-h) and rank numbers (1-8) to identify squares (e.g., e4, d7). |
| **K-Factor** | The multiplier in the ELO formula that determines how much a single match affects a player's rating. Higher K = faster rating changes. |
| **PvP** | Player versus Player — direct combat between human players in the online mode. |
| **Soulbound** | An item attribute meaning the item cannot be traded, dropped, or transferred to another player. |
| **Peaceful Flag** | An opt-in status that makes a player immune to PvP attacks (and unable to attack others) in safe zones. |
| **TPS** | Ticks Per Second — the server update rate (20 TPS matches Minecraft's server tick rate). |
| **Soft Reset** | A seasonal ELO adjustment that compresses all ratings toward the baseline (1000), preserving relative ranking while reducing extremes. |
| **Integrated Server** | A server instance running within the same process as the client (localhost). Used in single-player; identical code to a dedicated server. |
| **Dedicated Server** | A standalone server process that multiple clients connect to over the network. Used in co-op (v1.5) and online (v2.0). |
| **Client-Side Prediction** | A technique where the client immediately applies player movement locally, then reconciles with the server's authoritative state to hide network latency. |
| **Server-Authoritative** | A design pattern where the server is the single source of truth for all game state. Clients display what the server tells them. Prevents cheating. |
| **Protocol Buffers** | A binary serialization format by Google, used for efficient network packet encoding. |
| **Event Bus** | A publish-subscribe messaging system where game systems emit and listen for typed events (e.g., `BlockBreakEvent`), decoupling systems from each other. |
| **Player Session** | A server-side object representing a connected player, abstracting whether the connection is local (integrated) or remote (network). |

### 26.2 References

- Minecraft Java Edition technical documentation
- FIDE Laws of Chess (for accurate piece movement rules)
- LWJGL 3 documentation (graphics/audio/input framework)
- Minecraft's chunk format and world generation specifications

### 26.3 Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-02-07 | — | Initial PRD creation |
| 1.1 | 2026-02-07 | — | Row 6 updated to White's Forward Posts; chess algebraic notation added to all squares; Animation System added (Section 16); AI-controlled Black companions in combat (Section 7.4); Post-campaign Chess vs Computer mode at medium difficulty (Section 21); Alchemy & Elixir System added (Section 15) |
| 1.2 | 2026-02-07 | — | Post-campaign Online Multiplayer Mode added (Section 22) with item trading, PvP combat, and ELO rating system (base 1000, max 3000); Chess hint system changed to unlimited hints with no cooldown (Section 21.3) |
| 1.3 | 2026-02-07 | — | Mouse controls added alongside keyboard (Section 11.2); Chess Notation Hints & Guide System expanded with 3 hint levels, Chess Guide overlay, NPC tutor, and in-world notation features (Section 12.7); Game confirmed as local-install-first — online multiplayer moved to future post-launch v2.0 (Section 20, 22); Tech stack confirmed as Java 21 + Kotlin |
| 1.4 | 2026-02-07 | — | Full multiplayer-ready architecture from v1.0: client-server split with integrated local server, Netty packet protocol, server-authoritative game state, 20 TPS tick loop, entity UUIDs, event bus, chunk streaming, serializable player data (Section 10.2-10.7). Section 20 expanded with detailed multiplayer roadmap and v1.0 architecture-to-multiplayer mapping (Section 20.2-20.3). Networking protocol table added (Section 10.7). Save system redesigned for local-to-server migration (Section 10.6). Added Protocol Buffers and embedded database to tech stack. |

---

*End of Document*
