# M3 — Beta

**Timeline:** Months 10-13
**Version:** v0.5 (Beta)
**Status:** Not Started
**Depends On:** M2

---

## Milestone Goal

Deliver the **complete campaign** — all 5 Acts, all 64 zones, all 15 rescue missions, all 6 enemy types, all boss fights, the full skill tree, the complete companion AI system, the full elixir catalog, cutscene animations, and the post-campaign Chess vs Computer mode. This is the content-complete build.

## Working Build at Completion

> **v0.5 Beta:** The player can experience the entire game from start to finish: begin at Home Base, play through all 5 Acts across 64 zones, rescue all 15 pieces (8 Pawns, 2 Bishops, 2 Rooks, 1 Knight, Queen, King), defeat the Evil White King in a 3-phase boss fight, watch the victory cinematic, and then play chess against the computer at medium difficulty with unlimited hints. The full skill tree, all 9 Knight abilities, all 13 elixirs, and the pawn promotion mechanic are functional.

---

## Deliverables & Tasks

### 1. World — Rows 5-8 (32 Additional Zones, 64 Total)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Implement biome generation: Contested Plains (Row 5) | Engine | [ ] | Open fields, scattered ruins, dynamic weather |
| 1.2 | Implement biome generation: Scorched Frontier (Row 6 — White Forward Posts) | Engine | [ ] | Advance camps, siege weaponry, craters, hostile terrain |
| 1.3 | Implement biome generation: Marble Ramparts (Row 7) | Engine | [ ] | White-stone walls, ice spikes, pawn garrisons |
| 1.4 | Implement biome generation: Ivory Citadel (Row 8) | Engine | [ ] | Towering quartz/marble structures, End-like atmosphere |
| 1.5 | Build primary structures for Rows 5-8 (32 zones) | Level Design | [ ] | Fortresses, cathedrals, prisons, the White King's Throne Room (e8) |
| 1.6 | Create 100+ additional block textures (PBR) for Rows 5-8 biomes | Art | [ ] | Snow, ice, white quartz variants, marble, gold-trimmed blocks |
| 1.7 | Implement zone connectivity: gates, bridges, underground passages | Level Design | [ ] | Story-gated borders between Rows 4→5, 6→7, 7→8 |
| 1.8 | Implement Warp Board fast-travel system at Home Base | Gameplay | [ ] | Interactive chessboard at a1; select a coordinate to warp to any visited zone |
| 1.9 | Implement dynamic weather per zone (rain, snow, storms) | Rendering | [ ] | Weather particle systems, affects visibility and combat |

### 2. Enemy — White King (Final Boss)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Create White King voxel model (towering, crown of gold + diamond) | Art | [ ] | Largest enemy model, imposing presence |
| 2.2 | Create White King animations: idle, walk, ground slam, summon, scepter beam, phase transitions, death (multi-phase) | Art | [ ] | Full animation set per PRD Section 16.4 |
| 2.3 | Implement Phase 1: The Court (elite guard fight) | Combat/AI | [ ] | Player fights 4 elite White pieces in the Throne Room |
| 2.4 | Implement Phase 2: The King Attacks (shockwaves, Pawn walls, Rook artillery) | Combat/AI | [ ] | King enters fight, ground slam AOE, summon mechanics |
| 2.5 | Implement Phase 3: Checkmate (formation puzzle) | Combat/AI | [ ] | Player maneuvers allies into checkmate positions to trap King |
| 2.6 | Implement boss health phases with visual transitions | Rendering | [ ] | Armor cracks between phases, arena changes |

