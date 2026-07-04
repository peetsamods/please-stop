package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.PleaseStopConfig;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PleaseStopClient implements ClientModInitializer {
    public static final String MOD_ID = "please_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );

    private PleaseStopConfig config;
    private Path configPath;
    private KeyMapping toggleKeyMapping;
    private KeyMapping toggleToastsKeyMapping;
    private final LaunchToastGate launchToastGate = new LaunchToastGate();
    private boolean loggedActiveInput;
    private boolean loggedDisabledDrift;
    private boolean hadActiveFlightInputLastTick;
    private boolean brakeAfterToggleOn;

    @Override
    public void onInitializeClient() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(PleaseStopConfig.FILE_NAME);
        config = loadConfig(configPath);
        toggleKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.please_stop.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                CATEGORY
        ));
        toggleToastsKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.please_stop.toggle_toasts",
                InputConstants.Type.KEYSYM,
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

    private void onEndClientTick(Minecraft client) {
        boolean userFacingControlsAllowed = allowsUserFacingControls(client);
        if (launchToastGate.tick(client.player != null, userFacingControlsAllowed && config.showToasts())) {
            showLaunchToast(client);
        }

        while (toggleKeyMapping.consumeClick()) {
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

        while (toggleToastsKeyMapping.consumeClick()) {
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

    private boolean hasActiveFlightInput(Minecraft client) {
        return isCreativePlayer(client)
                && client.player.getAbilities().flying
                && client.player.input != null
                && CreativeFlightBrake.hasActiveFlightInput(client.player.input.keyPresses);
    }

    private boolean isCreativePlayer(Minecraft client) {
        return client.player != null
                && client.gameMode != null
                && isCreativeGameMode(client.gameMode.getPlayerMode());
    }

    static boolean isCreativeGameMode(GameType gameType) {
        return gameType == GameType.CREATIVE;
    }

    private boolean allowsUserFacingControls(Minecraft client) {
        GameType clientGameType = client.gameMode == null
                ? null
                : client.gameMode.getPlayerMode();
        return allowsUserFacingControls(
                client.player != null,
                client.player != null && client.player.isCreative(),
                client.getSingleplayerServer() != null,
                clientGameType,
                getSingleplayerSaveGameType(client)
        );
    }

    static boolean allowsUserFacingControls(
            boolean playerPresent,
            boolean playerCreative,
            boolean singleplayer,
            GameType clientGameType,
            GameType singleplayerSaveGameType
    ) {
        if (!playerPresent || !playerCreative || !isCreativeGameMode(clientGameType)) {
            return false;
        }

        if (singleplayer) {
            return isCreativeGameMode(singleplayerSaveGameType);
        }

        return true;
    }

    private GameType getSingleplayerSaveGameType(Minecraft client) {
        if (client.getSingleplayerServer() == null || client.getSingleplayerServer().getWorldData() == null) {
            return null;
        }

        return client.getSingleplayerServer().getWorldData().getGameType();
    }

    private void saveConfig() {
        try {
            config.save(configPath);
        } catch (IOException exception) {
            LOGGER.error("Could not save Please Stop config.", exception);
        }
    }

    private void sendToggleFeedback(Minecraft client, boolean enabled) {
        if (allowsUserFacingControls(client)) {
            client.player.sendOverlayMessage(Component.translatable(enabled
                    ? "message.please_stop.enabled"
                    : "message.please_stop.disabled"));
        }
    }

    private void sendToastToggleFeedback(Minecraft client, boolean showToasts) {
        if (allowsUserFacingControls(client)) {
            client.player.sendOverlayMessage(Component.translatable(showToasts
                    ? "message.please_stop.toasts_enabled"
                    : "message.please_stop.toasts_disabled"));
        }
    }

    private void showLaunchToast(Minecraft client) {
        if (!allowsUserFacingControls(client)) {
            return;
        }

        SystemToast.addOrUpdate(
                client.gui.toastManager(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.please_stop.title"),
                Component.translatable("toast.please_stop.body")
        );
    }
}
