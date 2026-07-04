package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

final class CreativeFlightBrakeTest {
    private static final Vec3 DRIFT = new Vec3(0.12, -0.03, 0.08);

    @Test
    void stopsResidualDriftWhenEnabledCreativeFlyingAndInputsReleased() {
        Vec3 result = CreativeFlightBrake.brakedVelocity(true, true, true, Input.EMPTY, DRIFT, true, false);

        assertEquals(Vec3.ZERO, result);
    }

    @Test
    void preservesVanillaVelocityWhenDisabled() {
        Vec3 result = CreativeFlightBrake.brakedVelocity(false, true, true, Input.EMPTY, DRIFT, true, false);

        assertEquals(DRIFT, result);
    }

    @Test
    void observesVanillaResidualDriftWhenDisabled() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(false, true, true, Input.EMPTY, DRIFT, true, false);

        assertEquals(CreativeFlightBrake.Action.VANILLA_DRIFT_OBSERVED, result);
    }

    @Test
    void reportsBrakeWhenEnabled() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, Input.EMPTY, DRIFT, true, false);

        assertEquals(CreativeFlightBrake.Action.BRAKE, result);
    }

    @Test
    void reportsActiveInputObservationWithoutBraking() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(
                true,
                true,
                true,
                input(true, false, false, false, false, false),
                DRIFT,
                true,
                false
        );

        assertEquals(CreativeFlightBrake.Action.ACTIVE_FLIGHT_INPUT_OBSERVED, result);
    }

    @Test
    void reportsBrakeWhenJustEnabledDuringExistingDrift() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, Input.EMPTY, DRIFT, false, true);

        assertEquals(CreativeFlightBrake.Action.BRAKE, result);
    }

    @Test
    void ignoresVelocityThatDidNotFollowFlightInputRelease() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, Input.EMPTY, DRIFT, false, false);

        assertEquals(CreativeFlightBrake.Action.NONE, result);
    }

    @Test
    void ignoresZeroVelocity() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, Input.EMPTY, Vec3.ZERO, true, false);

        assertEquals(CreativeFlightBrake.Action.NONE, result);
    }

    @Test
    void preservesVelocityOutsideCreativeFlight() {
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, false, true, Input.EMPTY, DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, false, Input.EMPTY, DRIFT, true, false));
    }

    @Test
    void preservesVelocityForExcludedMovementStates() {
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(state().spectator(true)));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(state().gliding(true)));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(state().swimming(true)));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(state().inVehicle(true)));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(state().recentlyHurt(true)));
    }

    @Test
    void preservesVelocityWhileMovementInputIsHeld() {
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(true, false, false, false, false, false), DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(false, true, false, false, false, false), DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(false, false, true, false, false, false), DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(false, false, false, true, false, false), DRIFT, true, false));
    }

    @Test
    void preservesVelocityWhileVerticalFlightInputIsHeld() {
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(false, false, false, false, true, false), DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, true, input(false, false, false, false, false, true), DRIFT, true, false));
    }

    private static Input input(
            boolean forward,
            boolean backward,
            boolean left,
            boolean right,
            boolean jump,
            boolean sneak
    ) {
        return new Input(forward, backward, left, right, jump, sneak, false);
    }

    private static CreativeFlightBrake.State state() {
        return new CreativeFlightBrake.State(
                true,
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                Input.EMPTY,
                DRIFT,
                true,
                false
        );
    }
}
