# M5 — Release (v1.0)

**Timeline:** Month 17
**Version:** v1.0 (Public Release)
**Status:** Not Started
**Depends On:** M4

---

## Milestone Goal

Ship **v1.0** to the public. This is a coordinated launch across Steam, itch.io, and the official website. All offline features are final. The game is installed locally, requires no internet, and includes the full single-player campaign plus Chess vs Computer mode.

## Working Build at Completion

> **v1.0 Release:** Identical to v0.9 RC from M4 with final bug fixes applied. Publicly available for purchase and download. Players can install, play the full campaign (15-25 hours), unlock chess mode, and play offline indefinitely. Multiplayer-ready architecture is present but not user-facing.

---

## Deliverables & Tasks

### 1. Final Bug Fixes & Sign-Off

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 1.1 | Fix any remaining P1 bugs from M4 RC testing | All | [ ] | Zero P0, zero P1 bugs |
| 1.2 | Full regression test pass (all 15 missions, all 5 difficulties) | QA | [ ] | No regressions from M4 bug fixes |
| 1.3 | Final save/load stress test (100 save/load cycles) | QA | [ ] | No corruption, no data loss |
| 1.4 | Final performance benchmark on all 3 hardware tiers | QA | [ ] | Meets all targets from PRD Section 19 |
| 1.5 | Go/No-Go decision meeting | All Leads | [ ] | Unanimous sign-off that build is release-ready |
| 1.6 | Version tag: `v1.0.0` in git, build number frozen | DevOps | [ ] | Exact build that ships is tagged and archived |

### 2. Distribution & Store Launch

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 2.1 | Upload Windows build to Steam | DevOps | [ ] | Build passes Steam's automated checks |
| 2.2 | Upload macOS build to Steam | DevOps | [ ] | Build passes Steam's automated checks |
| 2.3 | Upload Linux build to Steam | DevOps | [ ] | Build passes Steam's automated checks |
| 2.4 | Publish Steam store page (screenshots, trailer, description, tags) | Marketing | [ ] | Store page live and discoverable |
| 2.5 | Upload all 3 platform builds to itch.io | DevOps | [ ] | Downloads available, page published |
| 2.6 | Upload all 3 platform builds to official website | DevOps | [ ] | Direct download links functional |
| 2.7 | Verify auto-updater points to correct update server | DevOps | [ ] | Fresh install checks for updates correctly (finds nothing on launch day) |
| 2.8 | Verify Steam achievements integration (if applicable) | DevOps | [ ] | Campaign and chess achievements tracked by Steam |

### 3. Marketing & Community

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 3.1 | Publish launch trailer (60-90 seconds) | Marketing | [ ] | Trailer live on YouTube, embedded on Steam page |
| 3.2 | Publish launch blog post / press release | Marketing | [ ] | Sent to gaming press, posted on official channels |
| 3.3 | Set up community channels (Discord, Reddit, forums) | Community | [ ] | Channels created, moderation in place |
| 3.4 | Prepare Day 1 patch process (if critical bugs found in first 24 hours) | DevOps | [ ] | Hotfix pipeline tested: build → test → upload → auto-update in < 4 hours |
| 3.5 | Monitor crash reports and player feedback for first 72 hours | QA/DevOps | [ ] | Crash reporting system active, team on-call |

### 4. Documentation

| # | Task | Owner | Status | Acceptance Criteria |
|---|------|-------|--------|-------------------|
| 4.1 | Publish player-facing FAQ (installation, controls, known issues) | Docs | [ ] | Accessible from official website and Steam community hub |
| 4.2 | Prepare internal post-mortem template | Management | [ ] | Template ready for post-launch retrospective |

---

## Exit Criteria

- [ ] v1.0 is publicly available on Steam, itch.io, and official website
- [ ] All 3 platforms (Windows, macOS, Linux) verified by at least 1 real user each
- [ ] No P0 or P1 bugs reported in first 72 hours
- [ ] Auto-updater infrastructure tested and ready for first patch
- [ ] Community channels active with moderation
- [ ] Crash reporting and monitoring systems operational

---

## Dependencies

| Dependency | From |
|-----------|------|
| Release-candidate build with zero P0/P1 bugs | M4 |

## Risks

| Risk | Mitigation |
|------|-----------|
| Critical bug found on launch day | Hotfix pipeline tested in advance; team on-call for 72 hours |
| Steam review process delays | Submit build 2 weeks before launch date |
| Low initial visibility | Coordinate press outreach, content creator early access, social media campaign starting 1 month before launch |
