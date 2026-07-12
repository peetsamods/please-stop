# Please Stop Runtime Architecture

## Runtime Shape

Please Stop is planned as a Fabric-first client mod. The first implementation should be as small as possible:

- Client initializer for keybind/config setup.
- Config manager for loading, saving, and validating `enabled` at `config/please_stop.json`.
- Keybind handler for flipping `enabled` and saving the new state.
- Toast keybind handler for flipping the creative-only launch reminder preference.
- Native settings screen plus keybind for changing local Please Stop preferences.
- Creative Flight Assist controller for safely activating or restoring the player's existing Creative flight permission.
- Inertia controller for checking eligibility and applying the narrow drift stop.
- No server command surface in v1.

## Ownership

| Concern | Owner | Notes |
| --- | --- | --- |
| Toggle keybind | Client | Toggles local `enabled` state. |
| Toast keybind | Client | Toggles local `showToasts` state. |
| Settings screen | Client | Changes local enablement, toast, and Flight Assist mode preferences. |
| Flight Assist keybind | Client | Toggles the current session's Creative ground-flight assistance. |
| Config persistence | Client | Stores only local preference. |
| Movement check | Client | Applies only to local creative flight behavior. |
| Server authority | Server | Not changed by this mod. |
| Release policy | the maintainer | Public beta release is approved; broader release claims still require proof. |

## Data Flow

1. Client starts.
2. Config loads `enabled` from Fabric's config dir, defaulting to `false` and creating `please_stop.json` if missing.
3. Keybind registration exposes `Toggle Please Stop`.
4. Pressing `Toggle Please Stop` flips `enabled`, saves immediately, logs the new state, and shows local actionbar feedback only when the local player is in creative mode.
5. Pressing `Toggle Please Stop Toasts` flips `showToasts`, saves immediately, and shows local actionbar feedback only when the local player is in creative mode.
6. Pressing `Open Please Stop Settings` opens the native local settings screen; its `Key Bindings...` button uses Minecraft's standard Controls screen for rebinds.
7. While Please Stop is enabled, `Toggle Creative Flight Assist` uses Minecraft's normal Creative ability update only for an eligible local Creative player. Persistent mode restores activated flight at ground level; Always On mode can activate it without double-Space. Disabling Please Stop resets the session assistance.
8. When a creative world/session first becomes eligible and `showToasts=true`, the client may show the launch reminder toast.
9. During the narrow movement check, the mod verifies:
   - local player exists;
   - config is enabled;
   - player is in creative mode;
   - player is flying;
   - movement inputs are released;
   - up/down inputs are either released or held together as vertical-neutral building input.
10. Before braking, the mod also excludes:
   - spectator state;
   - elytra/gliding state;
   - swimming state;
   - vehicle state;
   - recently-hurt knockback-adjacent state.
11. To avoid treating unrelated or server-correction-like velocity as creative-flight inertia, the brake only acts after recent flight input release or immediately after the keybind has toggled the mod on.
12. If all conditions are true, residual creative flight drift is cleared.
13. If jump and sneak are held together after horizontal movement is released, the brake may clear residual horizontal drift while preserving the player's vertical-neutral intent.
14. At ground level, Sneak alone is treated as neutral for horizontal braking so assisted flight remains no-inertia; in the air, Sneak remains active descent input.
15. Client camera mixins replace the crouching eye-height target with standing eye height and suppress the separate view-bobbing transform only while Please Stop is enabled and the eligible Creative player is flying, grounded, and holding Sneak.
16. If any condition is false, vanilla behavior continues.

## Failure Modes

- Config missing: recreate default `enabled=false`.
- Config invalid or corrupt: fall back to `enabled=false` and do not crash the client.
- Player absent or world not loaded: do nothing.
- Not creative flying: do nothing.
- Not creative mode: do not toggle `enabled`, do not show the launch toast, and do not show `Please Stop: ON/OFF` actionbar feedback.
- Excluded movement state: do nothing.
- Dedicated server classloading risk: avoid client-only classes outside client entrypoints, or document the jar as client-only.

## Proof Cases

- Build proof after scaffold: `./gradlew build`.
- Load proof after scaffold: client reaches title screen with Please Stop installed.
- Toggle proof after implementation: config tests pass; keybind flips `enabled`; config persists after restart.
- Toast proof after implementation: config tests pass; toast keybind flips `showToasts`; launch reminder shows only when creative and enabled by preference.
- Gameplay proof after implementation: creative flight drift stops only when enabled and input is released.
- Beta posture proof: the maintainer tests the beta jars across worlds and confirms creative-only UI behavior and creative-flight braking work as expected.
- Phase 4 diagnostic proof: while enabled, log active creative-flight input preservation and residual creative drift clearance; while disabled, log vanilla creative drift observation.
- Phase 5 exclusion proof: controller tests preserve velocity for survival/non-creative, spectator, elytra/gliding, vehicle, swimming, knockback-adjacent, and unknown/server-correction-like movement.
- Beta.3 client proof: the development-only Fabric client gametest opens settings with `M`, verifies the Persistent tooltip, drives the real keybindings in a Creative world, captures the screen, checks persistent ground flight, no-inertia release, standing-height camera stability, and actual view-bob suppression while Sneak remains held, then proves master-off and Survival no-op behavior. The gametest source set is not packaged in the public jar.

## Not In v1

- Server-side commands.
- Multiplayer policy enforcement.
- Anti-cheat bypass.
- Broad compatibility matrix.
- Release work beyond the approved public beta lane.
