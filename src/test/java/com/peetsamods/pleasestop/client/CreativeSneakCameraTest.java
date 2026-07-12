package com.peetsamods.pleasestop.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CreativeSneakCameraTest {
    @Test
    void stabilizesOnlyEnabledGroundSneakDuringCreativeFlight() {
        assertTrue(CreativeSneakCamera.shouldStabilize(true, true, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(false, true, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, false, true, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, false, true, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, true, false, true));
        assertFalse(CreativeSneakCamera.shouldStabilize(true, true, true, true, false));
    }
}
