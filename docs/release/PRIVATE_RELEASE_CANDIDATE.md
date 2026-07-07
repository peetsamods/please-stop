# Please Stop Public Beta Candidate

## Identity

- Display name: `Please Stop`
- Mod id: `please_stop`
- Current built version: `0.1.0-beta.1+1.21.11`
- Minecraft target: `1.21.11`
- Loader: Fabric
- Java: `21`
- License: MIT, recorded in `LICENSE` and declared in `fabric.mod.json`
- Current archive name: `please-stop-0.1.0-beta.1+1.21.11.jar`
- Current local build path: `build/libs/please-stop-0.1.0-beta.1+1.21.11.jar`
- SHA-256: `9b04574e1d60741c001a14875b6a74c281e1c70a43a09e237eeaae18d609eb60`
- Approved smoke-test profile path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs`
- Approved staged path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs/mods/please-stop-0.1.0-beta.1+1.21.11.jar`

## Port Artifacts

| Minecraft target | Version | SHA-256 | Staged path | Savepoint tag |
| --- | --- | --- | --- | --- |
| `1.21.11` | `0.1.0-beta.1+1.21.11` | `2f4e105bb6f5b89e9206337e24e9f0dd68116a2db836bf658bae0777cef21e96` | `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs/mods/please-stop-0.1.0-beta.1+1.21.11.jar` | `please-stop-0.1.0-beta.1+1.21.11` |
| `1.21.1` | `0.1.0-beta.1+1.21.1` | `20e691f3c1316cceab00080c6a6b7356907e30700b2df016f1b0666889237fc6` | `<modrinth-profiles>/profiles/Slabbed 1.21.1/mods/please-stop-0.1.0-beta.1+1.21.1.jar` | `please-stop-0.1.0-beta.1+1.21.1` |
| `26.2` | `0.1.0-beta.1+26.2` | `9bc5c43a16fe696943c9dac882a0abdaf75ab66f6c760ff1031c946f035f7854` | `<modrinth-profiles>/profiles/SLABBED-MC 26.2/mods/please-stop-0.1.0-beta.1+26.2.jar` | `please-stop-0.1.0-beta.1+26.2` |

## Behavior Included

- Keybind: `Toggle Please Stop`, default `B`.
- Toast keybind: `Toggle Please Stop Toasts`, default `N`.
- Config: local JSON booleans `enabled`, default `false`, and `showToasts`, default `true`.
- User-facing toggle feedback is creative-only; pressing `Toggle Please Stop` outside creative mode does not toggle `enabled` and does not show `Please Stop: ON/OFF`.
- Launch reminder toast is creative-only and can be disabled with the toast keybind.
- Enabled behavior: residual creative flight drift is cleared after active movement input is released and up/down flight input is released or held together as vertical-neutral.
- Disabled behavior: vanilla creative flight drift remains.
- Active held creative-flight input remains normal.
- Exclusions: non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.

## Beta Proof Checklist

- `./gradlew clean test build`
- Jar contains `fabric.mod.json`.
- Repo contains root `LICENSE` matching the MIT metadata declaration.
- Jar contains `com/juliacoded/pleasestop/client/PleaseStopClient.class`.
- Jar contains `com/juliacoded/pleasestop/client/CreativeFlightBrake.class`.
- Jar contains `com/juliacoded/pleasestop/client/LaunchToastGate.class`.
- Local jar hash is recorded for the current beta iteration.
- Approved Modrinth profile loads Minecraft `1.21.11` with `please_stop`.
- Approved top `New World` enters singleplayer.
- Toggle smoke proof logs `Please Stop toggled on.` and `Please Stop toggled off.` in creative mode.
- Profile is left with `enabled=false`.
- No Minecraft Java process remains after stop.

## Proof Results

- Build/test: `./gradlew clean test build` passed.
- Port build/test: `./gradlew clean test build` passed for `1.21.1`; `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew clean test build` passed for `26.2`.
- the maintainer cross-world test: current beta jars were tested across worlds and confirmed working.
- Version posture: current public beta candidate is `0.1.0-beta.1+1.21.11`.
- Jar contents: `fabric.mod.json`, `PleaseStopClient.class`, `CreativeFlightBrake.class`, `CreativeFlightBrake$State.class`, and `PleaseStopConfig.class` are present.
- License proof: root `LICENSE` exists and `fabric.mod.json` declares `MIT`.
- Current local beta jar hash: `9b04574e1d60741c001a14875b6a74c281e1c70a43a09e237eeaae18d609eb60`.
- Smoke proof log: `/tmp/please-stop-phase6-rc-stopped-latest.log`.
- Smoke screenshots: `/tmp/please-stop-phase6-rc-window.png` and `/tmp/please-stop-phase6-rc-toggle.png`.
- Final profile config proof: `/tmp/please-stop-phase6-rc-final-config.json` contains `enabled=false`.
- Previous Phase 6 stop proof: Modrinth showed no instances running and no matching Minecraft Java process remained after that smoke run.
- Current beta staging note: the `Slabbed+Terrain Slabs` profile was running during the beta jar disk staging, so that client must be restarted before live beta proof counts.

## Release Boundary

This is the public beta-candidate packet. Public release is approved, but do not claim anti-cheat or broad multiplayer compatibility without proof.
