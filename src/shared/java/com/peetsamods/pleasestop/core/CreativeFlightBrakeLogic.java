package com.peetsamods.pleasestop.core;

/** Loader-neutral decision rules for the creative-flight inertia brake. */
public final class CreativeFlightBrakeLogic {
    public record Input(
            boolean forward,
            boolean backward,
            boolean left,
            boolean right,
            boolean jump,
            boolean sneak
    ) {
        public static final Input NONE = new Input(false, false, false, false, false, false);
    }

    public record State(
            boolean enabled,
            boolean creative,
            boolean flying,
            boolean onGround,
            boolean spectator,
            boolean gliding,
            boolean swimming,
            boolean inVehicle,
            boolean recentlyHurt,
            Input input,
            boolean velocityZero,
            boolean hadActiveFlightInputLastTick,
            boolean justEnabled
    ) {
    }

    public enum Action {
        NONE,
        ACTIVE_FLIGHT_INPUT_OBSERVED,
        VANILLA_DRIFT_OBSERVED,
        BRAKE
    }

    private CreativeFlightBrakeLogic() {
    }

    public static Action action(State state) {
        if (!state.creative()
                || !state.flying()
                || state.spectator()
                || state.gliding()
                || state.swimming()
                || state.inVehicle()
                || state.recentlyHurt()
                || state.velocityZero()) {
            return Action.NONE;
        }

        if (hasActiveFlightInput(state.input(), state.onGround())) {
            return state.enabled() ? Action.ACTIVE_FLIGHT_INPUT_OBSERVED : Action.NONE;
        }

        if (!state.hadActiveFlightInputLastTick() && !state.justEnabled()) {
            return Action.NONE;
        }

        return state.enabled() ? Action.BRAKE : Action.VANILLA_DRIFT_OBSERVED;
    }

    public static boolean hasActiveFlightInput(Input input, boolean onGround) {
        Input actual = input == null ? Input.NONE : input;
        boolean verticalInput = actual.jump() != actual.sneak();
        if (onGround && actual.sneak() && !actual.jump()) {
            verticalInput = false;
        }
        return actual.forward()
                || actual.backward()
                || actual.left()
                || actual.right()
                || verticalInput;
    }
}
