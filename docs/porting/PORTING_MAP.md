# Please Stop Porting Map

## Authority

This file is the implementation authority for the beta.3 Forge/NeoForge ports. It extends, but does not replace, the Fabric behavior contract in [please-stop-contract.md](../design/please-stop-contract.md).

## Canonical Baseline

- Behavior baseline: Please Stop `0.1.0-beta.3` at Fabric `main` commit `f5ec6d2`.
- Existing Fabric targets remain supported and are not converted to Forge or NeoForge.
- The port must preserve the beta.3 config fields, default values, keybind meanings, movement gates, Flight Assist modes, camera stabilization, and excluded states.

## Artifact Feasibility Gate

the maintainer's requested deliverable is two jars: one NeoForge jar for the `1.21.x` line and one Forge jar for the `1.20.x` line. A single jar spanning both loaders is not requested.

Verdict:

- Cross-loader universal jar: rejected and out of scope.
- One NeoForge `1.21.x` jar: candidate architecture; it must be the exact same binary proven on both `1.21.1` and `1.21.11`.
- One Forge `1.20.x` jar: candidate architecture; `1.20.1` is the required target from the active goal. No additional `1.20.x` version is claimed until the same binary is tested there.

Minecraft APIs and mixin targets can differ within a version line. Where direct binary compatibility fails, the line jar may contain narrowly selected version adapters or conditional mixins, but it must remain one shipped binary for that loader line.

## Target Matrix

| Deliverable | Required proof environments | Toolchains | Artifact identity | Status |
| --- | --- | --- | --- | --- |
| NeoForge `1.21.x` jar | Minecraft `1.21.1` + NeoForge `21.1.235`; Minecraft `1.21.11` + NeoForge `21.11.42` | ModDevGradle `2.0.141`, Java 21 | `please-stop-0.1.0-beta.3+1.21.x-neoforge.jar` | 1.21.1 GREEN at SHA-256 `b80884a6...3d69`; `BLOCKED_LINE_JAR_INFEASIBLE` for that exact binary on 1.21.11 pending the maintainer's artifact-plan decision |
| Forge `1.20.x` jar | Minecraft `1.20.1` + Forge `47.4.20` | ForgeGradle `6.0.54`, Java 17 | `please-stop-0.1.0-beta.3+1.20.x-forge.jar` | scaffold pending |

## Proof Evidence Ledger

- NeoForge `1.21.1`: [`tmp/port-proof/neoforge-1.21.1-proof-manifest.md`](../../tmp/port-proof/neoforge-1.21.1-proof-manifest.md) records the exact jar, binary-only harness, deterministic Creative fixture, the maintainer-accepted live behavior, packet-loop regression result, persisted final-off config, clean shutdown, and preserved artifacts.
- NeoForge `1.21.11`: [`tmp/port-proof/neoforge-1.21.11-incompatibility-manifest.md`](../../tmp/port-proof/neoforge-1.21.11-incompatibility-manifest.md) records the unchanged `b80884a6...3d69` jar's target-build and binary-runtime failures. No live/config claim is possible because mod construction fails before the title screen.
- The borrowed terrain world is failed-fixture history only and is not proof evidence.

## Source Ownership

| Concern | Shared | Target-specific |
| --- | --- | --- |
| Config schema/defaults/JSON behavior | yes | config-directory lookup |
| Flight Assist transition rules | yes | player state adapter and ability update |
| Inertia-brake decision rules | yes | input/velocity adapter and velocity mutation |
| Toast timing and camera eligibility | yes | actual toast, screen, keybind, and camera hooks |
| English translations | content-equivalent | loader resource layout |
| Loader entrypoint and metadata | no | yes |
| Mixin targets | no | yes, verified per Minecraft version |

## Required Proof Matrix

Every required proof environment owes all of the following before its line jar can be called green. The SHA-256 must be identical across the environments for the same line:

1. Clean unit-test and build command passes under the target Java version.
2. Final jar contains the correct loader metadata, entrypoint, shared logic, settings UI, and camera hook; it must not contain another loader's metadata.
3. Client reaches a local Creative world with no classloading or mixin failure.
4. `B` toggles Please Stop only in Creative and persists after restart.
5. `N` persists the toast preference.
6. `M` opens the settings screen and all three Flight Assist modes remain selectable.
7. `V` activates/deactivates eligible Creative Flight Assist.
8. Enabled input release brakes residual drift; disabled behavior preserves vanilla drift.
9. Ground-level Sneak preserves assisted flight, brakes horizontal drift, and suppresses crouch dip/view bob.
10. Survival, spectator, gliding, swimming, vehicles, recent damage, and airborne Sneak remain excluded.
11. Final config is left `enabled=false` after proof.

## Stop Conditions

- Stop if a target's official mappings do not expose a trustworthy equivalent hook.
- Stop if a port would need packet spoofing, server authority, or broadened movement behavior.
- Stop if the Fabric baseline changes while a port is being compared; rebase the behavior map before continuing.
- Stop before commit, tag, push, profile staging, upload, or publication without separate the maintainer authorization.
