package com.peetsamods.pleasestop.core;

/** Loader-neutral gate for Please Stop's Creative-only controls and feedback. */
public final class UserFacingControlsLogic {
    private UserFacingControlsLogic() {
    }

    public static boolean allows(
            boolean playerPresent,
            boolean playerCreative,
            boolean clientCreative
    ) {
        return playerPresent && playerCreative && clientCreative;
    }
}
