# M0 — Pre-Production

**Timeline:** Months 1-2
**Version:** v0.1 (Internal Prototype)
**Status:** Complete

---

## Milestone Goal

Validate the technical foundation and produce a **playable prototype** that demonstrates: a voxel world rendering, the Knight moving through it, and the client-server architecture operating over localhost. This is the proof that the engine works before any content is built on top of it.

## Working Build at Completion

> **v0.1 Prototype:** The player can launch the application, see a small voxel world (single zone, flat terrain with a few structures), move the Knight using keyboard + mouse, perform an L-Leap, and break/place blocks. Under the hood, all input flows through Netty packets to the integrated server, and the server sends state back. A debug overlay shows tick rate, packet count, and FPS.

---

## Deliverables & Tasks

### 1. PRD & Design Finalization

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Finalize PRD v1.4 | Design | [ ] | All sections reviewed and signed off by stakeholders |
| 1.2 | Create concept art: Knight character (3 views) | Art | [ ] | Front, side, back views in blocky style approved |
| 1.3 | Create concept art: Home Base zone (a1) | Art | [ ] | Exterior and interior views of Dark Castle approved |
| 1.4 | Create concept art: White Pawn enemy | Art | [ ] | Idle and attack poses approved |
| 1.5 | Create concept art: UI mockups (HUD, menus) | Art/UX | [ ] | HUD layout, main menu, key binding screen mockups approved |
| 1.6 | Define block palette: first 50 block types | Art | [ ] | Textures for core blocks (stone, obsidian, quartz, wood, etc.) at 32x32 |
| 1.7 | Write Game Design Document (GDD) for Act I missions | Design | [ ] | Detailed level layouts, enemy placements, puzzle designs for 2 Pawn rescue missions |

### 2. Tech Stack Validation

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Set up Gradle + Kotlin DSL project structure | Engine | [x] | Project compiles, runs empty window via LWJGL/GLFW |
| 2.2 | Validate Java 21 + Kotlin interop | Engine | [x] | Kotlin classes call Java libraries (LWJGL, Netty) without issues |
| 2.3 | Validate LWJGL 3 rendering pipeline (OpenGL 4.6) | Engine | [x] | Render a colored triangle on screen with shaders |
| 2.4 | Validate Netty localhost communication | Engine | [x] | Client sends a packet, server receives and responds, round-trip < 1ms |
| 2.5 | Validate Protocol Buffers serialization | Engine | [x] | Define 3 packet types in protobuf, serialize/deserialize round-trip test passes |
| 2.6 | Validate FastNoise Lite terrain generation | Engine | [x] | Generate a 256x256 heightmap from Simplex noise, render as grayscale image |
| 2.7 | Validate OpenAL 3D audio | Engine | [x] | Play a sound source that pans as the listener moves |
| 2.8 | Validate SQLite/H2 embedded database | Engine | [x] | Create player profile table, insert/query/update operations pass |
| 2.9 | Set up CI pipeline (build + unit tests) | DevOps | [x] | Automated build on push, test results reported |

### 3. Voxel Engine Prototype

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Implement Block Registry (ID → block type mapping) | Engine | [x] | Register 10+ block types with properties (solid, transparent, light level) |
| 3.2 | Implement Sub-Chunk storage (16x16x16 palette) | Engine | [x] | Store and retrieve block data by (x, y, z) within a sub-chunk |
| 3.3 | Implement Chunk Column (stack of sub-chunks) | Engine | [x] | Full-height chunk column with world height -64 to 320 |
| 3.4 | Implement Region file I/O (Anvil-like) | Engine | [x] | Save a chunk to disk, reload it, verify data integrity |
| 3.5 | Implement greedy meshing for sub-chunks | Rendering | [x] | Generate optimized mesh from block data, render with correct face culling |
| 3.6 | Implement basic chunk rendering (8-chunk radius) | Rendering | [x] | Render a small world (8 chunks in each direction) at 60+ FPS |
| 3.7 | Implement frustum culling | Rendering | [x] | Only chunks within the camera frustum are submitted for drawing |
| 3.8 | Implement basic terrain generation (flat + noise) | Engine | [x] | Generate terrain using Simplex noise with grass/stone/dirt layers |
| 3.9 | Implement basic block lighting (sky + block light) | Engine | [x] | Sunlight propagates from sky downward; torches emit block light |

