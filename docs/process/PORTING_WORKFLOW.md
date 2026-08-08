# Please Stop Porting Workflow

## Working Order

1. Run repo preflight and confirm the beta.3 baseline is clean.
2. Read [PORTING_MAP.md](../porting/PORTING_MAP.md), the behavioral contract, config spec, runtime architecture, handoff, and lessons.
3. Change loader-neutral behavior in shared source first and keep its unit tests green.
4. Implement only the adapter/hook surface needed by one target.
5. Run that target's clean test/build command and inspect its jar.
6. Run the target's exact live checklist before moving its status to green.
7. Repeat for the next target; do not infer parity from another loader's build.
8. Run the all-target verifier and audit the final diff.

## Planned Layout

```text
src/shared/                         loader-neutral beta.3 rules and config
src/main/                           existing Fabric 1.21.11 adapter
ports/neoforge-1.21.1/             NeoForge line-jar build and 1.21.1 proof harness
ports/neoforge-1.21.11/            same-binary 1.21.11 compatibility harness; not a deliverable jar
ports/forge-1.20.1/                Forge line-jar build and 1.20.1 proof harness
scripts/verify-ports.sh             build and artifact-identity coordinator
```

The deliverables are one NeoForge `1.21.x` jar and one Forge `1.20.x` jar. Compatibility harnesses may compile version-specific checks, but they must test the unchanged line jar and must not create an additional shipped artifact. Neither deliverable depends on a separately installed common library.

## Proof Commands

| Target | Command |
| --- | --- |
| Fabric baseline | `./gradlew clean test build` |
| NeoForge 1.21.1 | `JAVA_HOME=$(/usr/libexec/java_home -v 21) ports/neoforge-1.21.1/gradlew -p ports/neoforge-1.21.1 clean test build` |
| NeoForge 1.21.11 | `JAVA_HOME=$(/usr/libexec/java_home -v 21) ports/neoforge-1.21.11/gradlew -p ports/neoforge-1.21.11 clean test build` |
| Forge 1.20.1 | `JAVA_HOME=<a Java 17 JDK> ports/forge-1.20.1/gradlew -p ports/forge-1.20.1 clean test build` |
| All automated gates | `scripts/verify-ports.sh` |

Java 17 is not currently installed on this machine. The Forge build remains blocked until a project-local/toolchain-downloaded or installed Java 17 runtime is available; do not silently compile the Forge jar for Java 21 instead.

## Live Evidence Packet

For each target preserve:

- exact jar path and SHA-256;
- loader and Minecraft version lines from `latest.log`;
- Please Stop initializer and toggle/config markers;
- screenshot or deterministic runtime evidence for the settings screen and ground-Sneak camera behavior;
- final `please_stop.json` showing `enabled=false`;
- clean-stop evidence with no matching Minecraft Java process.

Do not stage a port jar into a real profile until the maintainer authorizes profile mutation.
