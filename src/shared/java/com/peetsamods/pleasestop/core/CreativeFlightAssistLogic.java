package com.peetsamods.pleasestop.core;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;

/** Loader-neutral Creative Flight Assist state machine. */
public final class CreativeFlightAssistLogic {
    public record Eligibility(
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
    }

    public record State(
            CreativeFlightAssistMode mode,
            CreativeFlightAssistMode previousMode,
            boolean eligible,
            boolean onGround,
            boolean flying,
            boolean wasFlyingLastTick,
            boolean assistActive,
            boolean manuallyDisabled
    ) {
    }

    public record Transition(Action action, boolean assistActive, boolean manuallyDisabled) {
    }

    public record GroundFlightRetention(
            CreativeFlightAssistMode mode,
            boolean eligible,
            boolean onGround,
            boolean flying,
            boolean assistActive,
            boolean manuallyDisabled
    ) {
    }

    public enum Action {
        NONE,
        ACTIVATE,
        DEACTIVATE,
        REACTIVATE
    }

    private CreativeFlightAssistLogic() {
    }

    public static Transition transition(State state) {
        if (!state.eligible() || state.mode() == CreativeFlightAssistMode.VANILLA) {
            return new Transition(Action.NONE, false, false);
        }

        boolean active = state.assistActive();
        boolean suppressed = state.manuallyDisabled();

        if (state.mode() != state.previousMode()) {
            active = false;
            suppressed = false;
        }

        if (state.flying() && !suppressed) {
            // Vanilla flight entry must not undo an explicit Flight Assist OFF choice.
            active = true;
        }

        if (state.wasFlyingLastTick() && !state.flying() && !state.onGround()) {
            active = false;
        }

        if (!suppressed
                && state.mode() == CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE
                && !state.flying()) {
            return new Transition(Action.REACTIVATE, true, false);
        }

        if (!suppressed
                && state.mode() == CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION
                && active
                && state.onGround()
                && !state.flying()) {
            return new Transition(Action.REACTIVATE, true, false);
        }

        return new Transition(Action.NONE, active, suppressed);
    }

    public static boolean isEligible(Eligibility eligibility) {
        return eligibility.pleaseStopEnabled()
                && eligibility.creative()
                && eligibility.allowFlying()
                && !eligibility.spectator()
                && !eligibility.gliding()
                && !eligibility.swimming()
                && !eligibility.inVehicle()
                && !eligibility.recentlyHurt()
                && !eligibility.flyingLocked();
    }

    public static boolean shouldPreserveGroundFlight(GroundFlightRetention state) {
        if (!state.eligible()
                || !state.onGround()
                || !state.flying()
                || state.manuallyDisabled()
                || state.mode() == CreativeFlightAssistMode.VANILLA) {
            return false;
        }
        return state.mode() == CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE
                || state.assistActive();
    }
}
