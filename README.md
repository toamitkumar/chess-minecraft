# Minecraft Chess: The Knight's Crusade

An adventure game fusing the blocky, voxel-based aesthetic of Minecraft with the strategic world of chess. Play as a Black Knight traversing a vast chess-themed voxel world to rescue captured allied pieces from the Evil White King.

Built from the ground up with a **client-server architecture** — even single-player runs an integrated local server — so multiplayer can be enabled seamlessly in future milestones.

[![Build & Test](https://github.com/toamitkumar/chess-minecraft/actions/workflows/build.yml/badge.svg)](https://github.com/toamitkumar/chess-minecraft/actions/workflows/build.yml)

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 (Temurin) | Runtime |
| Kotlin | 2.0.21 | Primary language |
| Gradle | 8.12 | Build system (Kotlin DSL) |
| LWJGL 3 | 3.3.6 | OpenGL, GLFW, OpenAL, STB |
| JOML | 1.10.8 | 3D math (matrices, vectors, quaternions) |
| Netty | 4.1.124.Final | Client-server networking |
| Protocol Buffers | 3.25.5 | Packet serialization |
| SQLite | 3.46.1.0 | Player profile persistence |
| FastNoiseLite | vendored | Procedural terrain generation |
| JUnit 5 | 5.11.3 | Testing |

**Rendering:** OpenGL 4.1 / GLSL 410 core (macOS compatible)

---

## Project Structure

```
minecraft-chess-game/
├── common/          Shared code: blocks, chunks, ECS, networking, math, noise
├── server/          Game server: tick loop, world gen, entity systems, persistence
├── client/          Rendering, input, animation, audio, camera
├── app/             Entry point: wires integrated server + client
├── proto/           Protobuf packet definitions
└── milestones/      Milestone planning documents
```

### Module Dependency Graph

```
app ──> client ──> common
 └────> server ──> common
```

---

## Prerequisites

- **Java 21** — install via [SDKMAN](https://sdkman.io/):
  ```bash
  sdk install java 21.0.5-tem
  ```
- **macOS note:** OpenGL 4.1 is the maximum supported version. The `-XstartOnFirstThread` JVM flag is required for GLFW and is preconfigured in `app/build.gradle.kts`.

---

## Build & Run

### Build the project
```bash
./gradlew build
```

### Run all tests
```bash
./gradlew test
```

### Launch the game
```bash
./gradlew :app:run
```

---

## Controls

| Key | Action |
|-----|--------|
| W / A / S / D | Move forward / left / backward / right |
| Mouse | Look around |
| Space | Jump |
| Left Shift | Sprint |
| F | L-Leap (chess knight move: 2 forward + 1 sideways) |
| Left Click | Break block |
| Right Click | Place block |
| F5 | Toggle first-person / third-person camera |

---

## Architecture Overview

### Client-Server Model

Even in single-player, the game runs a full client-server architecture using Netty's `LocalChannel` for zero-overhead in-memory communication:

1. **Client** captures input, sends `MovePacket` to server
2. **Server** runs authoritative game logic at 20 TPS (ticks per second)
3. **Server** broadcasts entity state and chunk data back to client
4. **Client** renders the world and interpolates entity positions

### Packet Protocol

7 protobuf packet types with VarInt length-prefixed framing:
- `LoginRequest` / `LoginResponse` — player authentication
- `MovePacket` — player input (position, look direction, actions)
- `ChunkDataPacket` — serialized chunk column data
- `BlockChangePacket` — block break/place events
- `EntitySpawnPacket` / `EntityMovePacket` / `EntityRemovePacket` — entity state sync

### Entity Component System (ECS)

Lightweight ECS framework in the `common` module:
- **Entity** — UUID + component bag
- **Components** — `Position`, `Velocity`, `Collision`, `Input`, `Player`, `Leap`
- **Systems** — `MovementSystem` (physics + input), `CollisionSystem` (AABB sweep), `LeapSystem` (L-shaped knight leap)
- **EcsWorld** — container that ticks all systems in registration order

### Voxel Engine

- **14 block types** with solid/transparent properties and RGB colors
- **SubChunk** (16x16x16) + **ChunkColumn** (16 sub-chunks, Y=0-255)
- **Greedy meshing** — merges coplanar same-block faces into larger quads
- **Frustum culling** — skips chunks outside the camera's view frustum
- **Procedural terrain** — 4-octave Simplex noise (FastNoiseLite), height range Y=40-80

### Persistence

- **Region files** — Anvil-inspired format (32x32 chunks per file, zlib compressed)
- **Auto-save** — dirty chunks saved every 5 minutes
- **Player database** — SQLite for player profiles (UUID, name, spawn position)

### Animation

- **Bone hierarchy** — 9-bone Knight skeleton (root, body, head, arms, legs)
- **Keyframe animation** — lerp position, slerp rotation
- **State machine** — IDLE / WALK / RUN with 0.2s cross-fade transitions

---

## Test Coverage

178+ tests across 19 test classes:

| Module | Test Classes | Tests |
|--------|-------------|-------|
| common | BlockTypeTest, SubChunkTest, ChunkColumnTest, ChunkPosTest, EntityTest, EcsWorldTest, ComponentsTest, AABBTest, BlockRaycastTest, PacketRegistryTest, VarIntTest, PacketSerializationTest | 120+ |
| server | MovementSystemTest, CollisionSystemTest, LeapSystemTest, TerrainGeneratorTest, ServerWorldTest, RegionFileTest, PlayerDatabaseTest | 58+ |

Run the full suite:
```bash
./gradlew test
```

---

## Milestones

| Milestone | Description | Status |
|-----------|-------------|--------|
| **M0** | Pre-Production — Internal prototype with voxel engine, networking, movement | Complete |
| **M1** | Vertical Slice — First playable zone with enemies, combat, textures | Planned |
| **M2** | Alpha — Multiple zones, rescue missions, progression system | Planned |
| **M3** | Beta — Full campaign, polished systems, balancing | Planned |
| **M4** | Polish — Performance, UX, accessibility, bug fixes | Planned |
| **M5** | Release — v1.0 launch | Planned |
| **M6** | Co-op — Local/LAN cooperative multiplayer | Planned |
| **M7** | Online — Dedicated server multiplayer | Planned |

See the [milestones/](milestones/) directory for detailed planning documents.

---

## License

All rights reserved. See [PRD.md](PRD.md) for the full product requirements document.
