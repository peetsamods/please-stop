# Please Stop Changelog

## 1.0.0 - Stable Release

### FIXED
- Holding Sneak at ground level no longer repeatedly cancels and restores assisted Creative flight or resends flight updates.
- Settings headings and help text remain readable over the blurred background.

### IMPROVED
- Settings help wraps to fit compact windows.

## 0.1.0-beta.5 - Fabric Release Candidate

- Keep an explicit Creative Flight Assist OFF choice through temporary exclusions such as swimming, riding, gliding, recent damage, a flight lock, or a temporary loss of flight permission.
- Use one shared current-Creative rule for Fabric and NeoForge controls, even when a single-player world was originally created in another game mode.
- Prevent ground-level Sneak by itself from authorizing a later brake of unrelated velocity.
- Require count-gated tests and release-JAR identity, metadata, package, and development-content checks on every Fabric beta.5 target.

## 0.1.0-beta.4 - NeoForge Release Candidate

- Include the beta.5 behavior repairs: manual Flight Assist OFF survives temporary exclusions, controls follow the player's current Creative state, and ground-level Sneak does not authorize braking unrelated velocity.
- Include the earlier fix that keeps manual Flight Assist OFF intact across later vanilla Creative-flight activation.
- Limit the line JAR to the tested Minecraft `1.21.1` and `1.21.11` targets.
- Use privacy-clean package and author metadata and require the release-JAR verifier before publication.
- Require both version-specific 17-test builds, compiled-hook checks, and exact same-JAR runtime and live acceptance on both versions.

## 0.1.0-beta.4 - Public Beta

- Fixed Creative Flight Assist silently turning itself back on when the player entered vanilla Creative flight after explicitly turning Flight Assist OFF.
- Kept mode changes and the Please Stop master-switch session reset as the intentional ways to clear that manual OFF choice.
- Published matching Fabric builds for `1.21.11`, `1.21.1`, and `26.2`.

## 0.1.0-beta.3 - Public Beta

- Added a native Please Stop Settings screen, opened with `M` by default, for the local enablement, launch-toast, and Creative Flight Assist preferences.
- Added explanatory tooltips, including a clear distinction between `Off (vanilla)` Flight Assist and the Please Stop master switch.
- Added `Toggle Creative Flight Assist`, default `V`, which uses Minecraft's normal Creative ability update rather than a local-only flight shortcut.
- Added configurable Flight Assist modes: Off (vanilla), Persistent after activation, and Always on in Creative.
- Made ground-level Flight Assist and no-inertia braking work while Sneak remains held, without changing airborne Sneak descent.
- Removed both Minecraft's crouch camera dip and its separate view-bobbing transform while Sneak is held during enabled ground-level Creative flight.
- Built and source-tested beta.3 candidates for `1.21.11`, `1.21.1`, and `26.2`.
- Published the matching beta.3 Fabric files for all three supported versions.

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
