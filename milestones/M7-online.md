# M7 — Online Mode (v2.0)

**Timeline:** Months 22-28
**Version:** v2.0 (Online Multiplayer)
**Status:** Not Started
**Depends On:** M6

---

## Milestone Goal

Launch the **full online multiplayer experience**. Players who have completed the campaign connect to cloud-hosted dedicated servers (up to 32 players per server), trade items, fight each other in PvP combat, compete on a global ELO leaderboard (base 1000, max 3000), and engage with social features. This is the culmination of the multiplayer-ready architecture built from M0.

## Working Build at Completion

> **v2.0 Online:** After completing the campaign, the player clicks "Go Online" from the Main Menu. They authenticate with their account, connect to a regional server, and enter a persistent post-campaign world populated with other players. They can trade elixirs and equipment via a split-screen trade window, challenge others to formal duels in instanced arenas (gear-normalized), engage in open-world PvP in contested zones (Rows 3-6), climb the ELO leaderboard from Pawn (0-999) through King's Conqueror (2600-3000), view global/regional/friends leaderboards, form parties of up to 4, and communicate via text chat. Seasonal ELO resets occur every 3 months with tier rewards.

---

## Deliverables & Tasks

### 1. Cloud Infrastructure & Server Hosting

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Provision cloud servers for 3 regions (NA, EU, Asia) | DevOps | [ ] | Servers running on AWS/GCP/Azure with auto-scaling groups |
| 1.2 | Deploy dedicated server binary to cloud instances | DevOps | [ ] | Server binary from M6 runs on Linux cloud VMs |
| 1.3 | Implement server orchestrator (spin up/down servers based on demand) | DevOps | [ ] | New server spins up when existing servers are > 80% full; idles down when empty for 30 min |
| 1.4 | Implement server browser API (list available servers with region, player count) | Backend | [ ] | Client fetches server list from REST API, displays in "Go Online" screen |
| 1.5 | Implement matchmaking: auto-assign player to lowest-latency server with available slots | Backend | [ ] | "Quick Join" button connects to best server in < 5 seconds |
| 1.6 | Implement server health monitoring and alerts | DevOps | [ ] | Dashboards for TPS, player count, memory, CPU. Alerts on degradation. |
| 1.7 | Implement server restart/migration without kicking players (rolling restarts) | DevOps | [ ] | Players warned 5 min before restart; seamless reconnect to new instance |
| 1.8 | Target: server handles 32 players at 20 TPS with < 50ms tick time | DevOps | [ ] | Load tested with 32 simulated players |

### 2. Authentication & Player Accounts

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Implement account registration (username + email + password) | Backend | [ ] | Registration via in-game UI or website. BCrypt password hashing. |
| 2.2 | Implement login flow (email + password → JWT token) | Backend | [ ] | Client sends credentials, receives JWT, attaches to all server packets |
| 2.3 | Implement session management (JWT validation on server) | Backend | [ ] | Server validates JWT on every connection. Expired tokens force re-login. |
| 2.4 | Implement "Go Online" UI in main menu (login/register screen) | UI | [ ] | Username + password fields, "Register" and "Login" buttons, error messages |
| 2.5 | Implement local profile → server profile migration | Backend | [ ] | First online login uploads local campaign data (inventory, ELO, stats) to server |
| 2.6 | Implement password reset flow (email-based) | Backend | [ ] | "Forgot Password" sends reset link to registered email |
| 2.7 | Implement account security: rate limiting, brute force protection | Security | [ ] | 5 failed logins → 15-minute lockout. IP rate limiting. |

### 3. PostgreSQL Database Migration

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Deploy PostgreSQL cluster (primary + read replica per region) | DevOps | [ ] | Database accessible from all server instances in the region |
| 3.2 | Migrate schema: `players`, `inventories`, `quest_progress`, `companions`, `elo_ratings` | Backend | [ ] | All tables from PRD Section 10.6 created with indexes |
| 3.3 | Implement `trade_log` table with rollback support | Backend | [ ] | Every trade recorded: timestamp, players, items exchanged. Admin can rollback. |
| 3.4 | Implement data access layer (DAO) for all entities | Backend | [ ] | Server reads/writes player data to PostgreSQL instead of local files |
| 3.5 | Implement database connection pooling (HikariCP) | Backend | [ ] | 32 concurrent players per server don't exhaust connections |
| 3.6 | Implement automated database backups (hourly, daily, weekly) | DevOps | [ ] | Point-in-time recovery within 24 hours |
| 3.7 | Implement cross-region profile access (player can log into any region) | Backend | [ ] | Profile data replicated or fetched on-demand across regions |

