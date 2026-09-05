# Please Stop

Please Stop is a tiny Minecraft mod idea for builders who are tired of creative flight drifting past the exact block they meant to place.

Current behavior:

- Fabric-first client mod.
- Display name: `Please Stop`.
- Mod id: `please_stop`.
- Keybind plus persistent local config.
- Native settings screen (`M`) with explanatory tooltips and a configurable Creative Flight Assist mode.
- Creative Flight Assist (`V`) can keep no-inertia Creative flight available at ground level after activation, or start it automatically in Creative if configured.
- Ground-level Sneak keeps the assisted camera steady instead of applying Minecraft's crouch dip/bob.
- Creative-only launch reminder toast with a keybind to disable toasts.
- When enabled, creative flight inertia stops immediately after movement, jump, or sneak input is released.
- When disabled, vanilla creative flight behavior remains unchanged.

Public releases currently include Fabric beta.4 for `1.21.11`, `1.21.1`, and `26.2`, plus NeoForge beta.3 for the `1.21.x` line. The next unreleased mainline builds are Fabric beta.5 and NeoForge beta.4; they tighten Creative eligibility, preserve explicit Flight Assist choices through temporary exclusions, and strengthen compatibility and release-artifact checks.

Project docs:

- [Changelog](docs/release/CHANGELOG.md)
- [Known Limitations](docs/release/KNOWN_LIMITATIONS.md)
- [Behavior Contract](docs/design/please-stop-contract.md)
- [Configuration](docs/design/config-spec.md)
- [Porting Map](docs/porting/PORTING_MAP.md)

License: [MIT](LICENSE).
