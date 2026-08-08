package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import net.minecraft.client.player.LocalPlayer;

final class CreativeFlightAssist {
    record Eligibility(boolean pleaseStopEnabled, boolean creative, boolean allowFlying, boolean spectator,
                       boolean gliding, boolean swimming, boolean inVehicle, boolean recentlyHurt,
                       boolean flyingLocked) {
    }

    record State(CreativeFlightAssistMode mode, CreativeFlightAssistMode previousMode, boolean eligible,
                 boolean onGround, boolean flying, boolean wasFlyingLastTick, boolean assistActive,
                 boolean manuallyDisabled) {
    }

    record Transition(Action action, boolean assistActive, boolean manuallyDisabled) {
    }

    enum Action { NONE, ACTIVATE, DEACTIVATE, REACTIVATE }

    private boolean assistActive;
    private boolean manuallyDisabled;
    private boolean wasFlyingLastTick;
    private CreativeFlightAssistMode previousMode = CreativeFlightAssistMode.VANILLA;

    Action tick(LocalPlayer player, boolean pleaseStopEnabled, CreativeFlightAssistMode mode, boolean flyingLocked) {
        boolean eligible = isEligible(player, pleaseStopEnabled, flyingLocked);
        boolean flying = player != null && player.getAbilities().flying;
        boolean onGround = player != null && player.onGround();
        Transition transition = transition(new State(mode, previousMode, eligible, onGround, flying,
                wasFlyingLastTick, assistActive, manuallyDisabled));

        assistActive = transition.assistActive();
        manuallyDisabled = transition.manuallyDisabled();
        previousMode = mode;
        if (transition.action() == Action.REACTIVATE) {
            setFlying(player, true);
            flying = true;
        }
        wasFlyingLastTick = flying;
        return transition.action();
    }

    Action toggle(LocalPlayer player, boolean pleaseStopEnabled, boolean flyingLocked) {
        if (!isEligible(player, pleaseStopEnabled, flyingLocked)) return Action.NONE;
        if (assistActive && !manuallyDisabled) {
            assistActive = false;
            manuallyDisabled = true;
            if (player.getAbilities().flying) setFlying(player, false);
            wasFlyingLastTick = false;
            return Action.DEACTIVATE;
        }
        assistActive = true;
        manuallyDisabled = false;
        if (!player.getAbilities().flying) setFlying(player, true);
        wasFlyingLastTick = true;
        return Action.ACTIVATE;
    }

    static Transition transition(State state) {
        if (!state.eligible() || state.mode() == CreativeFlightAssistMode.VANILLA) {
            return new Transition(Action.NONE, false, false);
        }
        boolean active = state.assistActive();
        boolean suppressed = state.manuallyDisabled();
        if (state.mode() != state.previousMode()) {
            active = false;
            suppressed = false;
        }
        if (state.flying() && !suppressed) {
            // Vanilla flight entry must not undo an explicit Flight Assist OFF choice.
            active = true;
        }
        if (state.wasFlyingLastTick() && !state.flying() && !state.onGround()) active = false;
        if (!suppressed && state.mode() == CreativeFlightAssistMode.ALWAYS_ON_IN_CREATIVE && !state.flying()) {
            return new Transition(Action.REACTIVATE, true, false);
        }
        if (!suppressed && state.mode() == CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION
                && active && state.onGround() && !state.flying()) {
            return new Transition(Action.REACTIVATE, true, false);
        }
        return new Transition(Action.NONE, active, suppressed);
    }

    static boolean isEligible(Eligibility eligibility) {
        return eligibility.pleaseStopEnabled() && eligibility.creative() && eligibility.allowFlying()
                && !eligibility.spectator() && !eligibility.gliding() && !eligibility.swimming()
                && !eligibility.inVehicle() && !eligibility.recentlyHurt() && !eligibility.flyingLocked();
    }

    private static boolean isEligible(LocalPlayer player, boolean pleaseStopEnabled, boolean flyingLocked) {
        return player != null && isEligible(new Eligibility(pleaseStopEnabled, player.isCreative(),
                player.getAbilities().mayfly, player.isSpectator(), player.isFallFlying(), player.isSwimming(),
                player.isPassenger(), player.hurtTime > 0, flyingLocked));
    }

    private static void setFlying(LocalPlayer player, boolean flying) {
        player.getAbilities().flying = flying;
        player.onUpdateAbilities();
    }
}
