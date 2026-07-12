package com.peetsamods.pleasestop.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class PleaseStopConfigTest {
    @TempDir
    private Path tempDir;

    @Test
    void missingConfigDefaultsToDisabled() {
        Path configPath = tempDir.resolve("please_stop.json");

        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
        assertEquals(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, config.creativeFlightAssistMode());
    }

    @Test
    void loadOrCreateWritesMissingDefaultConfig() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");

        PleaseStopConfig config = PleaseStopConfig.loadOrCreate(configPath);

        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
        assertTrue(Files.readString(configPath).contains("\"enabled\": false"));
        assertTrue(Files.readString(configPath).contains("\"showToasts\": true"));
        assertTrue(Files.readString(configPath).contains("\"creativeFlightAssistMode\": \"PERSISTENT_AFTER_ACTIVATION\""));
    }

    @Test
    void loadsEnabledValueFromJson() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");
        Files.writeString(configPath, "{ \"enabled\": true, \"showToasts\": false }");

        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertTrue(config.isEnabled());
        assertFalse(config.showToasts());
    }

    @Test
    void invalidConfigFallsBackToDisabled() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");
        Files.writeString(configPath, "{ nope");

        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
    }

    @Test
    void nonBooleanEnabledFallsBackToDisabled() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");
        Files.writeString(configPath, "{ \"enabled\": \"true\" }");

        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
    }

    @Test
    void nonBooleanShowToastsFallsBackToEnabled() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");
        Files.writeString(configPath, "{ \"enabled\": true, \"showToasts\": \"false\" }");

        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertTrue(config.isEnabled());
        assertTrue(config.showToasts());
    }

    @Test
    void toggleCanBeSavedAndReloaded() throws Exception {
        Path configPath = tempDir.resolve("nested").resolve("please_stop.json");
        PleaseStopConfig config = PleaseStopConfig.load(configPath);

        assertTrue(config.toggle());
        assertFalse(config.toggleToasts());
        config.save(configPath);

        assertTrue(PleaseStopConfig.load(configPath).isEnabled());
        assertFalse(PleaseStopConfig.load(configPath).showToasts());
    }

    @Test
    void loadsAndSafelyDefaultsCreativeFlightAssistMode() throws Exception {
        Path configPath = tempDir.resolve("please_stop.json");
        Files.writeString(configPath, "{ \"creativeFlightAssistMode\": \"ALWAYS_ON_IN_CREATIVE\" }");
        assertEquals(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                PleaseStopConfig.load(configPath).creativeFlightAssistMode());

        Files.writeString(configPath, "{ \"creativeFlightAssistMode\": \"invalid\" }");
        assertEquals(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                PleaseStopConfig.load(configPath).creativeFlightAssistMode());
    }
}
