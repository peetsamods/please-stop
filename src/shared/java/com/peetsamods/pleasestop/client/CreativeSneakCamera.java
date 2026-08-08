package com.peetsamods.pleasestop.client;

public final class CreativeSneakCamera {
    private CreativeSneakCamera() {
    }

    public static boolean shouldStabilize(
            boolean enabled,
            boolean creative,
            boolean flying,
            boolean onGround,
            boolean sneaking
    ) {
        return enabled && creative && flying && onGround && sneaking;
    }
}
