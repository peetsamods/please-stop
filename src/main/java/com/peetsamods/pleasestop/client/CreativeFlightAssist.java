package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import net.minecraft.client.network.ClientPlayerEntity;

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

    Action tick(ClientPlayerEntity player, boolean pleaseStopEnabled, CreativeFlightAssistMode mode, boolean flyingLocked) {
        boolean eligible = isEligible(player, pleaseStopEnabled, flyingLocked);
        boolean flying = player != null && player.getAbilities().flying;
        boolean onGround = player != null && player.isOnGround();
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

    Action toggle(ClientPlayerEntity player, boolean pleaseStopEnabled, boolean flyingLocked) {
        if (!isEligible(player, pleaseStopEnabled, flyingLocked)) {
            return Action.NONE;
        }

        if (assistActive && !manuallyDisabled) {
            assistActive = false;
            manuallyDisabled = true;
            if (player.getAbilities().flying) {
                setFlying(player, false);
            }
            wasFlyingLastTick = false;
            return Action.DEACTIVATE;
        }

        assistActive = true;
        manuallyDisabled = false;
        if (!player.getAbilities().flying) {
            setFlying(player, true);
        }
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
        if (state.flying() && !state.wasFlyingLastTick()) {
            active = true;
            suppressed = false;
        } else if (state.flying() && !suppressed) {
            active = true;
        }
        if (state.wasFlyingLastTick() && !state.flying() && !state.onGround()) {
            active = false;
        }
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

    private static boolean isEligible(ClientPlayerEntity player, boolean pleaseStopEnabled, boolean flyingLocked) {
        return player != null && isEligible(new Eligibility(pleaseStopEnabled, player.isCreative(),
                player.getAbilities().allowFlying, player.isSpectator(), player.isFallFlying(), player.isSwimming(),
                player.hasVehicle(), player.hurtTime > 0, flyingLocked));
    }

    private static void setFlying(ClientPlayerEntity player, boolean flying) {
        player.getAbilities().flying = flying;
        player.sendAbilitiesUpdate();
    }
}
