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
- SHA-256: `8caf90e1db0374af25e1fcd85a25e437b16f24d33a8e687dcd7cb1aa927d2100`
- Approved smoke-test profile path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs`
- Approved staged path: `<modrinth-profiles>/profiles/Slabbed+Terrain Slabs/mods/please-stop-0.1.0-alpha.0+1.21.11.jar`

## Behavior Included

- Keybind: `Toggle Please Stop`, default `B`.
- Toast keybind: `Toggle Please Stop Toasts`, default `N`.
- Config: local JSON booleans `enabled`, default `false`, and `showToasts`, default `true`.
- User-facing toggle feedback is creative-only; pressing `Toggle Please Stop` outside creative mode does not toggle `enabled` and does not show `Please Stop: ON/OFF`.
- Launch reminder toast is creative-only and can be disabled with the toast keybind.
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
- Jar contains `com/juliacoded/pleasestop/client/LaunchToastGate.class`.
- Local jar hash is recorded for the current private test iteration.
- Approved Modrinth profile loads Minecraft `1.21.11` with `please_stop`.
- Approved top `New World` enters singleplayer.
- Toggle smoke proof logs `Please Stop toggled on.` and `Please Stop toggled off.` in creative mode.
- Profile is left with `enabled=false`.
- No Minecraft Java process remains after stop.

## Proof Results

- Build/test: `./gradlew clean test build` passed.
- Jar contents: `fabric.mod.json`, `PleaseStopClient.class`, `CreativeFlightBrake.class`, `CreativeFlightBrake$State.class`, and `PleaseStopConfig.class` are present.
- License proof: root `LICENSE` exists and `fabric.mod.json` declares `MIT`.
- Current local private test jar hash: `8caf90e1db0374af25e1fcd85a25e437b16f24d33a8e687dcd7cb1aa927d2100`.
- Smoke proof log: `/tmp/please-stop-phase6-rc-stopped-latest.log`.
- Smoke screenshots: `/tmp/please-stop-phase6-rc-window.png` and `/tmp/please-stop-phase6-rc-toggle.png`.
- Final profile config proof: `/tmp/please-stop-phase6-rc-final-config.json` contains `enabled=false`.
- Stop proof: Modrinth showed no instances running and no matching Minecraft Java process remained.

## Release Boundary

This is a private release-candidate packet only. Do not publish, upload, create public release drafts, or claim anti-cheat or broad multiplayer compatibility without the maintainer explicitly approving that next lane.
