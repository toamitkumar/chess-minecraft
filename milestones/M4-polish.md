# M4 — Polish

**Timeline:** Months 14-16
**Version:** v0.9 (Release Candidate)
**Status:** Not Started
**Depends On:** M3

---

## Milestone Goal

No new features. This milestone is entirely about **quality**: fixing bugs, optimizing performance, polishing visuals and animations, tuning the chess AI, finalizing audio, completing the accessibility pass, and shipping localization for 12+ languages. The output is a release candidate that is indistinguishable from the final product.

## Working Build at Completion

> **v0.9 RC:** The game is fully playable at production quality. Zero P0 bugs, fewer than 5 P1 bugs. 60 FPS on recommended hardware at all times. All audio tracks finalized with spatial audio. All animations polished with smooth transitions. Chess AI beats intermediate players ~50% of the time. Accessibility features (colorblind mode, text size, camera shake reduction) all functional. Game available in 12 languages. Installer packages built for Windows, macOS, and Linux.

---

## Deliverables & Tasks

### 1. Bug Fixing

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Triage all known bugs from M3 beta testing | QA | [ ] | Every bug categorized: P0 (blocker), P1 (critical), P2 (major), P3 (minor) |
| 1.2 | Fix all P0 bugs (zero remaining) | All | [ ] | No crash bugs, no data loss bugs, no progression blockers |
| 1.3 | Fix all P1 bugs (fewer than 5 remaining) | All | [ ] | No major gameplay issues, no persistent visual glitches |
| 1.4 | Fix P2 bugs (best effort, prioritized) | All | [ ] | Focus on player-facing issues reported in playtests |
| 1.5 | Conduct full regression test on all 15 missions | QA | [ ] | Every mission completable start-to-finish on all 5 difficulty levels |
| 1.6 | Conduct save/load regression (save at every mission stage, reload, verify) | QA | [ ] | No save corruption across 50+ save/load cycles |
| 1.7 | Conduct edge-case testing: zone boundaries, death during cutscenes, alt-tab, minimize | QA | [ ] | No crashes or state corruption in edge cases |

### 2. Performance Optimization

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Profile CPU and GPU on minimum, recommended, and high-end hardware | Engine | [ ] | Bottlenecks identified per hardware tier |
| 2.2 | Optimize chunk meshing (target: < 5ms per chunk) | Engine | [ ] | Greedy meshing optimized, multithreaded mesh building |
| 2.3 | Optimize entity rendering (target: 50 entities at 60 FPS) | Rendering | [ ] | Instanced rendering for similar entities, LOD for distant entities |
| 2.4 | Implement occlusion culling (skip chunks hidden by terrain) | Rendering | [ ] | Measurable FPS improvement in zones with varied terrain |
| 2.5 | Optimize particle systems (GPU-accelerated) | Rendering | [ ] | Ability VFX and weather particles don't drop FPS below 60 |
| 2.6 | Optimize memory usage (target: < 4 GB minimum, < 8 GB recommended) | Engine | [ ] | Memory profiling shows within budget at all render distances |
| 2.7 | Implement dynamic resolution scaling (optional) | Rendering | [ ] | When FPS drops below target, resolution reduces to maintain framerate |
| 2.8 | Verify: 30 FPS @ 720p on minimum hardware, 60 FPS @ 1080p on recommended, 60 FPS @ 4K on high-end | QA | [ ] | Benchmark suite passes on all 3 tiers |
| 2.9 | Optimize load times (target: < 15 sec initial, < 3 sec zone transition) | Engine | [ ] | Timed load tests pass on recommended hardware |
| 2.10 | Optimize save file size (target: < 50 MB per world) | Engine | [ ] | Full campaign world save is within budget |

