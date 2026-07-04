# Please Stop Config Spec

## Authority

This file defines the planned v1 config surface. It must stay consistent with [please-stop-contract.md](please-stop-contract.md) and [runtime-architecture.md](runtime-architecture.md).

## v1 Config Fields

| Field | Type | Default | Scope | Meaning |
| --- | --- | --- | --- | --- |
| `enabled` | boolean | `false` | local client | Whether Please Stop removes creative flight drift when the contract conditions are met. |

## Keybind

- Name: `Toggle Please Stop`
- Default key: `B`
- Action: invert `enabled`
- Persistence: save the new value to the local config
- Feedback: local actionbar text shows `Please Stop: ON` or `Please Stop: OFF`

## Defaults

- `enabled` defaults to `false`.
- The player must intentionally turn it on.
- Missing config should recreate the default.
- Invalid config or a non-boolean `enabled` value should fall back safely to `false` and report the issue only if diagnostics exist.

## File Location

- Pinned location: Fabric Loader config dir plus `please_stop.json`.
- In the approved Modrinth profile, that resolves to `config/please_stop.json` inside the profile root.
- Format: JSON object with one boolean field, `enabled`.
- Parser/writer: Gson.

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