### 3. Rescue Missions — Remaining 9 (15 Total)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Mission 7: Pawn rescue (Row 5, combat) | Level Design | [ ] | White Knight ambush scenario |
| 3.2 | Mission 8: Pawn rescue (Row 5, puzzle) | Level Design | [ ] | Chess-themed sliding block puzzle |
| 3.3 | Mission 9: Pawn rescue (Row 6, stealth) | Level Design | [ ] | Navigate White forward post undetected |
| 3.4 | Mission 10: Knight rescue (Row 5, Captured Stable) | Level Design | [ ] | Free ally Knight from fortified stable; grants +2 L-Leap range |
| 3.5 | Mission 11: Rook rescue (Row 6, Fortress Prison) | Level Design | [ ] | Dungeon crawl with Rook-pattern trap corridors; grants Castle Charge |
| 3.6 | Mission 12: Bishop rescue (Row 7, Light Square Cathedral) | Level Design | [ ] | Puzzle-heavy cathedral with diagonal beam lasers |
| 3.7 | Mission 13: Rook rescue (Row 7, Watchtower siege) | Level Design | [ ] | Command assembled army in a siege battle |
| 3.8 | Mission 14: Queen rescue (Row 8, Ivory Dungeon) | Level Design | [ ] | Boss fight: defeat White Queen; grants Queen's Wrath |
| 3.9 | Mission 15: Black King rescue (Row 8, Throne Room) | Level Design | [ ] | Final boss: defeat the Evil White King |

### 4. Knight — Remaining Abilities (4 of 9) & Skill Tree

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Implement Fork Strike (unlocked by rescuing first Bishop) | Gameplay | [ ] | Attack two enemies simultaneously, animations + VFX |
| 4.2 | Implement Diagonal Slash (unlocked by rescuing both Bishops) | Gameplay | [ ] | Ranged diagonal energy arc, golden VFX |
| 4.3 | Implement Castle Charge (unlocked by rescuing Rooks) | Gameplay | [ ] | Swap positions with summoned Rook ally, massive damage at both locations |
| 4.4 | Implement Queen's Wrath ultimate (unlocked by rescuing Queen) | Gameplay | [ ] | Temporary 8-directional power, golden aura, screen tint |
| 4.5 | Implement full skill tree UI (T key) | UI | [ ] | Visual tree showing ability unlock paths tied to rescue progress |
| 4.6 | Implement passive bonuses from rescued Pawns (+1 attack per Pawn) | Gameplay | [ ] | 8 Pawns = +8 attack total, applied server-side |

### 5. Companion System — All Piece Types

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Implement Rook companion AI (straight-line charges, artillery, stone walls) | AI | [ ] | Rook holds chokepoints, creates cover |
| 5.2 | Implement Knight companion AI (L-pattern flanking, combo with player) | AI | [ ] | Coordinates L-leaps with player attacks |
| 5.3 | Implement Queen companion AI (all-directional, protects player) | AI | [ ] | Most powerful companion, focuses high-value targets |
| 5.4 | Implement Pawn promotion mechanic (Pawn at Row 8 promotes) | Gameplay | [ ] | Pawn reaching h8 can become Bishop, Rook, or Knight |
| 5.5 | Create Black piece companion models (5 types) with dark palette | Art | [ ] | Obsidian/blackstone/purple particle aesthetic |
| 5.6 | Create companion animations: idle, move, attack, summon, dismiss, victory | Art | [ ] | Unique idle quirks per piece type |

### 6. Alchemy — Complete Catalog (13 Elixirs)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Implement remaining 5 elixirs: Elixir of the Queen, Fortification, Phasing, True Sight, Board, Checkmate | Gameplay | [ ] | All 13 elixirs from PRD Section 15.4 brewable and functional |
| 6.2 | Add rare/legendary ingredients in Rows 5-8 (Queen's Radiance, King's Crown Shard) | Gameplay | [ ] | Boss-exclusive drops enable legendary elixirs |
| 6.3 | Implement recipe experimentation (combine unknown ingredients) | Gameplay | [ ] | Combining ingredients without a known recipe can discover new recipes |

### 7. Cutscene Animations

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 7.1 | Create opening cinematic (new game) | Animation | [ ] | White army invasion, Black pieces fall, Knight escapes |
| 7.2 | Create piece rescue cutscenes (15 unique, 5-10 sec each) | Animation | [ ] | Prison breaks open, piece steps out, acknowledges Knight |
| 7.3 | Create Act transition cutscenes (4 transitions) | Animation | [ ] | Board overview showing liberated vs enemy zones |
| 7.4 | Create final boss intro cutscene (White King rises from throne) | Animation | [ ] | Throne Room at e8, dramatic reveal |
| 7.5 | Create victory cinematic (board restores, celebration) | Animation | [ ] | Pieces march home, Knight celebrated, leads to chess mode |

