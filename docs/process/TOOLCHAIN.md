# Please Stop Toolchain Truth

## Status

Phase 1 toolchain truth is pinned for the first Fabric scaffold.

These values were verified from primary/current metadata on 2026-07-04. If a later phase upgrades any value, update this file before changing build files.

## Planned Identity Values

- Display name: `Please Stop`
- Mod id: `please_stop`
- Proposed group/package root: `com.peetsamods.pleasestop`
- Proposed archive base name: `please-stop`
- Minecraft target: `1.21.11`
- Loader family: Fabric first

## Pinned Values

| Field | Pinned value | Source |
| --- | --- | --- |
| Minecraft version | `1.21.11` | the maintainer-provided target, confirmed in Mojang version manifest and Fabric game metadata |
| Java version | `21` | Mojang `1.21.11` version JSON reports `java-runtime-delta`, major version `21`; local Java is OpenJDK `21.0.10` |
| Fabric Loader | `0.19.3` | Fabric loader metadata for Minecraft `1.21.11` |
| Fabric API | `0.141.4+1.21.11` | Fabric Maven metadata, latest artifact ending in `+1.21.11` |
| Loom | `1.17.13` | Fabric Loom Maven metadata release value |
| Gradle wrapper | `9.6.1` | Gradle current release metadata |
| Mappings | Yarn `1.21.11+build.6` | Fabric Yarn Maven metadata, latest `1.21.11` build |
| Intermediary | `1.21.11` | Fabric loader/intermediary metadata for Minecraft `1.21.11` |

## Source URLs

- Mojang version manifest: `https://piston-meta.mojang.com/mc/game/version_manifest_v2.json`
- Mojang `1.21.11` version JSON: `https://piston-meta.mojang.com/v1/packages/e80b2fd4510abc6373058c3d96860009079e68c8/1.21.11.json`
- Fabric game metadata: `https://meta.fabricmc.net/v2/versions/game`
- Fabric loader metadata for `1.21.11`: `https://meta.fabricmc.net/v2/versions/loader/1.21.11`
- Fabric API Maven metadata: `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml`
- Fabric Loom Maven metadata: `https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml`
- Fabric Yarn Maven metadata: `https://maven.fabricmc.net/net/fabricmc/yarn/maven-metadata.xml`
- Gradle current release metadata: `https://services.gradle.org/versions/current`

## Verification Snapshot

- Mojang manifest contains `1.21.11` as a release with release time `2025-12-09T12:23:30+00:00`.
- Mojang `1.21.11` version JSON reports Java major version `21`.
- Fabric game metadata lists `1.21.11` as stable.
- Fabric loader metadata for `1.21.11` reports stable loader `0.19.3` and intermediary `1.21.11`.
- The approved `Slabbed+Terrain Slabs` Modrinth test profile currently launches Fabric Loader `0.19.2`; `fabric.mod.json` therefore uses runtime minimum `>=0.19.2` while the build toolchain remains pinned to `0.19.3`.
- Fabric API Maven metadata latest `1.21.11` artifact is `0.141.4+1.21.11`.
- Fabric Loom Maven metadata release is `1.17.13`.
- Fabric Yarn Maven metadata latest `1.21.11` artifact is `1.21.11+build.6`.
- Gradle current release metadata reports `9.6.1`.

## Accepted Evidence

Use the strongest available evidence:

1. the maintainer-provided exact target versions.
2. Official/current Fabric and Minecraft documentation.
3. A known-good local donor project for the same target.
4. Existing project build files, once scaffold exists.

If sources disagree later, stop and report the conflict before implementation.

## Expected Proof After Scaffold

- Build proof: `./gradlew build`
- Load proof: launch a local client and confirm Please Stop is listed and the client initializer runs without classloading errors.

## Side Boundaries

- Client-only: keybind, local config, local movement handling, optional local feedback.
- Shared/common: only metadata or code that is safe to load outside the client, if any.
- Server-only: none planned for v1.

## Stop Conditions

Stop before coding if:

- Any pinned value above is changed without updating this file.
- Fabric version compatibility becomes ambiguous.
- Java compatibility becomes unknown.
- Mappings are unclear enough to risk wrong method/class names.
- Client/server classloading boundaries are unclear.
