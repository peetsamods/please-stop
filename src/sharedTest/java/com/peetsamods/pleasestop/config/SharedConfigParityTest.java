package com.peetsamods.pleasestop.config;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SharedConfigParityTest {
    @TempDir
    Path tempDir;

    @Test
    void defaultsAndRoundTripMatchBeta3() throws Exception {
        Path path = tempDir.resolve(PleaseStopConfig.FILE_NAME);
        PleaseStopConfig config = PleaseStopConfig.loadOrCreate(path);
        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
        assertEquals(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, config.creativeFlightAssistMode());

        config.setEnabled(true);
        config.setShowToasts(false);
        config.setCreativeFlightAssistMode(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE);
        config.save(path);

        PleaseStopConfig reloaded = PleaseStopConfig.load(path);
        assertTrue(reloaded.isEnabled());
        assertFalse(reloaded.showToasts());
        assertEquals(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, reloaded.creativeFlightAssistMode());
        assertTrue(Files.readString(path).contains("creativeFlightAssistMode"));
    }

    @Test
    void invalidConfigFallsBackSafely() throws Exception {
        Path path = tempDir.resolve(PleaseStopConfig.FILE_NAME);
        Files.writeString(path, "not json");
        PleaseStopConfig config = PleaseStopConfig.load(path);
        assertFalse(config.isEnabled());
        assertTrue(config.showToasts());
        assertEquals(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, config.creativeFlightAssistMode());
    }
}
