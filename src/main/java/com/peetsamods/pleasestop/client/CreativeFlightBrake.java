package com.peetsamods.pleasestop.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;

final class CreativeFlightBrake {
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

        Vec3d currentVelocity = player.getVelocity();
        Action action = action(
                enabled,
                player.isCreative(),
                player.getAbilities().flying,
                player.input.playerInput,
                currentVelocity,
                hadActiveFlightInputLastTick,
                justEnabled
        );

        if (action == Action.BRAKE) {
            player.setVelocity(Vec3d.ZERO);
        }

        return action;
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
        if (action(enabled, creative, flying, input, currentVelocity, hadActiveFlightInputLastTick, justEnabled) == Action.BRAKE) {
            return Vec3d.ZERO;
        }

        return currentVelocity;
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
        if (!creative || !flying || currentVelocity.equals(Vec3d.ZERO)) {
            return Action.NONE;
        }

        if (hasActiveFlightInput(input)) {
            return enabled ? Action.ACTIVE_FLIGHT_INPUT_OBSERVED : Action.NONE;
        }

        if (!hadActiveFlightInputLastTick && !justEnabled) {
            return Action.NONE;
        }

        return enabled ? Action.BRAKE : Action.VANILLA_DRIFT_OBSERVED;
    }

    static boolean hasActiveFlightInput(PlayerInput input) {
        PlayerInput playerInput = input == null ? PlayerInput.DEFAULT : input;
        return playerInput.forward()
                || playerInput.backward()
                || playerInput.left()
                || playerInput.right()
                || playerInput.jump()
                || playerInput.sneak();
    }
}
