package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.core.CreativeFlightBrakeLogic;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;

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
            PlayerInput input,
            Vec3d currentVelocity,
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

    static Action apply(ClientPlayerEntity player, boolean enabled, boolean hadActiveFlightInputLastTick, boolean justEnabled) {
        if (player == null || player.input == null) {
            return Action.NONE;
        }

        Action action = action(new State(
                enabled,
                player.isCreative(),
                player.getAbilities().flying,
                player.isOnGround(),
                player.isSpectator(),
                player.isGliding(),
                player.isSwimming(),
                player.hasVehicle(),
                player.hurtTime > 0,
                player.input.playerInput,
                player.getVelocity(),
                hadActiveFlightInputLastTick,
                justEnabled
        ));

        if (action == Action.BRAKE) {
            player.setVelocity(Vec3d.ZERO);
        }

        return action;
    }

    static Vec3d brakedVelocity(State state) {
        if (action(state) == Action.BRAKE) {
            return Vec3d.ZERO;
        }

        return state.currentVelocity();
    }

    static Vec3d brakedVelocity(
            boolean enabled,
            boolean creative,
            boolean flying,
            PlayerInput input,
            Vec3d currentVelocity,
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
        CreativeFlightBrakeLogic.Action action = CreativeFlightBrakeLogic.action(
                new CreativeFlightBrakeLogic.State(
                        state.enabled(),
                        state.creative(),
                        state.flying(),
                        state.onGround(),
                        state.spectator(),
                        state.gliding(),
                        state.swimming(),
                        state.inVehicle(),
                        state.recentlyHurt(),
                        input(state.input()),
                        state.currentVelocity().equals(Vec3d.ZERO),
                        state.hadActiveFlightInputLastTick(),
                        state.justEnabled()
                )
        );
        return Action.valueOf(action.name());
    }

    static Action action(
            boolean enabled,
            boolean creative,
            boolean flying,
            PlayerInput input,
            Vec3d currentVelocity,
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

    static boolean hasActiveFlightInput(PlayerInput input) {
        return hasActiveFlightInput(input, false);
    }

    static boolean hasActiveFlightInput(PlayerInput input, boolean onGround) {
        return CreativeFlightBrakeLogic.hasActiveFlightInput(input(input), onGround);
    }

    private static CreativeFlightBrakeLogic.Input input(PlayerInput input) {
        PlayerInput actual = input == null ? PlayerInput.DEFAULT : input;
        return new CreativeFlightBrakeLogic.Input(
                actual.forward(),
                actual.backward(),
                actual.left(),
                actual.right(),
                actual.jump(),
                actual.sneak()
        );
    }
}
