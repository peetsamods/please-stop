package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GroundFlightRetentionTest {
    private static CreativeFlightAssist.GroundFlightRetention state(CreativeFlightAssistMode mode,
            boolean eligible, boolean grounded, boolean flying, boolean active, boolean manuallyOff) {
        return new CreativeFlightAssist.GroundFlightRetention(mode, eligible, grounded, flying, active, manuallyOff);
    }
    @Test void persistentAssistRetainsOnlyFlightAlreadyOwnedByActivation() {
        assertTrue(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, true, false)));
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, false, false)));
    }
    @Test void alwaysModeRetainsExistingFlightWithoutWaitingForActivation() {
        assertTrue(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, true, true, true, false, false)));
    }
    @Test void explicitOffAndVanillaModeKeepVanillaCancellation() {
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, true, true)));
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, true, true, true, true, true)));
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.VANILLA, true, true, true, true, false)));
    }
    @Test void airborneOrNotFlyingPlayersAreNeverKeptInGroundFlight() {
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, false, true, true, false)));
        assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, true, true, false, true, false)));
    }
    @Test void disabledOrExcludedPlayersKeepVanillaAndUninitializedBridgeIsSafe() {
        for (CreativeFlightAssistMode mode : CreativeFlightAssistMode.values()) {
            assertFalse(CreativeFlightAssist.shouldPreserveGroundFlight(state(mode, false, true, true, true, false)));
        }
        assertFalse(PleaseStopClient.shouldPreserveGroundFlight(null, false));
    }
}
