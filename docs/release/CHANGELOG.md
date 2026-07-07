# Please Stop Changelog

## 0.1.0-beta.2 - Public Beta Candidate

- Fixed the creative-building case where holding jump and sneak together to stay level could still leave horizontal creative-flight drift after movement input was released.
- Rebuilt and staged all public beta artifacts with the beta.2 version line:
  - `0.1.0-beta.2+1.21.11`
  - `0.1.0-beta.2+1.21.1`
  - `0.1.0-beta.2+26.2`
- Preserved the existing creative-only UI hardening, toast toggle, local config, and movement exclusions from beta.1.

## 0.1.0-beta.1 - Public Beta Candidate

- Graduated from private testing to public beta after the maintainer tested the jars across worlds and confirmed the behavior works.
- Kept creative-only UI hardening: launch toasts, `Please Stop: ON/OFF`, and toast-toggle feedback do not appear in survival/non-creative worlds.
- Prepared the first public beta jars:
  - `0.1.0-beta.1+1.21.11`
  - `0.1.0-beta.1+1.21.1`
  - `0.1.0-beta.1+26.2`

## Test iteration - creative-only feedback and toast controls

- Added the `Toggle Please Stop Toasts` keybind, defaulting to `N`.
- Added local persisted `showToasts` config at `config/please_stop.json`, defaulting to `true`.
- Added a creative-only launch reminder toast: `Toggle Please Stop with B, configurable in key binds.`
- Kept `Toggle Please Stop` applicable only in creative mode; pressing it outside creative mode does not toggle `enabled` and does not show `Please Stop: ON/OFF`.
- Kept toast and toast-toggle actionbar feedback out of non-creative worlds.
- Prepared beta iterations for Minecraft `1.21.1` and `26.2`.

## 0.1.0-alpha.0+1.21.11 - Internal Release Candidate

- Added the `Toggle Please Stop` keybind, defaulting to `B`.
- Added local persisted config at `config/please_stop.json`, defaulting to `enabled=false`.
- Added actionbar feedback for `Please Stop: ON` and `Please Stop: OFF`.
- Added the creative-flight brake for residual drift after movement, jump, or sneak input is released.
- Fixed the remaining building case where holding jump and sneak together to stay level still allowed residual drift after movement input was released.
- Preserved active creative-flight input while held.
- Preserved vanilla creative-flight drift while disabled.
- Added exclusion gates for non-creative/non-flying movement, spectator, elytra/gliding, swimming, vehicles, recently-hurt knockback-adjacent movement, and unknown/server-correction-like velocity.
- Kept this alpha artifact internal; public release begins with `0.1.0-beta.1`.
