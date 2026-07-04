# Please Stop Config Spec

## Authority

This file defines the planned v1 config surface. It must stay consistent with [please-stop-contract.md](please-stop-contract.md) and [runtime-architecture.md](runtime-architecture.md).

## v1 Config Fields

| Field | Type | Default | Scope | Meaning |
| --- | --- | --- | --- | --- |
| `enabled` | boolean | `false` | local client | Whether Please Stop removes creative flight drift when the contract conditions are met. |

## Keybind

- Name: `Toggle Please Stop`
- Action: invert `enabled`
- Persistence: save the new value to the local config
- Feedback: minimal local feedback is allowed, but not required for the first proof

## Defaults

- `enabled` defaults to `false`.
- The player must intentionally turn it on.
- Missing config should recreate the default.
- Invalid config should fall back safely to `false` and report the issue only if diagnostics exist.

## File Location

- Not pinned yet.
- The exact config path and library, if any, must be chosen after Fabric/toolchain verification.
- The first implementation should prefer the simplest local client persistence pattern already accepted for the chosen loader/version.

## Migration Posture

- v1 has one field, so no migration format is needed yet.
- If later versions add fields, missing values should use documented defaults.
- Do not break old `enabled` values without a migration note.

## Deferred Settings

These are not v1 requirements:

- HUD/status indicator.
- Default-enabled preference.
- Per-world or per-server profile.
- Separate horizontal/vertical brake settings.
- Config screen.
