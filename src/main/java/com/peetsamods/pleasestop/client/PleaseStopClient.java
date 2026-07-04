package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.PleaseStopConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;

public final class PleaseStopClient implements ClientModInitializer {
    public static final String MOD_ID = "please_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String CATEGORY = "key.category.please_stop.controls";

    private PleaseStopConfig config;
    private Path configPath;
    private KeyBinding toggleKeyBinding;
    private KeyBinding toggleToastsKeyBinding;
    private final LaunchToastGate launchToastGate = new LaunchToastGate();
    private boolean loggedActiveInput;
    private boolean loggedDisabledDrift;
    private boolean hadActiveFlightInputLastTick;
    private boolean brakeAfterToggleOn;

    @Override
    public void onInitializeClient() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(PleaseStopConfig.FILE_NAME);
        config = loadConfig(configPath);
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.please_stop.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                CATEGORY
        ));
        toggleToastsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.please_stop.toggle_toasts",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        LOGGER.info("Please Stop client loaded. enabled={}", config.isEnabled());
    }

    private PleaseStopConfig loadConfig(Path path) {
        try {
            return PleaseStopConfig.loadOrCreate(path);
        } catch (IOException exception) {
            LOGGER.warn("Could not write Please Stop config; using disabled defaults.", exception);
            return PleaseStopConfig.load(path);
        }
    }

    private void onEndClientTick(MinecraftClient client) {
        boolean userFacingControlsAllowed = allowsUserFacingControls(client);
        if (launchToastGate.tick(client.player != null, userFacingControlsAllowed && config.showToasts())) {
            showLaunchToast(client);
        }

        while (toggleKeyBinding.wasPressed()) {
            if (!allowsUserFacingControls(client)) {
                continue;
            }

            boolean enabled = config.toggle();
            loggedActiveInput = false;
            loggedDisabledDrift = false;
            brakeAfterToggleOn = enabled;
            saveConfig();
            sendToggleFeedback(client, enabled);
            LOGGER.info("Please Stop toggled {}.", enabled ? "on" : "off");
        }

        while (toggleToastsKeyBinding.wasPressed()) {
            if (!allowsUserFacingControls(client)) {
                continue;
            }

            boolean showToasts = config.toggleToasts();
            saveConfig();
            sendToastToggleFeedback(client, showToasts);
            LOGGER.info("Please Stop launch toasts toggled {}.", showToasts ? "on" : "off");
        }

        CreativeFlightBrake.Action action = CreativeFlightBrake.apply(
                client.player,
                config.isEnabled(),
                hadActiveFlightInputLastTick,
                brakeAfterToggleOn
        );
        brakeAfterToggleOn = false;

        if (action == CreativeFlightBrake.Action.BRAKE) {
            LOGGER.info("Please Stop cleared residual creative flight drift.");
            loggedActiveInput = false;
        } else if (action == CreativeFlightBrake.Action.ACTIVE_FLIGHT_INPUT_OBSERVED && !loggedActiveInput) {
            LOGGER.info("Please Stop preserved active creative flight input.");
            loggedActiveInput = true;
        } else if (action == CreativeFlightBrake.Action.VANILLA_DRIFT_OBSERVED && !loggedDisabledDrift) {
            LOGGER.info("Please Stop observed vanilla creative flight drift while disabled.");
            loggedDisabledDrift = true;
        } else if (action == CreativeFlightBrake.Action.NONE) {
            loggedActiveInput = false;
            loggedDisabledDrift = false;
        }

        hadActiveFlightInputLastTick = hasActiveFlightInput(client);
    }

    private boolean hasActiveFlightInput(MinecraftClient client) {
        return isCreativePlayer(client)
                && client.player.getAbilities().flying
                && client.player.input != null
                && CreativeFlightBrake.hasActiveFlightInput(client.player.input);
    }

    private boolean isCreativePlayer(MinecraftClient client) {
        return client.player != null
                && client.interactionManager != null
                && isCreativeGameMode(client.interactionManager.getCurrentGameMode());
    }

    static boolean isCreativeGameMode(GameMode gameMode) {
        return gameMode == GameMode.CREATIVE;
    }

    private boolean allowsUserFacingControls(MinecraftClient client) {
        GameMode clientGameMode = client.interactionManager == null
                ? null
                : client.interactionManager.getCurrentGameMode();
        return allowsUserFacingControls(
                client.player != null,
                client.player != null && client.player.isCreative(),
                client.getServer() != null,
                clientGameMode,
                getSingleplayerSaveGameMode(client)
        );
    }

    static boolean allowsUserFacingControls(
            boolean playerPresent,
            boolean playerCreative,
            boolean singleplayer,
            GameMode clientGameMode,
            GameMode singleplayerSaveGameMode
    ) {
        if (!playerPresent || !playerCreative || !isCreativeGameMode(clientGameMode)) {
            return false;
        }

        if (singleplayer) {
            return isCreativeGameMode(singleplayerSaveGameMode);
        }

        return true;
    }

    private GameMode getSingleplayerSaveGameMode(MinecraftClient client) {
        if (client.getServer() == null || client.getServer().getSaveProperties() == null) {
            return null;
        }

        return client.getServer().getSaveProperties().getGameMode();
    }

    private void saveConfig() {
        try {
            config.save(configPath);
        } catch (IOException exception) {
            LOGGER.error("Could not save Please Stop config.", exception);
        }
    }

    private void sendToggleFeedback(MinecraftClient client, boolean enabled) {
        if (allowsUserFacingControls(client)) {
            client.player.sendMessage(Text.translatable(enabled
                    ? "message.please_stop.enabled"
                    : "message.please_stop.disabled"), true);
        }
    }

    private void sendToastToggleFeedback(MinecraftClient client, boolean showToasts) {
        if (allowsUserFacingControls(client)) {
            client.player.sendMessage(Text.translatable(showToasts
                    ? "message.please_stop.toasts_enabled"
                    : "message.please_stop.toasts_disabled"), true);
        }
    }

    private void showLaunchToast(MinecraftClient client) {
        if (!allowsUserFacingControls(client)) {
            return;
        }

        SystemToast.add(
                client.getToastManager(),
                SystemToast.Type.PERIODIC_NOTIFICATION,
                Text.translatable("toast.please_stop.title"),
                Text.translatable("toast.please_stop.body")
        );
    }
}
