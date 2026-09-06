# Please Stop Changelog

## 0.1.0-beta.5+26.2 - Release Candidate

- Preserve an explicit Creative Flight Assist OFF choice through temporary movement exclusions.
- Use the player's current Creative state instead of the world's original/default game mode for Please Stop controls.
- Prevent ground-level Sneak by itself from authorizing a later brake of unrelated velocity.
- Require the complete 38-test Fabric 26.2 suite and verify the built release JAR before publication.

## 0.1.0-alpha.0+1.21.11 - Private Release Candidate

- Added the `Toggle Please Stop` keybind, defaulting to `B`.
- Added local persisted config at `config/please_stop.json`, defaulting to `enabled=false`.
- Added actionbar feedback for `Please Stop: ON` and `Please Stop: OFF`.
- Added the creative-flight brake for residual drift after movement, jump, or sneak input is released.
- Preserved active creative-flight input while held.
- Preserved vanilla creative-flight drift while disabled.
- Added exclusion gates for non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.
- Kept the artifact private; no public upload or release has been approved.
