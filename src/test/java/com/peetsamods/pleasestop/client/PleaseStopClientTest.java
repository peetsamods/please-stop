package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.GameMode;
import org.junit.jupiter.api.Test;

final class PleaseStopClientTest {
    @Test
    void currentCreativePlayerAndClientModeAllowControls() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                GameMode.CREATIVE
        ));
    }

    @Test
    void playerMustActuallyBeCreativeForUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                false,
                GameMode.CREATIVE
        ));
    }

    @Test
    void nonCreativeClientModeBlocksUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                GameMode.SURVIVAL
        ));
    }

    @Test
    void missingPlayerBlocksUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                false,
                true,
                GameMode.CREATIVE
        ));
    }
}
