package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

final class CreativeFlightBrakeTest {
    private static final Vec3d DRIFT = new Vec3d(0.12, -0.03, 0.08);
    private static final Input NO_INPUT = new Input();

    @Test
    void stopsResidualDriftWhenEnabledCreativeFlyingAndInputsReleased() {
        Vec3d result = CreativeFlightBrake.brakedVelocity(true, true, true, NO_INPUT, DRIFT, true, false);

        assertEquals(Vec3d.ZERO, result);
    }

    @Test
    void preservesVanillaVelocityWhenDisabled() {
        Vec3d result = CreativeFlightBrake.brakedVelocity(false, true, true, NO_INPUT, DRIFT, true, false);

        assertEquals(DRIFT, result);
    }

    @Test
    void observesVanillaResidualDriftWhenDisabled() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(false, true, true, NO_INPUT, DRIFT, true, false);

        assertEquals(CreativeFlightBrake.Action.VANILLA_DRIFT_OBSERVED, result);
    }

    @Test
    void reportsBrakeWhenEnabled() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, NO_INPUT, DRIFT, true, false);

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
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, NO_INPUT, DRIFT, false, true);

        assertEquals(CreativeFlightBrake.Action.BRAKE, result);
    }

    @Test
    void ignoresVelocityThatDidNotFollowFlightInputRelease() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, NO_INPUT, DRIFT, false, false);

        assertEquals(CreativeFlightBrake.Action.NONE, result);
    }

    @Test
    void ignoresZeroVelocity() {
        CreativeFlightBrake.Action result = CreativeFlightBrake.action(true, true, true, NO_INPUT, Vec3d.ZERO, true, false);

        assertEquals(CreativeFlightBrake.Action.NONE, result);
    }

    @Test
    void preservesVelocityOutsideCreativeFlight() {
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, false, true, NO_INPUT, DRIFT, true, false));
        assertEquals(DRIFT, CreativeFlightBrake.brakedVelocity(true, true, false, NO_INPUT, DRIFT, true, false));
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

    @Test
    void stopsResidualDriftWhenJumpAndSneakAreHeldTogetherAfterMovementRelease() {
        Vec3d result = CreativeFlightBrake.brakedVelocity(
                true,
                true,
                true,
                input(false, false, false, false, true, true),
                DRIFT,
                true,
                false
        );

        assertEquals(Vec3d.ZERO, result);
    }

    @Test
    void groundSneakDoesNotBlockNoInertiaBrake() {
        Input sneakOnly = input(false, false, false, false, false, true);

        assertEquals(false, CreativeFlightBrake.hasActiveFlightInput(sneakOnly, true));
        assertEquals(true, CreativeFlightBrake.hasActiveFlightInput(sneakOnly, false));
    }

    private static Input input(
            boolean forward,
            boolean backward,
            boolean left,
            boolean right,
            boolean jump,
            boolean sneak
    ) {
        Input input = new Input();
        input.pressingForward = forward;
        input.pressingBack = backward;
        input.pressingLeft = left;
        input.pressingRight = right;
        input.jumping = jump;
        input.sneaking = sneak;
        return input;
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
                false,
                NO_INPUT,
                DRIFT,
                true,
                false
        );
    }
}
