package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.level.GameType;
import org.junit.jupiter.api.Test;

final class PleaseStopClientTest {
    @Test
    void singleplayerSurvivalSaveBlocksUserFacingControlsEvenIfClientReportsCreative() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                true,
                GameType.CREATIVE,
                GameType.SURVIVAL
        ));
    }

    @Test
    void playerMustActuallyBeCreativeForUserFacingControls() {
        assertFalse(PleaseStopClient.allowsUserFacingControls(
                true,
                false,
                true,
                GameType.CREATIVE,
                GameType.CREATIVE
        ));
    }

    @Test
    void singleplayerCreativeSaveAllowsUserFacingControlsWhenPlayerAndClientAreCreative() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                true,
                GameType.CREATIVE,
                GameType.CREATIVE
        ));
    }

    @Test
    void multiplayerCreativePlayerAllowsUserFacingControlsWithoutSingleplayerSaveMode() {
        assertTrue(PleaseStopClient.allowsUserFacingControls(
                true,
                true,
                false,
                GameType.CREATIVE,
                null
        ));
    }
}
