# Please Stop Known Limitations

- Public builds cover Fabric `1.21.1`, `1.21.11`, and `26.2`, plus a NeoForge `1.21.x` line for `1.21.1` and `1.21.11`.
- The mod is a client convenience. It does not create server authority and does not bypass server correction.
- Multiplayer, anti-cheat behavior, and broad modpack compatibility are not claimed.
- Knockback proof is limited to the local recently-hurt gate; it is not proof of every external velocity source.
- The settings screen controls local preferences only. Key rebinds remain in Minecraft's standard Controls screen.
- The default state is disabled. A player must intentionally toggle it on.
- User-facing Please Stop toggle feedback and the launch reminder toast are creative-only.
- Toast visibility is controlled by `showToasts` in `config/please_stop.json` and the `Toggle Please Stop Toasts` keybind.