### 3. Visual Polish

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Review and touch up all 200+ block textures (PBR) | Art | [ ] | Consistent quality, no placeholder textures remaining |
| 3.2 | Polish all Knight animations (16): timing, easing, particle sync | Animation | [ ] | Animations feel responsive and satisfying; no janky transitions |
| 3.3 | Polish all enemy animations (6 types × 4 anims): death effects, impact feel | Animation | [ ] | Enemies feel impactful to fight; deaths are satisfying |
| 3.4 | Polish companion animations: idle personality quirks, summon/dismiss | Animation | [ ] | Each companion feels unique and alive |
| 3.5 | Polish cutscene animations (opening, 15 rescues, 4 transitions, boss intro, victory) | Animation | [ ] | Smooth camera work, no clipping, consistent pacing |
| 3.6 | Implement volumetric lighting (god rays) in key locations | Rendering | [ ] | Cathedrals, dungeon entrances, Ivory Citadel throne room |
| 3.7 | Implement screen-space reflections (SSR) for water and marble | Rendering | [ ] | Moats, Ivory Citadel marble floors show reflections |
| 3.8 | Polish particle effects for all 9 abilities | Rendering | [ ] | VFX match PRD Section 9.5 specifications |
| 3.9 | Implement LOD system for distant terrain | Rendering | [ ] | Smooth transitions from full detail to simplified geometry |
| 3.10 | Polish sky: biome-specific skybox tones (warm Black side, cold White side) | Rendering | [ ] | Visible tonal shift as player moves from Row 1 to Row 8 |

### 4. Chess AI Tuning

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Playtest chess AI with 20+ players of varying skill levels | QA | [ ] | Target: 1200-1400 ELO players win ~50% of games |
| 4.2 | Tune suboptimal move rate (target: 15-20%) | Gameplay | [ ] | AI makes occasional 2nd/3rd-best moves without being obviously bad |
| 4.3 | Tune blunder rate (target: ~5% minor inaccuracy) | Gameplay | [ ] | AI doesn't throw games but makes human-like mistakes |
| 4.4 | Verify opening book variety (no repetitive openings) | Gameplay | [ ] | Over 10 games, AI plays at least 5 different openings |
| 4.5 | Verify endgame play (K+Q vs K, K+R vs K) | Gameplay | [ ] | AI can execute basic checkmates in endgames |
| 4.6 | Verify unlimited hints provide useful suggestions | Gameplay | [ ] | Hints show strong moves with clear explanations, no misleading advice |
| 4.7 | Tune AI move time (target: < 3 seconds on recommended hardware) | Engine | [ ] | No move takes longer than 3 seconds, average < 1.5 seconds |

### 5. Audio Finalization

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Finalize all music tracks (9 contexts from PRD Section 17.1) | Audio | [ ] | Home Base, exploration (3 zones), combat (standard + boss), victory, defeat, menu |
| 5.2 | Implement context-sensitive music transitions | Audio | [ ] | Seamless crossfade when entering combat, changing zones, entering menus |
| 5.3 | Finalize all block sounds (10+ material types) | Audio | [ ] | Place, break, walk-on sounds per material |
| 5.4 | Finalize all chess piece movement sounds | Audio | [ ] | Knight clip-clop, Rook stone grind, Bishop swoosh, etc. |
| 5.5 | Finalize combat sounds (all abilities, all enemy types) | Audio | [ ] | Each ability and enemy attack has distinct audio |
| 5.6 | Implement ambient audio loops per biome | Audio | [ ] | Wind, forest sounds, battle distant sounds, icy winds for Row 7-8 |
| 5.7 | Finalize chess mode audio (piece move sounds, check chime, checkmate fanfare) | Audio | [ ] | Chess mode has complete audio feedback |
| 5.8 | Verify 3D spatial audio correctness | Audio | [ ] | All sounds pan correctly based on position |

