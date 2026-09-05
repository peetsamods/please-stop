#!/usr/bin/env python3
"""Verify that one Please Stop release JAR contains only release-safe content."""

from __future__ import annotations

import argparse
import json
import re
import sys
import zipfile
from collections.abc import Iterable
from pathlib import Path


MOD_ID = "please_stop"
PACKAGE_PREFIX = "com/peetsamods/pleasestop/"
SAFE_AUTHOR = "Peetsa"
FABRIC_METADATA = "fabric.mod.json"
NEOFORGE_METADATA = "META-INF/neoforge.mods.toml"
OTHER_LOADER_METADATA = {
    "fabric": {NEOFORGE_METADATA, "META-INF/mods.toml"},
    "neoforge": {FABRIC_METADATA, "META-INF/mods.toml"},
}
DRIVE_ROOT = re.compile(r"^[A-Za-z]:[\\/]")
TOML_ARRAY_TABLE = re.compile(r"^\[\[([A-Za-z0-9_.-]+)\]\]$")
TOML_STRING_VALUE = re.compile(r"^([A-Za-z0-9_-]+)\s*=\s*([\"'])(.*?)\2$")


class ArtifactVerificationError(Exception):
    """A release JAR does not satisfy the project release contract."""


def parse_args(argv: list[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", required=True, type=Path, help="built release JAR to inspect")
    parser.add_argument("--loader", required=True, choices=("fabric", "neoforge"))
    parser.add_argument("--version", required=True, help="exact expected Please Stop version")
    parser.add_argument("--minecraft", required=True, help="exact expected Minecraft dependency value")
    return parser.parse_args(argv)


def verify_release_artifact(
        jar_path: Path,
        expected_loader: str,
        expected_version: str,
        expected_minecraft: str,
) -> None:
    if jar_path.name.endswith("-sources.jar"):
        raise ArtifactVerificationError("sources JARs cannot be released")
    if not jar_path.is_file():
        raise ArtifactVerificationError(f"release JAR does not exist: {jar_path}")

    try:
        with zipfile.ZipFile(jar_path) as archive:
            infos = archive.infolist()
            _verify_entries(infos)
            names = {info.filename for info in infos}
            _verify_loader_metadata(names, expected_loader)
            _verify_metadata(archive, expected_loader, expected_version, expected_minecraft)
    except zipfile.BadZipFile as error:
        raise ArtifactVerificationError("release artifact is not a readable JAR") from error


def _verify_entries(infos: Iterable[zipfile.ZipInfo]) -> None:
    seen: set[str] = set()
    for info in infos:
        name = info.filename
        if name in seen:
            raise ArtifactVerificationError(f"duplicate archive entry: {name}")
        seen.add(name)
        _verify_entry_path(name)
        _verify_release_content(name)


def _verify_entry_path(name: str) -> None:
    if not name or "\x00" in name or name.startswith(("/", "\\")) or DRIVE_ROOT.match(name):
        raise ArtifactVerificationError(f"absolute archive entry path: {name!r}")
    if "\\" in name or "//" in name:
        raise ArtifactVerificationError(f"non-canonical archive entry path: {name!r}")
    path_without_directory_marker = name[:-1] if name.endswith("/") else name
    if any(part in {"", ".", ".."} for part in path_without_directory_marker.split("/")):
        raise ArtifactVerificationError(f"traversal archive entry path: {name!r}")


def _verify_release_content(name: str) -> None:
    lowered = name.lower()
    if lowered.endswith((".java", ".kt", ".scala")) or lowered.startswith(("src/", "sources/")):
        raise ArtifactVerificationError(f"source content is not allowed in a release JAR: {name}")
    if "gametest" in lowered or lowered.startswith(("dev/", "debug/")):
        raise ArtifactVerificationError(f"development content is not allowed in a release JAR: {name}")
    if name.endswith(".class") and not name.startswith(PACKAGE_PREFIX):
        raise ArtifactVerificationError(
            f"compiled class is outside the allowed package prefix {PACKAGE_PREFIX}: {name}"
        )


def _verify_loader_metadata(names: set[str], expected_loader: str) -> None:
    required = FABRIC_METADATA if expected_loader == "fabric" else NEOFORGE_METADATA
    if required not in names:
        raise ArtifactVerificationError(f"missing {expected_loader} metadata: {required}")
    unexpected = sorted(names.intersection(OTHER_LOADER_METADATA[expected_loader]))
    if unexpected:
        raise ArtifactVerificationError(
            f"release JAR contains metadata for another loader: {', '.join(unexpected)}"
        )


def _verify_metadata(
        archive: zipfile.ZipFile,
        expected_loader: str,
        expected_version: str,
        expected_minecraft: str,
) -> None:
    if expected_loader == "fabric":
        _verify_fabric_metadata(archive, expected_version, expected_minecraft)
    else:
        _verify_neoforge_metadata(archive, expected_version, expected_minecraft)


def _verify_fabric_metadata(
        archive: zipfile.ZipFile,
        expected_version: str,
        expected_minecraft: str,
) -> None:
    try:
        metadata = json.loads(archive.read(FABRIC_METADATA))
    except (KeyError, UnicodeDecodeError, json.JSONDecodeError) as error:
        raise ArtifactVerificationError("Fabric metadata is not valid JSON") from error
    _verify_common_metadata(metadata, expected_version, expected_minecraft, "Fabric")


def _verify_neoforge_metadata(
        archive: zipfile.ZipFile,
        expected_version: str,
        expected_minecraft: str,
) -> None:
    try:
        metadata = _parse_neoforge_metadata(archive.read(NEOFORGE_METADATA).decode("utf-8"))
    except (KeyError, UnicodeDecodeError, ValueError) as error:
        raise ArtifactVerificationError("NeoForge metadata is not valid TOML") from error
    if metadata.get("modLoader") != "javafml":
        raise ArtifactVerificationError("NeoForge metadata does not declare the Java FML loader")
    mods = metadata.get("mods")
    if not isinstance(mods, list) or len(mods) != 1 or not isinstance(mods[0], dict):
        raise ArtifactVerificationError("NeoForge metadata must declare exactly one mod")
    _verify_common_metadata(mods[0], expected_version, None, "NeoForge")
    dependencies = metadata.get("dependencies")
    if not isinstance(dependencies, dict):
        raise ArtifactVerificationError("NeoForge metadata has no dependency table")
    entries = dependencies.get(MOD_ID)
    if not isinstance(entries, list):
        raise ArtifactVerificationError("NeoForge metadata has no Please Stop dependency list")
    minecraft_ranges = [entry.get("versionRange") for entry in entries if entry.get("modId") == "minecraft"]
    if minecraft_ranges != [expected_minecraft]:
        raise ArtifactVerificationError(
            f"NeoForge Minecraft dependency mismatch: expected {expected_minecraft!r}, got {minecraft_ranges!r}"
        )


def _verify_common_metadata(
        metadata: dict[str, object],
        expected_version: str,
        expected_minecraft: str | None,
        loader_name: str,
) -> None:
    if metadata.get("id", metadata.get("modId")) != MOD_ID:
        raise ArtifactVerificationError(f"{loader_name} metadata has the wrong mod id")
    if metadata.get("version") != expected_version:
        raise ArtifactVerificationError(
            f"{loader_name} version mismatch: expected {expected_version!r}, got {metadata.get('version')!r}"
        )
    if not _has_only_safe_author(metadata.get("authors")):
        raise ArtifactVerificationError(f"{loader_name} metadata must name only the approved release author")
    if expected_minecraft is not None:
        depends = metadata.get("depends")
        actual = depends.get("minecraft") if isinstance(depends, dict) else None
        if actual != expected_minecraft:
            raise ArtifactVerificationError(
                f"{loader_name} Minecraft dependency mismatch: expected {expected_minecraft!r}, got {actual!r}"
            )


def _has_only_safe_author(authors: object) -> bool:
    if isinstance(authors, str):
        return authors == SAFE_AUTHOR
    if isinstance(authors, list):
        return authors == [SAFE_AUTHOR]
    return False


def _parse_neoforge_metadata(contents: str) -> dict[str, object]:
    """Parse the release-relevant TOML subset used by NeoForge metadata.

    Python 3.9 is a supported local verifier runtime but does not include ``tomllib``.
    This is intentionally not a general TOML implementation: it reads the simple
    quoted fields and array tables emitted by the NeoForge resource processor.
    """
    metadata: dict[str, object] = {"mods": [], "dependencies": {}}
    current: dict[str, object] = metadata
    in_multiline_value = False
    for raw_line in contents.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        if in_multiline_value:
            if line.endswith("'''") or line.endswith('\"\"\"'):
                in_multiline_value = False
            continue
        if "='''" in line or '=\"\"\"' in line:
            in_multiline_value = True
            continue
        table = TOML_ARRAY_TABLE.match(line)
        if table:
            path = table.group(1).split(".")
            if path == ["mods"]:
                current = {}
                metadata["mods"].append(current)
                continue
            if len(path) == 2 and path[0] == "dependencies":
                dependencies = metadata["dependencies"]
                assert isinstance(dependencies, dict)
                entries = dependencies.setdefault(path[1], [])
                assert isinstance(entries, list)
                current = {}
                entries.append(current)
                continue
            current = {}
            continue
        field = TOML_STRING_VALUE.match(line)
        if field:
            current[field.group(1)] = field.group(3)
            continue
        raise ValueError(f"unsupported NeoForge TOML syntax: {line!r}")
    if in_multiline_value:
        raise ValueError("unterminated multiline TOML value")
    return metadata


def main(argv: list[str] | None = None) -> int:
    args = parse_args(argv)
    try:
        verify_release_artifact(args.jar, args.loader, args.version, args.minecraft)
    except ArtifactVerificationError as error:
        print(f"release artifact verification failed: {error}", file=sys.stderr)
        return 1
    print(f"release artifact verified: {args.jar}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
