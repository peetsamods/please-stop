package com.peetsamods.pleasestop.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;

final class CreativeFlightBrake {
    record State(
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
            Vec3 currentVelocity,
            boolean hadActiveFlightInputLastTick,
            boolean justEnabled
    ) {
        State spectator(boolean value) {
            return with(value, gliding, swimming, inVehicle, recentlyHurt);
        }

        State gliding(boolean value) {
            return with(spectator, value, swimming, inVehicle, recentlyHurt);
        }

        State swimming(boolean value) {
            return with(spectator, gliding, value, inVehicle, recentlyHurt);
        }

        State inVehicle(boolean value) {
            return with(spectator, gliding, swimming, value, recentlyHurt);
        }

        State recentlyHurt(boolean value) {
            return with(spectator, gliding, swimming, inVehicle, value);
        }

        private State with(
                boolean nextSpectator,
                boolean nextGliding,
                boolean nextSwimming,
                boolean nextInVehicle,
                boolean nextRecentlyHurt
        ) {
            return new State(
                    enabled,
                    creative,
                    flying,
                    onGround,
                    nextSpectator,
                    nextGliding,
                    nextSwimming,
                    nextInVehicle,
                    nextRecentlyHurt,
                    input,
                    currentVelocity,
                    hadActiveFlightInputLastTick,
                    justEnabled
            );
        }
    }

    enum Action {
        NONE,
        ACTIVE_FLIGHT_INPUT_OBSERVED,
        VANILLA_DRIFT_OBSERVED,
        BRAKE
    }

    private CreativeFlightBrake() {
    }

    static Action apply(LocalPlayer player, boolean enabled, boolean hadActiveFlightInputLastTick, boolean justEnabled) {
        if (player == null || player.input == null) {
            return Action.NONE;
        }

        Action action = action(new State(
                enabled,
                player.isCreative(),
                player.getAbilities().flying,
                player.onGround(),
                player.isSpectator(),
                player.isFallFlying(),
                player.isSwimming(),
                player.isPassenger(),
                player.hurtTime > 0,
                player.input.keyPresses,
                player.getDeltaMovement(),
                hadActiveFlightInputLastTick,
                justEnabled
        ));

        if (action == Action.BRAKE) {
            player.setDeltaMovement(Vec3.ZERO);
        }

        return action;
    }

    static Vec3 brakedVelocity(State state) {
        if (action(state) == Action.BRAKE) {
            return Vec3.ZERO;
        }

        return state.currentVelocity();
    }

    static Vec3 brakedVelocity(
            boolean enabled,
            boolean creative,
            boolean flying,
            Input input,
            Vec3 currentVelocity,
            boolean hadActiveFlightInputLastTick,
            boolean justEnabled
    ) {
        return brakedVelocity(new State(
                enabled,
                creative,
                flying,
                false,
                false,
                false,
                false,
                false,
                false,
                input,
                currentVelocity,
                hadActiveFlightInputLastTick,
                justEnabled
        ));
    }

    static Action action(State state) {
        if (!state.creative()
                || !state.flying()
                || state.spectator()
                || state.gliding()
                || state.swimming()
                || state.inVehicle()
                || state.recentlyHurt()
                || state.currentVelocity().equals(Vec3.ZERO)) {
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

    static Action action(
            boolean enabled,
            boolean creative,
            boolean flying,
            Input input,
            Vec3 currentVelocity,
            boolean hadActiveFlightInputLastTick,
            boolean justEnabled
    ) {
        return action(new State(
                enabled,
                creative,
                flying,
                false,
                false,
                false,
                false,
                false,
                false,
                input,
                currentVelocity,
                hadActiveFlightInputLastTick,
                justEnabled
        ));
    }

    static boolean hasActiveFlightInput(Input input) {
        return hasActiveFlightInput(input, false);
    }

    static boolean hasActiveFlightInput(Input input, boolean onGround) {
        Input playerInput = input == null ? Input.EMPTY : input;
        boolean verticalInput = playerInput.jump() != playerInput.shift();
        if (onGround && playerInput.shift() && !playerInput.jump()) {
            verticalInput = false;
        }
        return playerInput.forward()
                || playerInput.backward()
                || playerInput.left()
                || playerInput.right()
                || verticalInput;
    }
}