### 8. Chess vs Computer Mode

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 8.1 | Implement chess engine: minimax + alpha-beta pruning | Gameplay | [ ] | Correct move generation for all piece types, all special moves |
| 8.2 | Implement iterative deepening (6-8 ply, < 3 sec/move) | Gameplay | [ ] | AI thinks within time budget on recommended hardware |
| 8.3 | Implement opening book (500+ openings) | Gameplay | [ ] | AI plays known openings for first 10 moves |
| 8.4 | Implement medium difficulty tuning (15-20% suboptimal moves, 5% inaccuracies) | Gameplay | [ ] | AI beatable by intermediate players, not trivially easy |
| 8.5 | Implement chess board visual presentation (isometric voxel view) | Rendering | [ ] | Camera transitions to top-down view, pieces on squares |
| 8.6 | Implement piece move animations in chess mode | Animation | [ ] | Knight L-leaps, Bishop glides diagonally, Rook charges straight |
| 8.7 | Implement square highlighting (green/red/blue) | UI | [ ] | Selected piece shows valid moves, captures, special moves |
| 8.8 | Implement unlimited hints (T key, cycles candidates) | Gameplay | [ ] | AI suggests 1-2 moves with brief explanation, no limit |
| 8.9 | Implement move history panel (algebraic notation) | UI | [ ] | Side panel shows "1. e4 e5 2. Nf3 Nc6..." |
| 8.10 | Implement check/checkmate indicators and cinematic | UI/Animation | [ ] | King square pulses red on check; full cinematic on checkmate |
| 8.11 | Implement game review (step through moves with AI evaluation) | Gameplay | [ ] | Post-game review labels moves as good/inaccuracy/mistake/blunder |
| 8.12 | Implement win/loss record (stored in local SQLite/H2) | Engine | [ ] | Persistent record, viewable from chess mode menu |
| 8.13 | Implement chess controls (cursor, select, undo, resign) | Input | [ ] | All controls from PRD Section 21.6, keyboard + mouse |

### 9. Crafting — Complete

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 9.1 | Implement Rook's Hammer recipe (3 Netherite + 2 Stone) | Gameplay | [ ] | +12 damage, destroys blocks |
| 9.2 | Implement Queen's Scepter recipe (Lance + Staff + Nether Star) | Gameplay | [ ] | Ultimate weapon, all-directional |
| 9.3 | Implement Checkmate Armor Set (rare drops from White pieces) | Gameplay | [ ] | Full set grants +50% damage to White King |
| 9.4 | Implement Obsidian armor tier (highest tier) | Gameplay | [ ] | Best armor rating, unique dark visual |

---

## Exit Criteria

- [ ] Complete campaign playable from Act I through Act V (all 15 missions)
- [ ] All 64 zones (8x8 board) explorable with correct biomes and structures
- [ ] All 6 enemy types functional with chess-accurate AI
- [ ] 3-phase final boss fight against the White King is completable
- [ ] All 9 Knight abilities unlocked through rescue progression
- [ ] Skill tree UI reflects rescue-based ability unlocks
- [ ] All 5 companion types controllable by AI with 4 behavior modes
- [ ] Pawn promotion mechanic works at Row 8
- [ ] All 13 elixirs brewable from gathered ingredients
- [ ] 5 cutscenes play at correct story triggers
- [ ] Chess vs Computer mode fully functional post-campaign
- [ ] Game is completable in 15-25 hours on Normal difficulty

---

## Dependencies

| Dependency | From |
|-----------|------|
| Rows 1-4 content, 5 enemy types, companion AI, crafting, save system | M2 |

## Risks

| Risk | Mitigation |
|------|-----------|
| Content pipeline bottleneck (32 new zones + 9 missions + cutscenes) | Prioritize critical-path content; use procedural generation for secondary structures; parallelize art and level design |
| Chess AI too strong or too weak at medium difficulty | Extensive playtesting with target audience (1200-1400 ELO players); adjustable suboptimal move rate |
| Final boss fight pacing is off | Build boss arena first, iterate on phases independently; playtest weekly from Month 12 |
| Campaign too long or too short | Target 15-25 hours; cut or add side content to hit target; adjust encounter density per Act |
