# Please Stop Runtime Architecture

## Runtime Shape

Please Stop is planned as a Fabric-first client mod. The first implementation should be as small as possible:

- Client initializer for keybind/config setup.
- Config manager for loading, saving, and validating `enabled`.
- Keybind handler for flipping `enabled` and saving the new state.
- Inertia controller for checking eligibility and applying the narrow drift stop.
- No server command surface in v1.

## Ownership

| Concern | Owner | Notes |
| --- | --- | --- |
| Toggle keybind | Client | Toggles local `enabled` state. |
| Config persistence | Client | Stores only local preference. |
| Movement check | Client | Applies only to local creative flight behavior. |
| Server authority | Server | Not changed by this mod. |
| Release policy | the maintainer | Public upload requires explicit approval. |

## Data Flow

1. Client starts.
2. Config loads `enabled`, defaulting to `false` if missing.
3. Keybind registration exposes `Toggle Please Stop`.
4. During the narrow movement check, the mod verifies:
   - local player exists;
   - config is enabled;
   - player is in creative mode;
   - player is flying;
   - movement/up/down inputs are released.
5. If all conditions are true, residual creative flight drift is cleared.
6. If any condition is false, vanilla behavior continues.

## Failure Modes

- Config missing: recreate default `enabled=false`.
- Config invalid or corrupt: fall back to `enabled=false` and do not crash the client.
- Player absent or world not loaded: do nothing.
- Not creative flying: do nothing.
- Dedicated server classloading risk: avoid client-only classes outside client entrypoints, or document the jar as client-only.

## Proof Cases

- Build proof after scaffold: `./gradlew build`.
- Load proof after scaffold: client reaches title screen with Please Stop installed.
- Toggle proof after implementation: keybind flips `enabled` and persists after restart.
- Gameplay proof after implementation: creative flight drift stops only when enabled and input is released.
- Exclusion proof: survival, spectator, elytra, vehicle, swimming, and knockback-adjacent states are not intentionally changed.

## Not In v1

- Server-side commands.
- Multiplayer policy enforcement.
- Anti-cheat bypass.
- Broad compatibility matrix.
- Public release upload.
