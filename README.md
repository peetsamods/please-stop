# Please Stop

Please Stop is a tiny Minecraft mod idea for builders who are tired of creative flight drifting past the exact block they meant to place.

Planned v1 behavior:

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

This repo now has public Fabric beta.3 jars for `1.21.11`, `1.21.1`, and `26.2`, including Creative Flight Assist, settings, the creative-flight brake, exclusion gates, and creative-only feedback. Beta.4 is the local correction candidate that keeps an explicit Flight Assist OFF choice intact when the player later enters vanilla Creative flight.

Beta release docs:

- [Beta Candidate](docs/release/PUBLIC_RELEASE_CANDIDATE.md)
- [Changelog](docs/release/CHANGELOG.md)
- [Known Limitations](docs/release/KNOWN_LIMITATIONS.md)

License: [MIT](LICENSE).

Start with [docs/BINDER.md](docs/BINDER.md).
