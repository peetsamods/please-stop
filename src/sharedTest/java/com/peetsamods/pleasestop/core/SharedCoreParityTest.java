package com.peetsamods.pleasestop.core;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SharedCoreParityTest {
    @Test
    void flightAssistRequiresEverySafetyGate() {
        CreativeFlightAssistLogic.Eligibility eligible = eligibility();
        assertTrue(CreativeFlightAssistLogic.isEligible(eligible));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, true, false, false, false, false, false)));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, true, false, false, false, false)));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, false, true, false, false, false)));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, false, false, true, false, false)));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, false, false, false, true, false)));
        assertFalse(CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, false, false, false, false, true)));
    }

    @Test
    void persistentAssistRestoresOnlyAnActivatedGroundedFlight() {
        CreativeFlightAssistLogic.Transition transition = CreativeFlightAssistLogic.transition(
                new CreativeFlightAssistLogic.State(
                        CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                        CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                        true,
                        true,
                        false,
                        true,
                        true,
                        false
                )
        );
        assertEquals(CreativeFlightAssistLogic.Action.REACTIVATE, transition.action());
        assertTrue(transition.assistActive());
    }

    @Test
    void manualFlightAssistOffSurvivesLaterVanillaFlightActivation() {
        CreativeFlightAssistLogic.Transition transition = CreativeFlightAssistLogic.transition(
                new CreativeFlightAssistLogic.State(
                        CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                        CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                        true,
                        false,
                        true,
                        false,
                        false,
                        true
                )
        );

        assertEquals(CreativeFlightAssistLogic.Action.NONE, transition.action());
        assertFalse(transition.assistActive());
        assertTrue(transition.manuallyDisabled());
    }

    @Test
    void groundFlightRetentionRequiresAnOwnedEligibleFlightState() {
        assertTrue(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, true, false)));
        assertTrue(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE, true, true, true, false, false)));

        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.VANILLA, true, true, true, true, false)));
        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, false, false)));
        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, true, true, true)));
        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, false, true, true, true, false)));
        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, false, true, true, false)));
        assertFalse(CreativeFlightAssistLogic.shouldPreserveGroundFlight(retention(
                CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION, true, true, false, true, false)));
    }

    @Test
    void brakePreservesActiveInputAndExcludedStates() {
        CreativeFlightBrakeLogic.State active = brakeState(
                new CreativeFlightBrakeLogic.Input(true, false, false, false, false, false), false, false);
        assertEquals(CreativeFlightBrakeLogic.Action.ACTIVE_FLIGHT_INPUT_OBSERVED,
                CreativeFlightBrakeLogic.action(active));

        CreativeFlightBrakeLogic.State spectator = new CreativeFlightBrakeLogic.State(
                true, true, true, false, true, false, false, false, false,
                CreativeFlightBrakeLogic.Input.NONE, false, true, false);
        assertEquals(CreativeFlightBrakeLogic.Action.NONE, CreativeFlightBrakeLogic.action(spectator));
    }

    @Test
    void brakeTreatsGroundSneakAndJumpSneakAsVerticalNeutral() {
        CreativeFlightBrakeLogic.Input groundSneak = new CreativeFlightBrakeLogic.Input(
                false, false, false, false, false, true);
        assertFalse(CreativeFlightBrakeLogic.hasActiveFlightInput(groundSneak, true));
        assertTrue(CreativeFlightBrakeLogic.hasActiveFlightInput(groundSneak, false));

        CreativeFlightBrakeLogic.Input jumpSneak = new CreativeFlightBrakeLogic.Input(
                false, false, false, false, true, true);
        assertFalse(CreativeFlightBrakeLogic.hasActiveFlightInput(jumpSneak, false));
        assertEquals(CreativeFlightBrakeLogic.Action.BRAKE,
                CreativeFlightBrakeLogic.action(brakeState(jumpSneak, false, false)));
    }

    @Test
    void disabledPathObservesVanillaDriftInsteadOfBraking() {
        assertEquals(CreativeFlightBrakeLogic.Action.VANILLA_DRIFT_OBSERVED,
                CreativeFlightBrakeLogic.action(brakeState(CreativeFlightBrakeLogic.Input.NONE, true, false)));
    }

    private static CreativeFlightAssistLogic.Eligibility eligibility() {
        return new CreativeFlightAssistLogic.Eligibility(
                true, true, true, false, false, false, false, false, false);
    }

    private static CreativeFlightAssistLogic.GroundFlightRetention retention(
            CreativeFlightAssistMode mode,
            boolean eligible,
            boolean onGround,
            boolean flying,
            boolean assistActive,
            boolean manuallyDisabled
    ) {
        return new CreativeFlightAssistLogic.GroundFlightRetention(
                mode, eligible, onGround, flying, assistActive, manuallyDisabled);
    }

    private static CreativeFlightBrakeLogic.State brakeState(
            CreativeFlightBrakeLogic.Input input,
            boolean disabled,
            boolean onGround
    ) {
        return new CreativeFlightBrakeLogic.State(
                !disabled, true, true, onGround, false, false, false, false, false,
                input, false, true, false);
    }
}
