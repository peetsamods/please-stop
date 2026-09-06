# Please Stop Toolchain Truth

## Status

Phase 1 toolchain truth is pinned for the Fabric 26.3-pre-2 preview.

These values were verified from primary/current metadata on 2026-09-06. If a later phase upgrades any value, update this file before changing build files.

## Planned Identity Values

- Display name: `Please Stop`
- Mod id: `please_stop`
- Proposed group/package root: `com.peetsamods.pleasestop`
- Proposed archive base name: `please-stop`
- Minecraft target: `26.3-pre-2` (preview)
- Loader family: Fabric first

## Pinned Values

| Field | Pinned value | Source |
| --- | --- | --- |
| Minecraft version | `26.3-pre-2` | Fabric game metadata marks this as a non-stable preview |
| Java version | `25` | required by the Minecraft 26.3 toolchain |
| Fabric Loader | `0.19.5` | Fabric loader metadata for Minecraft `26.3-pre-2` |
| Fabric API | `0.159.4+26.3` | Fabric Maven metadata, latest artifact ending in `+26.3` |
| Loom | `1.17.20` | selected Loom release compatible with this preview |
| Gradle wrapper | `9.6.1` | Gradle current release metadata |
| Mappings | official Mojang mappings via Loom | this build does not declare a Yarn dependency |
| Intermediary | `0.0.0` | Fabric loader metadata for Minecraft `26.3-pre-2` |
| JUnit Jupiter | `6.1.1` | Maven Central/Sonatype artifact metadata |

## Source URLs

- Mojang version manifest: `https://piston-meta.mojang.com/mc/game/version_manifest_v2.json`
- Fabric game metadata: `https://meta.fabricmc.net/v2/versions/game`
- Fabric loader metadata for `26.3-pre-2`: `https://meta.fabricmc.net/v2/versions/loader/26.3-pre-2`
- Fabric API Maven metadata: `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml`
- Fabric Loom Maven metadata: `https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml`
- Gradle current release metadata: `https://services.gradle.org/versions/current`
- JUnit Jupiter artifact metadata: `https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter`

## Verification Snapshot

- Fabric game metadata lists `26.3-pre-2` as non-stable.
- Fabric loader metadata for `26.3-pre-2` reports stable loader `0.19.5` and intermediary `0.0.0`.
- Fabric API Maven metadata latest `+26.3` artifact is `0.159.4+26.3`.
- Loom `1.17.20` is the selected release for the preview build.
- The build uses Loom's official Mojang mappings; no Yarn mapping coordinate is declared.
- Gradle current release metadata reports `9.6.1`.
- Maven Central/Sonatype reports JUnit Jupiter `6.1.1`; Gradle `9.6.1` test runtime requires the matching JUnit Platform launcher explicitly.

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
- Config/toggle proof: `./gradlew test build`, then live-launch and verify keybind persistence in the approved profile.

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
