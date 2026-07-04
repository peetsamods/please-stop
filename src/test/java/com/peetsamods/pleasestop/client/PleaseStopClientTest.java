package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.GameMode;
import org.junit.jupiter.api.Test;

final class PleaseStopClientTest {
    @Test
    void singleplayerSurvivalSaveBlocksUserFacingControlsEvenIfClientReportsCreative() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                true,
                GameMode.CREATIVE,
                GameMode.SURVIVAL
        ));
    }

    @Test
    void playerMustActuallyBeCreativeForUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                false,
                true,
                GameMode.CREATIVE,
                GameMode.CREATIVE
        ));
    }

    @Test
    void singleplayerCreativeSaveAllowsUserFacingControlsWhenPlayerAndClientAreCreative() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                true,
                GameMode.CREATIVE,
                GameMode.CREATIVE
        ));
    }

    @Test
    void multiplayerCreativePlayerAllowsUserFacingControlsWithoutSingleplayerSaveMode() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                false,
                GameMode.CREATIVE,
                null
        ));
    }
}
