# Please Stop Fabric 1.21.1 Release Candidate Checklist

## Identity

- Display name: `Please Stop`
- Mod id: `please_stop`
- Version: `0.1.0-beta.5+1.21.1`
- Minecraft target: `1.21.1`
- Loader: Fabric
- Java: `21`
- License: MIT, recorded in `LICENSE` and declared in `fabric.mod.json`
- Archive name: `please-stop-0.1.0-beta.5+1.21.1.jar`
- Local build path: `build/libs/please-stop-0.1.0-beta.5+1.21.1.jar`
- Final SHA-256: record after the clean build from the tagged commit

## Behavior Included

- Keybind: `Toggle Please Stop`, default `B`.
- Config: local JSON preferences, with `enabled=false` by default.
- Enabled behavior: residual creative flight drift is cleared after active movement/up/down flight input is released.
- Disabled behavior: vanilla creative flight drift remains.
- Active held creative-flight input remains normal.
- Creative Flight Assist OFF survives temporary exclusions and later vanilla flight entry.
- Controls follow the player's current Creative state, not the world's original/default mode.
- Ground-level Sneak does not authorize braking unrelated velocity.
- Exclusions: non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.

## Required Release Proof

- Clean 38-test build from the exact release commit.
- Release-JAR identity, purity, and repository privacy checks pass.
- Jar contains `fabric.mod.json`.
- Repo contains root `LICENSE` matching the MIT metadata declaration.
- Jar contains `com/peetsamods/pleasestop/client/PleaseStopClient.class`.
- Jar contains `com/peetsamods/pleasestop/client/CreativeFlightBrake.class`.
- Exact candidate JAR loads in the intended Minecraft `1.21.1` profile.
- `B`, `N`, `V`, and `M` work; config persistence and the beta.5 behavior fixes pass.
- Profile is left with `enabled=false`, and the tested/uploaded JAR hashes match.

## Release Boundary

Publish only the verified beta.5 JAR and its complete changelog to the explicitly authorized Modrinth and CurseForge destinations. Do not broaden multiplayer, anti-cheat, or modpack compatibility claims.
