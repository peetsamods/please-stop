# Please Stop Private Release Candidate

## Identity

- Display name: `Please Stop`
- Mod id: `please_stop`
- Version: `0.1.0-alpha.0+1.21.11`
- Minecraft target: `1.21.11`
- Loader: Fabric
- Java: `21`
- License: MIT, recorded in `LICENSE` and declared in `fabric.mod.json`
- Archive name: `please-stop-0.1.0-alpha.0+1.21.11.jar`
- Local build path: `build/libs/please-stop-0.1.0-alpha.0+1.21.11.jar`
- SHA-256: `d528b5b5eaa1acf7a2e0eeaed38ed4401cf5bf426f8b03b0fbdfa71b122b9cd7`
- Approved smoke-test profile path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs`
- Approved staged path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs/mods/please-stop-0.1.0-alpha.0+1.21.11.jar`

## Behavior Included

- Keybind: `Toggle Please Stop`, default `B`.
- Config: local JSON boolean `enabled`, default `false`.
- Enabled behavior: residual creative flight drift is cleared after active movement/up/down flight input is released.
- Disabled behavior: vanilla creative flight drift remains.
- Active held creative-flight input remains normal.
- Exclusions: non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.

## Private RC Proof Checklist

- `./gradlew clean test build`
- Jar contains `fabric.mod.json`.
- Repo contains root `LICENSE` matching the MIT metadata declaration.
- Jar contains `com/juliacoded/pleasestop/client/PleaseStopClient.class`.
- Jar contains `com/juliacoded/pleasestop/client/CreativeFlightBrake.class`.
- Local jar hash matches staged jar hash.
- Approved Modrinth profile loads Minecraft `1.21.11` with `please_stop`.
- Approved top `New World` enters singleplayer.
- Toggle smoke proof logs `Please Stop toggled on.` and `Please Stop toggled off.`
- Profile is left with `enabled=false`.
- No Minecraft Java process remains after stop.

## Proof Results

- Build/test: `./gradlew clean test build` passed.
- Jar contents: `fabric.mod.json`, `PleaseStopClient.class`, `CreativeFlightBrake.class`, `CreativeFlightBrake$State.class`, and `PleaseStopConfig.class` are present.
- License proof: root `LICENSE` exists and `fabric.mod.json` declares `MIT`.
- Local jar hash matched the staged Modrinth jar hash: `d528b5b5eaa1acf7a2e0eeaed38ed4401cf5bf426f8b03b0fbdfa71b122b9cd7`.
- Smoke proof log: `/tmp/please-stop-phase6-rc-stopped-latest.log`.
- Smoke screenshots: `/tmp/please-stop-phase6-rc-window.png` and `/tmp/please-stop-phase6-rc-toggle.png`.
- Final profile config proof: `/tmp/please-stop-phase6-rc-final-config.json` contains `enabled=false`.
- Stop proof: Modrinth showed no instances running and no matching Minecraft Java process remained.

## Release Boundary

This is a private release-candidate packet only. Do not publish, upload, create public release drafts, or claim anti-cheat or broad multiplayer compatibility without the maintainer explicitly approving that next lane.