### 4. Trading System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Implement trade request (`E` key near another player) | Gameplay | [ ] | Trade request appears as popup for target player |
| 4.2 | Implement trade window UI (split-screen, 27 slots per side) | UI | [ ] | Both players see offered items, navigate with keyboard + mouse |
| 4.3 | Implement trade offer/counter-offer packets | Network | [ ] | `TradeOfferPacket` sent as items are placed/removed |
| 4.4 | Implement trade confirmation (both press Enter, 5-sec countdown) | Gameplay | [ ] | Both must confirm; either can cancel during countdown |
| 4.5 | Implement server-side trade validation | Backend | [ ] | Server verifies both players own offered items, no duplication |
| 4.6 | Implement soulbound item restriction (Checkmate Armor, Queen's Scepter) | Gameplay | [ ] | Soulbound items cannot be placed in trade window |
| 4.7 | Implement trade logging (all trades recorded in `trade_log`) | Backend | [ ] | Full audit trail: who, what, when |
| 4.8 | Implement trade rollback (admin tool) | Backend | [ ] | Admin can reverse a trade by ID, restoring items to original owners |

### 5. PvP Combat System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 5.1 | Implement PvP damage between players (same combat system as PvE) | Combat | [ ] | Player attacks damage other players; server validates all hits |
| 5.2 | Implement Peaceful flag (toggle at Home Base) | Gameplay | [ ] | Peaceful players immune to PvP in safe zones (Rows 1-2, 7-8) |
| 5.3 | Implement always-PvP zones (Rows 3-6, regardless of flag) | Gameplay | [ ] | Peaceful flag ignored in contested territory |
| 5.4 | Implement PvP death penalty (drop 25% non-equipped items) | Gameplay | [ ] | Random selection of inventory items dropped on death; equipped gear safe |
| 5.5 | Implement respawn at Home Base (10-sec delay) | Gameplay | [ ] | Death screen with timer, respawn at a1 |
| 5.6 | Implement 5-min PvP immunity vs same killer (anti-spawn-camp) | Combat | [ ] | After being killed, immune to that specific player for 5 min |
| 5.7 | Implement formal duel system (`G` key near player) | Gameplay | [ ] | Duel request → acceptance → instanced arena → fight → result |
| 5.8 | Implement duel arena (instanced, gear-normalized) | Gameplay | [ ] | Separate instance, stats normalized, 3 elixir limit |
| 5.9 | Implement L-Leap PvP balancing (50% reduced trample damage) | Combat | [ ] | L-Leap landing on a player deals half PvE damage |
| 5.10 | Implement companion restriction in PvP (companions disabled) | Combat | [ ] | AI companions despawn when PvP combat begins |
| 5.11 | Implement team system (up to 4 players, no friendly fire) | Gameplay | [ ] | Team members highlighted on minimap, cannot damage each other |

### 6. ELO Rating System

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 6.1 | Implement ELO calculation engine (standard formula, K-factor tiers) | Backend | [ ] | `E_a = 1 / (1 + 10^((R_b - R_a) / 400))`, K=40/20/10 by tier |
| 6.2 | Implement base ELO = 1000, max = 3000, min = 0 | Backend | [ ] | New online players start at 1000; rating clamped to [0, 3000] |
| 6.3 | Implement PvE ELO gains (+1 to +5 by enemy type, diminishing returns) | Backend | [ ] | Pawn +1, Knight/Bishop +2, Rook +3, Queen +4, King +5. 50/day cap per type. |
| 6.4 | Implement PvP duel ELO gains/losses (+/-10 to 30) | Backend | [ ] | Calculated per standard ELO formula with K-factor |
| 6.5 | Implement open-world PvP ELO gains/losses (+/-5 to 15) | Backend | [ ] | Reduced compared to duels |
| 6.6 | Implement chess win ELO bonus (+3) | Backend | [ ] | Small gain for beating the computer in chess mode |
| 6.7 | Implement inactivity decay (-5/week after 2 weeks offline, floor 1000) | Backend | [ ] | Scheduled job runs weekly, decays inactive players |
| 6.8 | Implement 7 ELO tiers with titles and badges | UI/Art | [ ] | Pawn → Knight Initiate → Bishop's Guard → Rook Warden → Queen's Champion → Grandmaster → King's Conqueror |
| 6.9 | Create tier badge icons (7 icons) | Art | [ ] | Bronze, silver, gold, platinum, diamond, animated star, animated crown |
| 6.10 | Implement tier reward skins (gold-trimmed armor, purple trail, weapon skins) | Art | [ ] | Cosmetic rewards per PRD Section 22.6 |
| 6.11 | Implement ELO display: above player, in trade window, in chat, on profile card | UI | [ ] | Rating + tier + badge visible everywhere |

### 7. Leaderboard & Seasons

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 7.1 | Implement global leaderboard (top 100 by ELO) | Backend/UI | [ ] | Fetchable from main menu and Home Base notice board |
| 7.2 | Implement regional leaderboard (top 100 per region) | Backend/UI | [ ] | NA, EU, Asia, SA, OCE separate rankings |
| 7.3 | Implement friends leaderboard | Backend/UI | [ ] | Ranking among friends list |
| 7.4 | Implement 3-month season system | Backend | [ ] | Season end: rewards based on peak ELO, soft reset (compress toward 1000 by 50%) |
| 7.5 | Implement season reward distribution (automated) | Backend | [ ] | Rewards granted to all players based on peak tier achieved |
| 7.6 | Implement season history (past seasons viewable in profile) | UI | [ ] | Shows peak ELO, tier, and rewards earned per past season |

### 8. Anti-Boosting & Integrity

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 8.1 | Implement win-trade detection (same pair, alternating outcomes) | Security | [ ] | After 5 duels between same pair in 24h, ELO gains reduced 90% |
| 8.2 | Implement AFK detection in duels (30 sec no input → forfeit, no ELO change) | Security | [ ] | Both players must actively play |
| 8.3 | Implement item duplication detection (server-side item checksums) | Security | [ ] | Server rejects inventory states that violate item conservation |
| 8.4 | Implement report system (in-game report button on player profile) | UI/Backend | [ ] | Reports stored in DB, reviewable by admin |
| 8.5 | Implement temporary ban system (1h, 24h, 7d, permanent) | Backend | [ ] | Banned players cannot connect; shown ban reason on login attempt |
| 8.6 | Implement text chat filter (profanity, slurs, spam) | Backend | [ ] | Filtered words replaced with asterisks; repeat offenders auto-muted |

### 9. Social Features

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 9.1 | Implement friends list (add by username, online status, ELO, current zone) | UI/Backend | [ ] | Friends list persisted in database, viewable from `O` key |
| 9.2 | Implement party system (up to 4, shared minimap, private chat, fast-travel) | Gameplay | [ ] | Party members see each other on minimap, can warp to party leader's zone |
| 9.3 | Implement player profiles (viewable by pressing Tab while looking at player) | UI | [ ] | Shows ELO, tier, win/loss, campaign stats, equipped gear |
| 9.4 | Implement emotes (`F1-F4`: Wave, Bow, Challenge, Victory) | Animation | [ ] | 4 emote animations, higher tiers unlock 2 more |
| 9.5 | Implement block/mute (per player, persisted) | UI/Backend | [ ] | Blocked players cannot trade, duel, or message you |
| 9.6 | Implement text chat channels (global, team, whisper) | UI/Backend | [ ] | `/all`, `/team`, `/whisper [name]` commands |

### 10. Online Mode Controls & UI

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 10.1 | Implement "Go Online" menu flow (login → server select → connect) | UI | [ ] | Smooth flow from main menu to online world |
| 10.2 | Implement server browser (list of servers with region, player count, ping) | UI | [ ] | Sortable by region, ping, player count |
| 10.3 | Implement online HUD additions (player count, ping indicator, chat window) | UI | [ ] | Visible on HUD without cluttering existing elements |
| 10.4 | Implement online controls (Enter=chat, E=trade, G=duel, O=social, Tab=profile) | Input | [ ] | All bindings customizable, defaults per PRD Section 22.8 |
| 10.5 | Implement graceful disconnect handling (return to main menu, not crash) | Network | [ ] | Network errors show message and return to menu |

---

## Exit Criteria

- [ ] 32 players on a single server at 20 TPS for 8+ hours without crashes
- [ ] Trading completes successfully with full server-side validation and audit logging
- [ ] PvP combat works in open world and instanced duels with correct damage and death mechanics
- [ ] ELO system accurately calculates ratings for PvE, PvP, and chess events
- [ ] Leaderboards display top 100 globally and per region
- [ ] Season system completes a test season: soft reset + reward distribution
- [ ] Anti-boosting catches win-trading and AFK exploitation
- [ ] Authentication flow works end-to-end (register → login → play → logout)
- [ ] PostgreSQL stores all player data reliably with automated backups
- [ ] Social features (friends, party, chat, emotes, block/mute) all functional
- [ ] 3 regional server clusters operational (NA, EU, Asia)
- [ ] < 100 KB/s bandwidth per player during normal online gameplay

---

## Dependencies

| Dependency | From |
|-----------|------|
| Dedicated server binary, Netty TCP transport, anti-cheat foundation, multi-player sessions | M6 |
| Multiplayer-ready architecture (client-server split, packets, UUIDs, event bus) | M0-M1 |
| PostgreSQL schema design, serializable player data | M0-M2 |

## Risks

| Risk | Mitigation |
|------|-----------|
| Cloud infrastructure costs exceed budget | Start with 1 server per region; auto-scale conservatively; consider player-hosted servers as fallback |
| ELO farming via PvE kills | Diminishing returns (50/day cap per enemy type); focus ELO gains on PvP duels |
| Trade exploits / item duplication | Server-authoritative inventory; all trade operations are atomic DB transactions; rollback capability |
| Player toxicity ruins community | Chat filter, report system, temp bans, mute. Assign community moderators. |
| Database under high load with 32 players writing concurrently | Connection pooling (HikariCP), batch writes, read replicas for leaderboard queries |
