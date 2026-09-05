# Please Stop Porting Workflow

## Working Order

1. Run repo preflight and confirm the current `main` baseline is clean.
2. Read [PORTING_MAP.md](../porting/PORTING_MAP.md), the behavioral contract, config spec, and runtime architecture.
3. Change loader-neutral behavior in shared source first and keep its unit tests green.
4. Implement only the adapter/hook surface needed by one target.
5. Run that target's clean test/build command and inspect its jar.
6. Run the target's exact live checklist before moving its status to green.
7. Repeat for the next target; do not infer parity from another loader's build.
8. Run the all-target verifier and audit the final diff.

## Planned Layout

```text
src/shared/                         loader-neutral rules and config
src/main/                           existing Fabric 1.21.11 adapter
ports/neoforge-1.21.1/             NeoForge line-jar build and 1.21.1 proof harness
ports/neoforge-1.21.11/            1.21.11 source/compiled-hook compatibility harness; not a deliverable jar
ports/neoforge-1.21.1-runtime/     exact line-jar runtime harness for 1.21.1
ports/neoforge-1.21.11-runtime/    exact same line-jar runtime harness for 1.21.11
```

The NeoForge deliverable is one `1.21.x` jar. The version-specific source harnesses check compilation and hook shape, while the runtime harnesses must load the unchanged line jar. A separately built `1.21.11` harness jar is proof scaffolding, not a second release artifact.

## Proof Commands

| Target | Command |
| --- | --- |
| Fabric baseline | `./gradlew --no-daemon clean test build` |
| NeoForge 1.21.1 | `ports/neoforge-1.21.1/gradlew --no-daemon -p ports/neoforge-1.21.1 clean test build` |
| NeoForge 1.21.11 | `ports/neoforge-1.21.1/gradlew --no-daemon -p ports/neoforge-1.21.11 clean test build` |
| Release-JAR unit checks | `python3 -m unittest tools/test_verify_release_artifact.py` |
| Exact line-jar binding | Run `verifyProductionJar` in both `neoforge-1.21.1-runtime` and `neoforge-1.21.11-runtime` after updating their candidate path and expected SHA-256 |

All current Fabric and NeoForge builds use Java 21. A build and source-harness green does not replace the two exact-JAR runtime checks.

## Live Evidence Packet

For each target preserve:

- exact jar path and SHA-256;
- loader and Minecraft version lines from `latest.log`;
- Please Stop initializer and toggle/config markers;
- screenshot or deterministic runtime evidence for the settings screen and ground-Sneak camera behavior;
- final `please_stop.json` showing `enabled=false`;
- clean-stop evidence with no matching Minecraft Java process.

Do not stage a port jar into a real profile until the maintainer authorizes profile mutation.
