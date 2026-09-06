# Please Stop Changelog

## 0.1.0-preview.1+26.3-pre.2 - Local Preview

- Prepare a non-public Fabric build for Minecraft `26.3-pre-2` using Java 25 and current preview APIs.
- Preserve an explicit Creative Flight Assist OFF choice through temporary movement exclusions.
- Use the player's current Creative state instead of the world's original/default game mode for Please Stop controls.
- Prevent ground-level Sneak by itself from authorizing a later brake of unrelated velocity.
- Require the complete 38-test preview suite and verify the built JAR before any later final-version work.

## 0.1.0-alpha.0+1.21.11 - Private Release Candidate

- Added the `Toggle Please Stop` keybind, defaulting to `B`.
- Added local persisted config at `config/please_stop.json`, defaulting to `enabled=false`.
- Added actionbar feedback for `Please Stop: ON` and `Please Stop: OFF`.
- Added the creative-flight brake for residual drift after movement, jump, or sneak input is released.
- Preserved active creative-flight input while held.
- Preserved vanilla creative-flight drift while disabled.
- Added exclusion gates for non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.
- Kept the artifact private; no public upload or release has been approved.
