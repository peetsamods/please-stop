package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.level.GameType;
import org.junit.jupiter.api.Test;

final class LaunchToastGateTest {
    @Test
    void showsOnceOnlyAfterReminderEligibilityIsStableThenResetsAfterLeavingWorld() {
        LaunchToastGate gate = new LaunchToastGate();

        assertFalse(gate.tick(false, false));
        assertFalse(gate.tick(true, false));
        assertTrue(tickUntilShown(gate));
        assertFalse(gate.tick(true, true));
        assertFalse(gate.tick(false, false));
        assertTrue(tickUntilShown(gate));
    }

    @Test
    void survivalDoesNotSpendReminderBeforeCreativeBecomesAvailable() {
        LaunchToastGate gate = new LaunchToastGate();

        assertFalse(gate.tick(true, PleaseStopClient.isCreativeGameMode(null)));
        assertFalse(gate.tick(true, PleaseStopClient.isCreativeGameMode(GameType.SURVIVAL)));
        assertTrue(tickUntilShown(gate));
    }

    @Test
    void transientCreativeEligibilityDoesNotShowDuringSurvivalLoad() {
        LaunchToastGate gate = new LaunchToastGate();

        assertFalse(gate.tick(true, true));
        assertFalse(gate.tick(true, false));
        assertTrue(tickUntilShown(gate));
    }

    private static boolean tickUntilShown(LaunchToastGate gate) {
        boolean shown = false;
        for (int tick = 0; tick < 20; tick++) {
            shown = gate.tick(true, true);
            if (tick < 19) {
                assertFalse(shown);
            }
        }
        return shown;
    }
}
