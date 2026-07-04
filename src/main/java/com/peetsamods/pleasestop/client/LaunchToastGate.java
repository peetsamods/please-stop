package com.peetsamods.pleasestop.client;

final class LaunchToastGate {
    private static final int REQUIRED_ELIGIBLE_TICKS = 20;

    private boolean shownForCurrentWorld;
    private int eligibleTicks;

    boolean tick(boolean inWorld, boolean eligible) {
        if (!inWorld) {
            shownForCurrentWorld = false;
            eligibleTicks = 0;
            return false;
        }

        if (shownForCurrentWorld) {
            return false;
        }

        if (!eligible) {
            eligibleTicks = 0;
            return false;
        }

        eligibleTicks++;
        if (eligibleTicks < REQUIRED_ELIGIBLE_TICKS) {
            return false;
        }

        shownForCurrentWorld = true;
        return true;
    }
}
