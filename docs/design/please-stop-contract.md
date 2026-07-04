# Please Stop Behavioral Contract

## Core Law

Please Stop exists to stop unwanted residual creative flight drift when the maintainer toggles it on. It must feel like saying "please stop" to the local creative flight inertia, not like replacing Minecraft movement.

## Terms

- `Enabled`: the local persisted toggle is on.
- `Creative flying`: the local player is in creative mode and currently flying.
- `Residual drift`: continued motion after movement, jump, and sneak flight inputs are released.
- `Active input`: the player is currently holding movement, jump, or sneak input.

## Allowed Behavior

- When enabled and the local player is creative flying, residual drift may be stopped immediately after active flight inputs are released.
- When active input is held, vanilla creative flight movement speed and direction should remain normal.
- When disabled, vanilla creative flight behavior should remain unchanged.
- The keybind may toggle the local enabled state.
- The config may persist the enabled state locally.

## Forbidden Behavior

- Do not change survival movement.
- Do not change spectator movement.
- Do not change elytra movement.
- Do not change swimming movement.
- Do not change vehicle movement.
- Do not change knockback behavior intentionally.
- Do not change max creative flight speed.
- Do not grant server authority or bypass server correction.
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
- Active input still moves normally.
- Excluded states show no intentional behavior change.

## Not Claimed Yet

- Public release readiness.
- Compatibility with every modpack.
- Anti-cheat safety.
- Multiplayer policy approval.
- Support outside the first verified Minecraft/Fabric target.
