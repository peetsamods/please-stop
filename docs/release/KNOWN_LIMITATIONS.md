# Please Stop Known Limitations

- This is a Fabric client mod for Minecraft `1.21.1`, `1.21.11`, and `26.2`.
- The current candidate has graduated to public beta posture after the maintainer's cross-world testing.
- The mod is a client convenience. It does not create server authority and does not bypass server correction.
- Multiplayer, anti-cheat behavior, and broad modpack compatibility are not claimed.
- Knockback proof is limited to the local recently-hurt gate; it is not proof of every external velocity source.
- Beta.3 is public and adds the small native settings screen and Creative Flight Assist. Beta.4 is the local correction candidate for preserving an explicit Flight Assist OFF choice across later vanilla flight entry; focused tests pass on all three versions and the deterministic `1.21.11` Creative client proof is green. Profile staging and publication remain pending.
- The settings screen controls local preferences only. Key rebinds remain in Minecraft's standard Controls screen.
- The default state is disabled. the maintainer must intentionally toggle it on.
- User-facing Please Stop toggle feedback and the launch reminder toast are creative-only.
- Toast visibility is controlled by `showToasts` in `config/please_stop.json` and the `Toggle Please Stop Toasts` keybind.
