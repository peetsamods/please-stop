package com.peetsamods.pleasestop.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SharedClientGateParityTest {
    @Test
    void toastRequiresTwentyConsecutiveEligibleTicksAndResetsBetweenWorlds() {
        LaunchToastGate gate = new LaunchToastGate();
        for (int tick = 1; tick < 20; tick++) {
            assertFalse(gate.tick(true, true));
        }
        assertTrue(gate.tick(true, true));
        assertFalse(gate.tick(true, true));
        assertFalse(gate.tick(false, false));
        for (int tick = 1; tick < 20; tick++) {
            assertFalse(gate.tick(true, true));
        }
        assertTrue(gate.tick(true, true));
    }

    @Test
    void cameraGateIsLimitedToEnabledGroundedCreativeFlightSneak() {
        assertTrue(CreativeSneakCamera.shouldStabilize(true, true, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(false, true, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, false, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, false, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, true, false, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, true, true, false));
    }
}
