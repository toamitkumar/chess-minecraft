# M6 — Co-op Update (v1.5)

**Timeline:** Months 18-21
**Version:** v1.5 (Co-op Multiplayer)
**Status:** Not Started
**Depends On:** M5

---

## Milestone Goal

Enable **2-4 player co-op campaign**. The integrated server from v1.0 is extracted into a standalone dedicated server binary. Players connect over the network to play the campaign together. Each player controls a different Black piece. This is the first user-facing multiplayer experience — proving the multiplayer-ready architecture built in M0-M1.

## Working Build at Completion

> **v1.5 Co-op:** Player 1 hosts a dedicated server (or uses the built-in "Host Game" option). Players 2-4 join via IP address or LAN discovery. Player 1 is the Knight; others choose from Rook, Bishop, or promoted Pawn. All 4 players explore the chessboard world together, fight enemies cooperatively, tackle rescue missions as a team, share quest progress, and experience the full campaign. Server-authoritative state prevents cheating. Disconnected players can rejoin where they left off.

---

## Deliverables & Tasks

### 1. Dedicated Server Binary

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Extract integrated server into standalone headless binary | Engine | [ ] | Server runs without a window, accepts connections on port 25565 |
| 1.2 | Implement server CLI (start, stop, status, player list, kick) | Engine | [ ] | Operators can manage the server from terminal |
| 1.3 | Implement server configuration file (max players, difficulty, port, render distance) | Engine | [ ] | `server.properties` file, same format as Minecraft |
| 1.4 | Implement "Host Game" UI button in-game | UI | [ ] | Player clicks "Host Game" from main menu; integrated server starts, others can join |
| 1.5 | Implement LAN discovery (broadcast on local network) | Network | [ ] | Other players on the same LAN see the game in a server browser |
| 1.6 | Implement direct connect (IP:port) | UI | [ ] | "Join Game" screen with IP input field |

### 2. Netty TCP Transport (localhost → network)

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Switch packet transport from in-memory channel to TCP (Netty) | Network | [ ] | Same packets now travel over TCP. In-memory path remains for single-player. |
| 2.2 | Implement Protocol Buffers serialization for all v1.0 packet types | Network | [ ] | All packets serialize/deserialize correctly over the wire |
| 2.3 | Implement packet compression (zlib) | Network | [ ] | ChunkDataPackets compressed; bandwidth reduced by 60%+ |
| 2.4 | Implement connection management (join, leave, timeout, reconnect) | Network | [ ] | Players can join/leave without crashing. 30-sec timeout for disconnected players. |
| 2.5 | Implement reconnect: player state preserved for 5 minutes after disconnect | Network | [ ] | Disconnected player's entity frozen; on reconnect, resume from same position |
| 2.6 | Implement latency compensation (up to 200ms) | Network | [ ] | Player movement feels responsive at typical home network latencies |
| 2.7 | Bandwidth budget: < 100 KB/s per player during normal gameplay | Network | [ ] | Profiled and verified with 4 players in a combat scenario |

### 3. Multi-Player Session Management

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Expand `PlayerSession` from 1 → N sessions | Engine | [ ] | Server tracks multiple player sessions simultaneously |
| 3.2 | Implement player spawn: each player spawns at Home Base (a1) | Gameplay | [ ] | New players start at the castle courtyard |
| 3.3 | Implement piece selection: Player 1 = Knight, Players 2-4 choose Rook/Bishop/Pawn | UI | [ ] | Character select screen on join. Chosen pieces locked. |
| 3.4 | Create playable Rook character (movement, abilities, model, animations) | Gameplay/Art | [ ] | Straight-line charge, ram attack, stone wall ability |
| 3.5 | Create playable Bishop character (movement, abilities, model, animations) | Gameplay/Art | [ ] | Diagonal movement, ranged beam, healing aura |
| 3.6 | Create playable promoted Pawn character (movement, abilities, model, animations) | Gameplay/Art | [ ] | Forward movement, diagonal attack, shield wall, can promote |
| 3.7 | Implement shared inventory (individual per player) | Gameplay | [ ] | Each player has their own inventory, equipment, and elixirs |
| 3.8 | Implement shared quest state | Gameplay | [ ] | Mission progress shared: one player completing an objective advances it for all |
| 3.9 | Implement shared companion pool | Gameplay | [ ] | Rescued companions available to all players; summon limit is per player |

