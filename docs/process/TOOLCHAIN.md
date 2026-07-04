# Please Stop Toolchain Truth

## Status

Minecraft target is `1.21.11`. The rest of toolchain truth is not pinned yet. This is intentional until Phase 1 records official-source verification.

Please Stop is Fabric-first. Exact Fabric Loader, Fabric API, Loom, Gradle, mappings, and Java versions must be verified from official/current sources before scaffold work begins.

## Planned Identity Values

- Display name: `Please Stop`
- Mod id: `please_stop`
- Proposed group/package root: `com.peetsamods.pleasestop`
- Proposed archive base name: `please-stop`
- Minecraft target: `1.21.11`
- Loader family: Fabric first

## Values To Verify Before Scaffold

| Field | Current value | Required source before pinning |
| --- | --- | --- |
| Minecraft version | `1.21.11` | the maintainer-provided target; confirm against Mojang and Fabric metadata in Phase 1 |
| Fabric Loader version | Unpinned | Fabric official source |
| Fabric API version | Unpinned | Fabric official source for chosen Minecraft version |
| Loom version | Unpinned | Fabric/Loom official source |
| Gradle version | Unpinned | Loom/Fabric compatibility or generated wrapper source |
| Java version | Unpinned | Minecraft/Fabric compatibility for chosen target |
| Mappings | Unpinned | Yarn or approved mapping source for chosen target |

## Accepted Evidence

Use the strongest available evidence:

1. the maintainer-provided exact target versions.
2. Official/current Fabric and Minecraft documentation.
3. A known-good local donor project for the same target.
4. Existing project build files, once scaffold exists.

If sources disagree, stop and report the conflict before implementation.

## Expected Proof After Scaffold

- Build proof: `./gradlew build`
- Load proof: launch a local client and confirm Minecraft reaches title screen with Please Stop installed.

## Side Boundaries

- Client-only: keybind, local config, local movement handling, optional local feedback.
- Shared/common: only metadata or code that is safe to load outside the client, if any.
- Server-only: none planned for v1.

## Stop Conditions

Stop before coding if:

- Minecraft version is ambiguous.
- Fabric version compatibility is ambiguous.
- Java compatibility is unknown.
- Mappings are unclear enough to risk wrong method/class names.
- Client/server classloading boundaries are unclear.
