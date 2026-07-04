# Please Stop

Please Stop is a tiny Minecraft mod idea for builders who are tired of creative flight drifting past the exact block they meant to place.

Planned v1 behavior:

- Fabric-first client mod.
- Display name: `Please Stop`.
- Mod id: `please_stop`.
- Keybind plus persistent local config.
- Creative-only launch reminder toast with a keybind to disable toasts.
- When enabled, creative flight inertia stops immediately after movement, jump, or sneak input is released.
- When disabled, vanilla creative flight behavior remains unchanged.

This repo now has a Fabric client scaffold with the `Toggle Please Stop` keybind, local persisted config, the creative-flight brake, exclusion gates, creative-only user-facing feedback, and beta jars. the maintainer has tested the current behavior across worlds, so the project status is public beta candidate.

Beta release docs:

- [Beta Candidate](docs/release/PRIVATE_RELEASE_CANDIDATE.md)
- [Changelog](docs/release/CHANGELOG.md)
- [Known Limitations](docs/release/KNOWN_LIMITATIONS.md)

License: [MIT](LICENSE).

Start with [docs/BINDER.md](docs/BINDER.md).
