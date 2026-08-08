package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import org.junit.jupiter.api.Test;

final class CreativeFlightAssistTest {
    @Test
    void persistentModeRestoresFlightAtGroundLevelAfterActivation() {
        CreativeFlightAssist.Transition result = transition(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, false, true, true, false);
        assertEquals(CreativeFlightAssist.Action.REACTIVATE, result.action());
        assertTrue(result.assistActive());
    }

    @Test
    void persistentModeDoesNotForceFlightBackOnInMidAir() {
        CreativeFlightAssist.Transition result = transition(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, false, false, true, true, false);
        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertFalse(result.assistActive());
    }

    @Test
    void alwaysOnModeStartsCreativeFlightWithoutDoubleSpace() {
        CreativeFlightAssist.Transition result = transition(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.VANILLA, true, false, false, false, false, false);
        assertEquals(CreativeFlightAssist.Action.REACTIVATE, result.action());
    }

    @Test
    void changingModeClearsButFreshVanillaActivationPreservesManualSuppression() {
        CreativeFlightAssist.Transition modeChange = transition(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, false, false, false, true);
        CreativeFlightAssist.Transition freshFlight = transition(CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, false, true, false, false, true);
        assertEquals(CreativeFlightAssist.Action.REACTIVATE, modeChange.action());
        assertFalse(modeChange.manuallyDisabled());
        assertFalse(freshFlight.assistActive());
        assertTrue(freshFlight.manuallyDisabled());
    }

    @Test
    void vanillaOrDisabledMasterClearsSessionAssistance() {
        CreativeFlightAssist.Transition vanilla = transition(CreativeFlightAssistMode.VANILLA,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, false, true, true, false);
        CreativeFlightAssist.Transition disabled = transition(CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, false, true, false, true, true, false);
        assertFalse(vanilla.assistActive());
        assertFalse(disabled.assistActive());
    }

    @Test
    void eligibilityRequiresMasterToggleAndEveryCreativeSafetyGate() {
        assertTrue(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, false, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(false, true, true, false, false, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, false, true, false, false, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, false, false, false, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, true, false, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, true, false, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, false, true, false, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, false, false, true, false, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, false, false, false, true, false)));
        assertFalse(CreativeFlightAssist.isEligible(eligibility(true, true, true, false, false, false, false, false, true)));
    }

    private static CreativeFlightAssist.Transition transition(CreativeFlightAssistMode mode,
            CreativeFlightAssistMode previousMode, boolean eligible, boolean onGround, boolean flying,
            boolean wasFlyingLastTick, boolean assistActive, boolean manuallyDisabled) {
        return CreativeFlightAssist.transition(new CreativeFlightAssist.State(mode, previousMode, eligible,
                onGround, flying, wasFlyingLastTick, assistActive, manuallyDisabled));
    }

    private static CreativeFlightAssist.Eligibility eligibility(boolean enabled, boolean creative,
            boolean allowFlying, boolean spectator, boolean gliding, boolean swimming, boolean vehicle,
            boolean hurt, boolean locked) {
        return new CreativeFlightAssist.Eligibility(enabled, creative, allowFlying, spectator, gliding,
                swimming, vehicle, hurt, locked);
    }
}