### 4. Co-op Combat & Gameplay

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Scale enemy HP and damage for player count (2P: 1.5x, 3P: 2x, 4P: 2.5x) | Combat | [ ] | Enemies tougher with more players to maintain challenge |
| 4.2 | Implement cross-player combo attacks (e.g., Knight L-Leaps + Rook charges) | Combat | [ ] | Coordinated attacks deal bonus damage |
| 4.3 | Implement shared boss fight mechanics (all players in same arena) | Combat | [ ] | Boss fight adapts to number of players present |
| 4.4 | Implement player revive: downed players can be revived by allies (10-sec channel) | Combat | [ ] | Interact key on downed player's position for 10 seconds to revive |
| 4.5 | Implement player death in co-op: respawn at nearest rally point after 30 sec | Combat | [ ] | Death in co-op is temporary; player respawns nearby |
| 4.6 | Implement friendly fire toggle (off by default) | Gameplay | [ ] | Server setting: enable/disable damage between players |

### 5. Anti-Cheat Foundation

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Verify all combat damage is server-validated | Security | [ ] | Client cannot inflict more damage than server calculates |
| 5.2 | Verify all inventory changes are server-validated | Security | [ ] | Client cannot spawn items or modify quantities |
| 5.3 | Implement movement speed validation | Security | [ ] | Server rejects player positions that exceed max movement speed |
| 5.4 | Implement teleportation detection | Security | [ ] | Server flags players who jump > 10 blocks in 1 tick |
| 5.5 | Implement server-side event logging | Security | [ ] | All player actions logged for post-hoc review |

### 6. Co-op UI & Social

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Implement text chat (Enter to open, global channel) | UI | [ ] | Players can type messages visible to all connected players |
| 6.2 | Implement player nameplates (name + piece type above head) | UI | [ ] | Other players' names visible at up to 32 blocks distance |
| 6.3 | Implement minimap player indicators (colored dots for each player) | UI | [ ] | All players visible on minimap with distinct colors |
| 6.4 | Implement "Players" tab in pause menu (list connected players) | UI | [ ] | Shows player name, piece type, HP, current zone |

---

## Exit Criteria

- [ ] 4 players can complete the full campaign cooperatively without desync or crashes
- [ ] Dedicated server runs stably for 8+ hours with 4 players
- [ ] All 3 new playable characters (Rook, Bishop, Pawn) have full ability sets and animations
- [ ] Quest progress shared correctly across all players
- [ ] Enemy scaling makes combat challenging regardless of player count
- [ ] Bandwidth < 100 KB/s per player during normal gameplay
- [ ] Reconnect works: disconnected player can rejoin within 5 minutes
- [ ] Anti-cheat catches movement hacks, damage hacks, and item spawning

---

## Dependencies

| Dependency | From |
|-----------|------|
| Shipped v1.0 with multiplayer-ready architecture | M5 |
| Netty packet protocol, PlayerSession abstraction, server tick loop | M0-M1 |

## Risks

| Risk | Mitigation |
|------|-----------|
| Desync between players (entity positions diverge) | Server-authoritative state + client interpolation. Automated desync detection tests. |
| 3 new playable characters is significant content | Reuse enemy AI patterns (Rook charges like White Rook, Bishop moves like White Bishop). Reduces new AI code. |
| NAT traversal issues for non-LAN players | Document port forwarding instructions. Consider adding relay server in M7. |
