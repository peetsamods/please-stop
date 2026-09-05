from __future__ import annotations

import contextlib
import io
import json
import tempfile
import unittest
import warnings
import zipfile
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).parent))
from verify_release_artifact import ArtifactVerificationError, main, verify_release_artifact


VERSION = "0.1.0-beta.5+test"
FABRIC_MINECRAFT = "1.21.11"
NEOFORGE_MINECRAFT = "[1.21.1],[1.21.11]"


class VerifyReleaseArtifactTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.directory = Path(self.temporary_directory.name)

    def tearDown(self) -> None:
        self.temporary_directory.cleanup()

    def test_accepts_valid_fabric_jar(self) -> None:
        jar = self._write_jar("valid-fabric.jar", {
            "fabric.mod.json": json.dumps({
                "id": "please_stop",
                "version": VERSION,
                "authors": ["Peetsa"],
                "depends": {"minecraft": FABRIC_MINECRAFT},
            }),
            "com/peetsamods/pleasestop/client/PleaseStopClient.class": b"class bytes",
            "assets/please_stop/lang/en_us.json": b"{}",
        })

        verify_release_artifact(jar, "fabric", VERSION, FABRIC_MINECRAFT)

    def test_accepts_normal_jar_directory_entries(self) -> None:
        jar = self._write_jar("directory-entries.jar", {
            "META-INF/": b"",
            "com/": b"",
            "com/peetsamods/": b"",
            "com/peetsamods/pleasestop/": b"",
            "fabric.mod.json": _fabric_metadata(),
        })

        verify_release_artifact(jar, "fabric", VERSION, FABRIC_MINECRAFT)

    def test_accepts_valid_neoforge_jar(self) -> None:
        jar = self._write_jar("valid-neoforge.jar", {
            "META-INF/neoforge.mods.toml": _neoforge_metadata(),
            "com/peetsamods/pleasestop/neoforge/PleaseStopNeoForge.class": b"class bytes",
        })

        verify_release_artifact(jar, "neoforge", VERSION, NEOFORGE_MINECRAFT)

    def test_rejects_wrong_loader_metadata(self) -> None:
        jar = self._write_jar("wrong-loader.jar", {
            "fabric.mod.json": _fabric_metadata(),
            "META-INF/neoforge.mods.toml": _neoforge_metadata(),
        })

        self._assert_rejected(jar, "fabric", "another loader")

    def test_rejects_missing_expected_loader_metadata(self) -> None:
        jar = self._write_jar("missing-loader.jar", {"fabric.mod.json": _fabric_metadata()})

        self._assert_rejected(jar, "neoforge", "missing neoforge metadata", minecraft=NEOFORGE_MINECRAFT)

    def test_rejects_duplicate_metadata_that_could_forge_a_green_result(self) -> None:
        jar = self.directory / "duplicate.jar"
        with warnings.catch_warnings():
            warnings.simplefilter("ignore", UserWarning)
            with zipfile.ZipFile(jar, "w") as archive:
                archive.writestr("fabric.mod.json", _fabric_metadata())
                archive.writestr("fabric.mod.json", _fabric_metadata())

        self._assert_rejected(jar, "fabric", "duplicate archive entry")

    def test_rejects_sources_jar_by_name(self) -> None:
        jar = self._write_jar("please-stop-sources.jar", {"fabric.mod.json": _fabric_metadata()})

        self._assert_rejected(jar, "fabric", "sources JARs")

    def test_rejects_source_and_development_content(self) -> None:
        for name, expected in (
                ("com/peetsamods/pleasestop/Proof.java", "source content"),
                ("com/peetsamods/pleasestop/client/ClientGameTest.class", "development content"),
        ):
            with self.subTest(name=name):
                jar = self._write_jar("content.jar", {"fabric.mod.json": _fabric_metadata(), name: b"x"})
                self._assert_rejected(jar, "fabric", expected)

    def test_rejects_non_allowlisted_compiled_package(self) -> None:
        jar = self._write_jar("package.jar", {
            "fabric.mod.json": _fabric_metadata(),
            "unapproved/package/Thing.class": b"x",
        })

        self._assert_rejected(jar, "fabric", "allowed package prefix")

    def test_rejects_absolute_and_traversal_paths(self) -> None:
        for name, expected in (("/absolute.class", "absolute archive entry path"), ("a/../b.class", "traversal archive entry path")):
            with self.subTest(name=name):
                jar = self._write_jar("path.jar", {"fabric.mod.json": _fabric_metadata(), name: b"x"})
                self._assert_rejected(jar, "fabric", expected)

    def test_rejects_bad_author_version_and_minecraft_expectations(self) -> None:
        invalid_author = json.dumps({
            "id": "please_stop",
            "version": VERSION,
            "authors": ["Someone Else"],
            "depends": {"minecraft": FABRIC_MINECRAFT},
        })
        jar = self._write_jar("metadata.jar", {"fabric.mod.json": invalid_author})
        self._assert_rejected(jar, "fabric", "approved release author")

        valid = self._write_jar("valid.jar", {"fabric.mod.json": _fabric_metadata()})
        self._assert_rejected(valid, "fabric", "version mismatch", version="different")
        self._assert_rejected(valid, "fabric", "Minecraft dependency mismatch", minecraft="different")

    def test_rejects_neoforge_bad_author_and_minecraft_range(self) -> None:
        bad_author = self._write_jar("neoforge-author.jar", {
            "META-INF/neoforge.mods.toml": _neoforge_metadata().replace('authors="Peetsa"', 'authors="Other"'),
        })
        self._assert_rejected(bad_author, "neoforge", "approved release author", minecraft=NEOFORGE_MINECRAFT)

        bad_range = self._write_jar("neoforge-range.jar", {
            "META-INF/neoforge.mods.toml": _neoforge_metadata().replace(
                f'versionRange="{NEOFORGE_MINECRAFT}"',
                'versionRange="[1.21.1,1.22)"',
            ),
        })
        self._assert_rejected(bad_range, "neoforge", "Minecraft dependency mismatch", minecraft=NEOFORGE_MINECRAFT)

    def test_cli_returns_nonzero_for_a_rejection(self) -> None:
        jar = self._write_jar("invalid.jar", {"fabric.mod.json": _fabric_metadata(), "debug/Trace.class": b"x"})
        stderr = io.StringIO()

        with contextlib.redirect_stderr(stderr):
            result = main(["--jar", str(jar), "--loader", "fabric", "--version", VERSION, "--minecraft", FABRIC_MINECRAFT])

        self.assertEqual(1, result)
        self.assertIn("release artifact verification failed", stderr.getvalue())

    def _assert_rejected(
            self,
            jar: Path,
            loader: str,
            message: str,
            version: str = VERSION,
            minecraft: str = FABRIC_MINECRAFT,
    ) -> None:
        with self.assertRaisesRegex(ArtifactVerificationError, message):
            verify_release_artifact(jar, loader, version, minecraft)

    def _write_jar(self, name: str, entries: dict[str, str | bytes]) -> Path:
        jar = self.directory / name
        with zipfile.ZipFile(jar, "w") as archive:
            for entry_name, contents in entries.items():
                archive.writestr(entry_name, contents)
        return jar


def _fabric_metadata() -> str:
    return json.dumps({
        "id": "please_stop",
        "version": VERSION,
        "authors": ["Peetsa"],
        "depends": {"minecraft": FABRIC_MINECRAFT},
    })


def _neoforge_metadata() -> str:
    return f"""modLoader="javafml"

[[mods]]
modId="please_stop"
version="{VERSION}"
authors="Peetsa"
description='''
Release-safe descriptor parser fixture.
'''

[[dependencies.please_stop]]
modId="minecraft"
versionRange="{NEOFORGE_MINECRAFT}"
"""


if __name__ == "__main__":
    unittest.main()
