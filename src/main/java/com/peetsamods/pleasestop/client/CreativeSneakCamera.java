package com.peetsamods.pleasestop.client;

final class CreativeSneakCamera {
    private CreativeSneakCamera() {
    }

    static boolean shouldStabilize(boolean enabled, boolean creative, boolean flying, boolean onGround, boolean sneaking) {
        return enabled && creative && flying && onGround && sneaking;
    }
}