### 6. Accessibility Pass

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Test colorblind mode (Protanopia, Deuteranopia, Tritanopia) | QA | [ ] | All gameplay-critical color distinctions have non-color alternatives |
| 6.2 | Test high contrast UI mode | QA | [ ] | All text readable, all buttons clearly visible |
| 6.3 | Test text size (4 levels) across all screens | QA | [ ] | Extra Large text doesn't overflow UI elements |
| 6.4 | Test camera shake Off/Reduced settings | QA | [ ] | Reduced eliminates motion sickness triggers; Off removes all shake |
| 6.5 | Test flash effects Off/Reduced settings | QA | [ ] | No rapid flashing with effects reduced/off |
| 6.6 | Test keyboard-only mode end-to-end | QA | [ ] | Complete campaign playable without mouse |
| 6.7 | Test one-handed mode (left/right) | QA | [ ] | All actions accessible with one hand |
| 6.8 | Verify input assist (auto-aim) at Low and High settings | QA | [ ] | Low: slight aim correction. High: attacks target nearest enemy. |
| 6.9 | Test hold-vs-toggle for sprint, crouch, block | QA | [ ] | Both modes work correctly for all hold-actions |

### 7. Localization

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 7.1 | Extract all strings to language files (JSON, Minecraft `lang` format) | Engine | [ ] | Zero hardcoded strings in code |
| 7.2 | Translate to 12 languages (as per PRD Section 12.6) | Localization | [ ] | English, Spanish, French, German, Portuguese, Japanese, Korean, Chinese (Simplified + Traditional), Russian, Italian, Polish |
| 7.3 | Test UI layout with all languages (handle long translations) | QA | [ ] | No text overflow, truncation, or layout breaking in any language |
| 7.4 | Test chess notation and guide overlay in all languages | QA | [ ] | Chess terms correctly translated, diagrams still clear |
| 7.5 | Implement language selection in settings | UI | [ ] | Language switch applies immediately without restart |

### 8. Installer & Distribution Prep

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 8.1 | Build Windows installer (.exe / .msi) with bundled JRE | DevOps | [ ] | Install, run, uninstall cycle works cleanly on Windows 10 and 11 |
| 8.2 | Build macOS package (.dmg) with bundled JRE | DevOps | [ ] | Install, run on macOS 12+ (Intel + Apple Silicon) |
| 8.3 | Build Linux packages (.deb, .rpm, .tar.gz) with bundled JRE | DevOps | [ ] | Install, run on Ubuntu 20.04+, Fedora 36+, Arch |
| 8.4 | Implement auto-updater (optional, check on launch) | DevOps | [ ] | Detects new version, downloads patch, applies silently |
| 8.5 | Test clean install → full campaign → uninstall on all 3 platforms | QA | [ ] | No leftover files, no registry pollution, no permission issues |
| 8.6 | Prepare Steam store page assets (screenshots, trailer, description) | Marketing | [ ] | 5+ screenshots, 1 trailer, store description in English |

---

## Exit Criteria

- [ ] Zero P0 bugs; fewer than 5 P1 bugs remaining
- [ ] 60 FPS on recommended hardware in all scenarios (combat, exploration, chess mode)
- [ ] All 200+ textures, 50+ animations, and 20+ cutscenes at production quality
- [ ] Chess AI calibrated to 1200-1400 ELO (verified by playtesting)
- [ ] All 9 music tracks, complete SFX library, and 3D spatial audio finalized
- [ ] All accessibility settings tested and functional
- [ ] Game localized in 12 languages with no layout issues
- [ ] Installers built and tested on Windows, macOS, and Linux
- [ ] Full playthrough (15-25 hours) completed by 3+ internal testers without blockers

---

## Dependencies

| Dependency | From |
|-----------|------|
| Complete campaign content, all features | M3 |

## Risks

| Risk | Mitigation |
|------|-----------|
| Too many bugs from M3 to fix in 3 months | Strict triage: P0 and P1 only. P2/P3 deferred to post-launch patches. |
| Localization quality issues | Use professional translators, not machine translation. Community review for major languages. |
| Performance regression from visual polish additions | Lock performance budget before adding volumetric lighting / SSR. Profile after every addition. |
