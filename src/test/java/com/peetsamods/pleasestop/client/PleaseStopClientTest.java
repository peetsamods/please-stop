package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.level.GameType;
import org.junit.jupiter.api.Test;

final class PleaseStopClientTest {
    @Test
    void currentCreativePlayerAndClientModeAllowControlsInSurvivalCreatedSingleplayerWorld() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                GameType.CREATIVE
        ));
    }

    @Test
    void playerMustActuallyBeCreativeForUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                false,
                GameType.CREATIVE
        ));
    }

    @Test
    void nonCreativeClientModeBlocksUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                GameType.SURVIVAL
        ));
    }

    @Test
    void missingPlayerBlocksUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                false,
                true,
                GameType.CREATIVE
        ));
    }
}
