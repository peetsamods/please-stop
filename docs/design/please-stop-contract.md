# Please Stop Behavioral Contract

## Core Law

Please Stop exists to stop unwanted residual creative flight drift when the maintainer toggles it on. It must feel like saying "please stop" to the local creative flight inertia, not like replacing Minecraft movement.

## Terms

- `Enabled`: the local persisted toggle is on.
- `Creative flying`: the local player is in creative mode and currently flying.
- `Residual drift`: continued motion after movement or net vertical flight input is released.
- `Active input`: the player is currently holding movement input, jump, or airborne Sneak input. Ground-level Sneak alone does not block the no-inertia brake.
- `Vertical-neutral input`: the player is holding jump and sneak together to stay level while building; this does not count as active vertical flight input by itself.

## Allowed Behavior

- When enabled and the local player is creative flying, residual drift may be stopped immediately after active flight inputs are released.
- When active input is held, vanilla creative flight movement speed and direction should remain normal.
- When jump and sneak are held together after movement input is released, horizontal residual drift may be stopped while the player stays level.
- While assisted Creative flight is at ground level, holding Sneak must not cancel Flight Assist or prevent horizontal residual drift from stopping after movement is released.
- During that same enabled ground-Sneak state, the camera may use standing eye height and suppress Minecraft's view-bobbing transform.
- While airborne, Sneak remains normal active descent input and must not be braked away.
- When disabled, vanilla creative flight behavior should remain unchanged.
- The keybind may toggle the local enabled state.
- The config may persist the enabled state locally.
- When Please Stop is enabled, Creative Flight Assist may use Minecraft's normal Creative ability update to activate or restore local Creative flight. It must only do this for an eligible Creative player who already has permission to fly.
- In `Persistent after activation` mode, an activated Flight Assist may restore flight only when the player returns to ground level. In `Always on in Creative` mode, it may activate flight without double-Space.
- The Flight Assist keybind may explicitly turn this per-session assistance on or off.
- `VANILLA` Flight Assist mode disables only automatic flight activation/restoration; it does not disable Please Stop's inertia brake or eligible Sneak camera stabilization. The master switch disables all Please Stop behavior.

## Forbidden Behavior

- Do not change survival movement.
- Do not change spectator movement.
- Do not change elytra movement.
- Do not change swimming movement.
- Do not change vehicle movement.
- Do not change knockback behavior intentionally.
- Do not change max creative flight speed.
- Do not suppress normal crouch camera motion outside enabled, eligible, ground-level Creative flight.
- Do not grant server authority or bypass server correction.
- Do not force flight while the player is outside the Creative ability state, in an excluded movement state, recently hurt, or under a server/client flight lock.
- Do not claim anti-cheat compatibility until tested in a named environment.

## Client And Server Invariants

- v1 is a client convenience mod.
- Client config, keybind state, and local movement handling do not create server authority.
- The mod should avoid loading client-only classes on a dedicated server, or the docs must state clearly that the artifact is client-only.

## Performance Invariants

- The runtime check should be narrow: local player, enabled state, creative flying state, and input state.
- No broad world scans.
- No packet spam.
- No recurring background work unrelated to the movement check.

## Proof Obligations

- Toggle off preserves vanilla creative flight drift.
- Toggle on stops creative flight drift after input release.
- Toggle on stops creative flight drift after horizontal input release even when jump and sneak are held together.
- Ground-level Flight Assist remains active and stops horizontal drift when Sneak stays held after movement is released.
- Ground-level Sneak keeps a stable camera while Please Stop and Creative flight are active.
- Airborne Sneak still descends normally.
- Active input still moves normally.
- Excluded states show no intentional behavior change.

## Not Claimed Yet

- Broad release readiness beyond the approved public beta.
- Compatibility with every modpack.
- Anti-cheat safety.
- Multiplayer policy approval.
- Support outside the first verified Minecraft/Fabric target.
