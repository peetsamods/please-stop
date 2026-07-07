# Please Stop Known Limitations

- This is a Fabric client mod for Minecraft `1.21.1`, `1.21.11`, and `26.2`.
- The current candidate has graduated to public beta posture after the maintainer's cross-world testing.
- The mod is a client convenience. It does not create server authority and does not bypass server correction.
- Multiplayer, anti-cheat behavior, and broad modpack compatibility are not claimed.
- Knockback proof is limited to the local recently-hurt gate; it is not proof of every external velocity source.
- There is no config screen yet. The v1 surface is keybind plus `config/please_stop.json`.
- The default state is disabled. the maintainer must intentionally toggle it on.
- User-facing Please Stop toggle feedback and the launch reminder toast are creative-only.
- Toast visibility is controlled by `showToasts` in `config/please_stop.json` and the `Toggle Please Stop Toasts` keybind.
