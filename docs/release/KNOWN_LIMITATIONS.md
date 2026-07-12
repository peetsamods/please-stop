# Please Stop Known Limitations

- This is a Fabric client mod for Minecraft `1.21.1`, `1.21.11`, and `26.2`.
- The current candidate has graduated to public beta posture after the maintainer's cross-world testing.
- The mod is a client convenience. It does not create server authority and does not bypass server correction.
- Multiplayer, anti-cheat behavior, and broad modpack compatibility are not claimed.
- Knockback proof is limited to the local recently-hurt gate; it is not proof of every external velocity source.
- Beta.3 adds a small native settings screen. All three supported versions are source-tested locally and the deterministic `1.21.11` Creative client proof is green. The matching local Modrinth profile jars are labeled `TEST`; the maintainer's follow-up 26.2 playtest confirmed the camera behavior, but publishing is still pending.
- The settings screen controls local preferences only. Key rebinds remain in Minecraft's standard Controls screen.
- The default state is disabled. the maintainer must intentionally toggle it on.
- User-facing Please Stop toggle feedback and the launch reminder toast are creative-only.
- Toast visibility is controlled by `showToasts` in `config/please_stop.json` and the `Toggle Please Stop Toasts` keybind.
