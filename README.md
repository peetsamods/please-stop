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

This repo now has a Fabric client scaffold with the `Toggle Please Stop` keybind, local persisted config, the creative-flight brake, exclusion gates, creative-only user-facing feedback, and public beta.2 jars. Beta.3 adds Creative Flight Assist and settings; all three supported versions build, pass source tests, and are staged locally as `TEST` jars, but none is published yet.

Beta release docs:

- [Beta Candidate](docs/release/PUBLIC_RELEASE_CANDIDATE.md)
- [Changelog](docs/release/CHANGELOG.md)
- [Known Limitations](docs/release/KNOWN_LIMITATIONS.md)

License: [MIT](LICENSE).

Start with [docs/BINDER.md](docs/BINDER.md).
