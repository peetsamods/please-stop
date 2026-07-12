# Please Stop Config Spec

## Authority

This file defines the planned v1 config surface. It must stay consistent with [please-stop-contract.md](please-stop-contract.md) and [runtime-architecture.md](runtime-architecture.md).

## v1 Config Fields

| Field | Type | Default | Scope | Meaning |
| --- | --- | --- | --- | --- |
| `enabled` | boolean | `false` | local client | Whether Please Stop removes creative flight drift when the contract conditions are met. |
| `showToasts` | boolean | `true` | local client | Whether Please Stop may show the creative-only launch reminder toast. |
| `creativeFlightAssistMode` | enum | `PERSISTENT_AFTER_ACTIVATION` | local client | How Creative Flight Assist makes no-inertia flight available at ground level. |

## Creative Flight Assist Modes

Flight Assist controls only automatic Creative-flight activation/restoration. Choosing `VANILLA` keeps Please Stop's inertia braking and Sneak camera stabilization enabled; turning the Please Stop master switch off disables every feature.

| Mode | Meaning |
| --- | --- |
| `VANILLA` | Please Stop does not activate or restore Creative flight. |
| `PERSISTENT_AFTER_ACTIVATION` | Once Creative flight has been activated, Flight Assist restores it when the player returns to ground level. This is the default. |
| `ALWAYS_ON_IN_CREATIVE` | Flight Assist activates Creative flight whenever the local player is safely eligible, without double-Space. |

## Keybind

- Name: `Toggle Please Stop`
- Default key: `B`
- Action: invert `enabled` only when the local player is in creative mode
- Persistence: save the new value to the local config
- Feedback: in creative mode, local actionbar text shows `Please Stop: ON` or `Please Stop: OFF`

## Toast Keybind

- Name: `Toggle Please Stop Toasts`
- Default key: `N`
- Action: invert `showToasts`
- Persistence: save the new value to the local config
- Feedback: in creative mode, local actionbar text shows `Please Stop toasts: ON` or `Please Stop toasts: OFF`

## Flight Assist Keybind

- Name: `Toggle Creative Flight Assist`
- Default key: `V`
- Action: activate or deactivate the current session's Flight Assist using Minecraft's normal Creative ability update.
- Master gate: no-op while `enabled=false`, so Flight Assist never creates a flight state without Please Stop's no-inertia brake.
- Safety: no-op unless the local player is an eligible Creative player with flight permission; it respects spectator, gliding, swimming, vehicles, recent damage, and a flight lock.

## Settings Screen

- Name: `Please Stop Settings`
- Default key: `M` (`O` conflicted in the tested 26.2 profile).
- Controls: Please Stop enablement, Creative Flight Assist mode, and launch reminder toast preference, each with an explanatory tooltip.
- Key rebinds: available through the screen's `Key Bindings...` button and Minecraft's standard Controls screen.

## Defaults

- `enabled` defaults to `false`.
- `showToasts` defaults to `true`.
- The player must intentionally turn it on.
- Missing config should recreate the default.
- Invalid config should fall back safely to `enabled=false` and `showToasts=true`.
- A non-boolean field should fall back to that field's default and report the issue only if diagnostics exist.

## File Location

- Pinned location: Fabric Loader config dir plus `please_stop.json`.
- In the approved Modrinth profile, that resolves to `config/please_stop.json` inside the profile root.
- Format: JSON object with boolean fields `enabled` and `showToasts`.
- Parser/writer: Gson.

## Migration Posture

- If later versions add fields, missing values should use documented defaults.
- Do not break old `enabled` values without a migration note.

## Deferred Settings

These are not v1 requirements:

- Default-enabled preference.
- Per-world or per-server profile.
- Separate horizontal/vertical brake settings.