### 4. Client-Server Architecture Prototype

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Implement integrated server thread (20 TPS tick loop) | Engine | [x] | Server runs at stable 20 TPS independent of client frame rate |
| 4.2 | Implement Netty in-memory channel (localhost) | Network | [x] | Client and server communicate via Netty pipeline, zero network overhead |
| 4.3 | Define core packet types: `LoginPacket`, `MovePacket`, `ChunkDataPacket`, `BlockChangePacket`, `EntitySpawnPacket`, `EntityMovePacket` | Network | [x] | Packet classes defined in protobuf, serialize/deserialize tests pass |
| 4.4 | Implement `PlayerSession` abstraction | Network | [x] | Server creates a `PlayerSession` on login, tracks player state |
| 4.5 | Implement client-side prediction for player movement | Network | [x] | Player moves instantly on client; server validates and reconciles |
| 4.6 | Implement chunk streaming (server → client) | Network | [x] | Client requests chunks within render distance; server sends `ChunkDataPacket` |
| 4.7 | Implement entity interpolation on client | Rendering | [x] | Entities move smoothly between server tick updates |
| 4.8 | Add debug overlay: TPS, FPS, packet count, latency | Engine | [x] | Press F3 to toggle overlay showing real-time metrics |

### 5. Knight Movement Prototype

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Implement ECS framework (Entity, Component, System) | Engine | [x] | Create entities with components, systems iterate over matching entities |
| 5.2 | Implement UUID-based entity identification | Engine | [x] | All entities have UUIDs; lookup by UUID returns correct entity |
| 5.3 | Implement Knight entity with position, velocity, collision components | Gameplay | [x] | Knight entity spawns in the world, server tracks position |
| 5.4 | Implement AABB collision detection | Physics | [x] | Knight cannot walk through solid blocks, stands on ground |
| 5.5 | Implement keyboard + mouse input → `MovePacket` pipeline | Input | [x] | WASD moves Knight, mouse rotates camera, all sent as packets |
| 5.6 | Implement basic L-Leap (2+1 block jump) | Gameplay | [x] | Press F to execute L-Leap in the direction of movement |
| 5.7 | Implement basic Knight placeholder model (textured cube) | Art | [ ] | Knight renders as a recognizable blocky character (placeholder OK) |
| 5.8 | Implement first-person and third-person camera toggle | Rendering | [x] | F5 cycles between camera modes |

### 6. Animation System Prototype

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Implement skeletal bone hierarchy data structure | Animation | [x] | Define bones, parent-child relationships, rest pose |
| 6.2 | Implement keyframe interpolation (linear) | Animation | [x] | Animate a test model between two keyframe poses smoothly |
| 6.3 | Implement animation state machine (idle ↔ walk ↔ run) | Animation | [x] | Knight transitions between idle, walk, and run based on speed |
| 6.4 | Create placeholder Knight walk animation (8 frames) | Art | [ ] | Knight visibly walks when moving |
| 6.5 | Create placeholder Knight idle animation (4 frames) | Art | [ ] | Knight subtly moves when standing still |

---

## Exit Criteria

All of the following must be true to close M0:

- [x] Prototype application launches and renders a voxel world
- [x] Knight moves via keyboard + mouse; camera rotates via mouse
- [x] L-Leap executes correctly (2+1 block pattern)
- [x] Blocks can be broken and placed
- [x] All input flows through Netty packets (visible in debug overlay)
- [x] Server tick loop runs at stable 20 TPS
- [x] Chunks stream from server to client via `ChunkDataPacket`
- [x] Animation state machine plays idle/walk/run on the Knight
- [x] Region file save/load works (world persists across restart)
- [x] CI pipeline builds and runs unit tests on every push
- [ ] PRD and Act I GDD are finalized and approved
- [ ] Concept art for Knight, Home Base, White Pawn, and UI approved

---

## Dependencies

- None (first milestone)

## Risks

| Risk | Mitigation |
|------|-----------|
| LWJGL/Netty integration issues on Java 21 | Validate in Task 2.2 during Week 1 — switch to alternatives early if broken |
| Greedy meshing performance insufficient | Profile early (Task 3.5); fall back to naive meshing if needed for prototype |
| Client-server split adds unexpected complexity | Keep single-player path simple (in-memory channel); don't optimize network path yet |
