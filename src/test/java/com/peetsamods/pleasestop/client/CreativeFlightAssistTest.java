package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import org.junit.jupiter.api.Test;

final class CreativeFlightAssistTest {
    @Test
    void persistentModeRestoresFlightAtGroundLevelAfterActivation() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                true,
                true,
                false,
                true,
                true,
                false
        );

        assertEquals(CreativeFlightAssist.Action.REACTIVATE, result.action());
        assertTrue(result.assistActive());
        assertFalse(result.manuallyDisabled());
    }

    @Test
    void persistentModeDoesNotForceFlightBackOnInMidAir() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                true,
                false,
                false,
                true,
                true,
                false
        );

        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertFalse(result.assistActive());
    }

    @Test
    void alwaysOnModeStartsCreativeFlightWithoutDoubleSpace() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.VANILLA,
                true,
                false,
                false,
                false,
                false,
                false
        );

        assertEquals(CreativeFlightAssist.Action.REACTIVATE, result.action());
        assertTrue(result.assistActive());
    }

    @Test
    void changingToAlwaysOnClearsEarlierManualSuppression() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                true,
                true,
                false,
                false,
                false,
                true
        );

        assertEquals(CreativeFlightAssist.Action.REACTIVATE, result.action());
        assertFalse(result.manuallyDisabled());
    }

    @Test
    void vanillaDoubleSpaceReactivationPreservesManualSuppression() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                true,
                false,
                true,
                false,
                false,
                true
        );

        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertFalse(result.assistActive());
        assertTrue(result.manuallyDisabled());
    }

    @Test
    void vanillaModeClearsSessionAssistanceWithoutChangingFlight() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.VANILLA,
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                true,
                true,
                false,
                true,
                true,
                false
        );

        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertFalse(result.assistActive());
        assertFalse(result.manuallyDisabled());
    }

    @Test
    void manualFlightAssistOffIsRespectedUntilExplicitlyReenabled() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                true,
                true,
                false,
                false,
                false,
                true
        );

        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertTrue(result.manuallyDisabled());
    }

    @Test
    void disablingPleaseStopResetsFlightAssistSession() {
        CreativeFlightAssist.Transition result = transition(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE,
                false,
                true,
                false,
                true,
                true,
                false
        );

        assertEquals(CreativeFlightAssist.Action.NONE, result.action());
        assertFalse(result.assistActive());
    }

    @Test
    void eligibilityRequiresMasterToggleAndAllCreativeSafetyGates() {
        assertTrue(CreativeFlightAssist.isEligible(eligibility()));
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

    private static CreativeFlightAssist.Transition transition(
            CreativeFlightAssistMode mode,
            CreativeFlightAssistMode previousMode,
            boolean eligible,
            boolean onGround,
            boolean flying,
            boolean wasFlyingLastTick,
            boolean assistActive,
            boolean manuallyDisabled
    ) {
        return CreativeFlightAssist.transition(new CreativeFlightAssist.State(
                mode,
                previousMode,
                eligible,
                onGround,
                flying,
                wasFlyingLastTick,
                assistActive,
                manuallyDisabled
        ));
    }

    private static CreativeFlightAssist.Eligibility eligibility() {
        return eligibility(true, true, true, false, false, false, false, false, false);
    }

    private static CreativeFlightAssist.Eligibility eligibility(
            boolean pleaseStopEnabled,
            boolean creative,
            boolean allowFlying,
            boolean spectator,
            boolean gliding,
            boolean swimming,
            boolean inVehicle,
            boolean recentlyHurt,
            boolean flyingLocked
    ) {
        return new CreativeFlightAssist.Eligibility(
                pleaseStopEnabled,
                creative,
                allowFlying,
                spectator,
                gliding,
                swimming,
                inVehicle,
                recentlyHurt,
                flyingLocked
        );
    }
}
