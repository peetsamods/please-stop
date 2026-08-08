package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import com.peetsamods.pleasestop.config.PleaseStopConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public final class PleaseStopBeta3ClientGameTest implements FabricClientGameTest {
    private static final String PROOF = "PLEASE_STOP_BETA3_CLIENT_PROOF";

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder()
                .setUseConsistentSettings(true)
                .adjustSettings(creator -> creator.setGameMode(WorldCreator.Mode.CREATIVE))
                .create()) {
            context.waitFor(client -> client.player != null
                    && client.player.isCreative()
                    && client.player.getAbilities().allowFlying);

            ensurePleaseStopEnabled(context);
            proveSettingsScreen(context);
            proveGroundFlightAndBrake(context);
            proveMasterAndSurvivalNoOps(context, singleplayer);

            System.out.println("[" + PROOF + "] terminalClassification=GREEN"
                    + " settingsScreen=true"
                    + " settingsTooltip=true"
                    + " directFlightKey=true"
                    + " persistentGroundFlight=true"
                    + " noInertiaAfterRelease=true"
                    + " sneakCompatible=true"
                    + " sneakCameraStable=true"
                    + " sneakViewBobSuppressed=true"
                    + " manualOff=true"
                    + " manualOffPersists=true"
                    + " masterOffNoOp=true"
                    + " survivalNoOp=true");
        }
    }

    private static void ensurePleaseStopEnabled(ClientGameTestContext context) {
        if (!readConfig().isEnabled()) {
            context.getInput().pressKey(GLFW.GLFW_KEY_B);
            context.waitFor(client -> readConfig().isEnabled());
        }
    }

    private static void proveSettingsScreen(ClientGameTestContext context) {
        context.getInput().pressKey(GLFW.GLFW_KEY_M);
        context.waitForScreen(PleaseStopSettingsScreen.class);
        require(context.computeOnClient(client -> ((PleaseStopSettingsScreen) client.currentScreen)
                        .flightAssistTooltip().getString().contains("restores")),
                "Flight Assist tooltip did not explain Persistent mode");

        CreativeFlightAssistMode before = readConfig().creativeFlightAssistMode();
        clickFlightAssistButton(context);
        context.waitFor(client -> readConfig().creativeFlightAssistMode() != before);

        for (int i = 0; i < CreativeFlightAssistMode.values().length
                && readConfig().creativeFlightAssistMode() != CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION; i++) {
            CreativeFlightAssistMode current = readConfig().creativeFlightAssistMode();
            clickFlightAssistButton(context);
            context.waitFor(client -> readConfig().creativeFlightAssistMode() != current);
        }
        require(readConfig().creativeFlightAssistMode() == CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                "settings screen did not restore Persistent mode");

        context.waitTicks(20);
        Path screenshot = context.takeScreenshot("please-stop-beta3-settings");
        require(Files.isRegularFile(screenshot), "settings screenshot was not created");
        context.setScreen(() -> null);
        context.waitForScreen(null);
    }

    private static void clickFlightAssistButton(ClientGameTestContext context) {
        double[] position = context.computeOnClient(client -> {
            PleaseStopSettingsScreen screen = (PleaseStopSettingsScreen) client.currentScreen;
            double scale = client.getWindow().getScaleFactor();
            return new double[]{screen.width / 2.0d * scale, (screen.height / 4.0d + 50.0d) * scale};
        });
        context.getInput().setCursorPos(position[0], position[1]);
        context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static void proveGroundFlightAndBrake(ClientGameTestContext context) {
        context.getInput().pressKey(GLFW.GLFW_KEY_B);
        context.waitFor(client -> !readConfig().isEnabled());
        context.waitTicks(2);
        context.getInput().pressKey(GLFW.GLFW_KEY_B);
        context.waitFor(client -> readConfig().isEnabled());
        context.waitTicks(2);

        context.runOnClient(client -> {
            client.player.getAbilities().flying = false;
            client.player.sendAbilitiesUpdate();
            client.player.setVelocity(Vec3d.ZERO);
        });

        context.getInput().holdKey(options -> options.sneakKey);
        context.waitTicks(1);
        context.getInput().pressKey(GLFW.GLFW_KEY_V);
        context.waitFor(client -> client.player != null && client.player.getAbilities().flying);
        context.waitTicks(4);
        require(context.computeOnClient(client -> client.player.isOnGround()),
                "Creative Flight Assist proof player was not at ground level");
        require(context.computeOnClient(client -> client.player.getAbilities().flying),
                "Persistent mode did not keep Creative flight active at ground level");
        require(context.computeOnClient(client -> PleaseStopClient.shouldStabilizeSneakCamera(client.player)),
                "Sneak camera stabilization gate was not active");
        require(context.computeOnClient(client -> Math.abs(
                client.gameRenderer.getCamera().getCameraPos().y
                        - (client.player.getY() + client.player.getEyeHeight(EntityPose.STANDING))
        ) < 0.05d), "Sneak camera dipped or bobbed during assisted ground flight");
        require(PleaseStopClient.suppressedSneakViewBobCount() > 0,
                "Minecraft's separate view-bobbing transform was not suppressed");

        context.getInput().holdKey(options -> options.forwardKey);
        context.waitTicks(4);
        context.getInput().releaseKey(options -> options.forwardKey);
        context.waitTicks(2);
        require(context.computeOnClient(client -> client.player.getVelocity().lengthSquared() < 1.0e-8d),
                "velocity remained after ground-flight movement was released while Sneak stayed held");
        require(context.computeOnClient(client -> client.player.getAbilities().flying),
                "Sneak canceled assisted Creative flight at ground level");

        context.getInput().releaseKey(options -> options.sneakKey);
        context.waitTicks(1);

        context.getInput().pressKey(GLFW.GLFW_KEY_V);
        context.waitFor(client -> client.player != null && !client.player.getAbilities().flying);
        context.waitTicks(2);
        require(context.computeOnClient(client -> !client.player.getAbilities().flying),
                "manual Flight Assist off was not respected");

        context.runOnClient(client -> {
            client.player.getAbilities().flying = true;
            client.player.sendAbilitiesUpdate();
        });
        context.waitTicks(2);
        context.runOnClient(client -> {
            client.player.getAbilities().flying = false;
            client.player.sendAbilitiesUpdate();
        });
        context.waitTicks(4);
        require(context.computeOnClient(client -> !client.player.getAbilities().flying),
                "manual Flight Assist off was lost after a later Creative-flight activation");
    }

    private static void proveMasterAndSurvivalNoOps(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        context.getInput().pressKey(GLFW.GLFW_KEY_B);
        context.waitFor(client -> !readConfig().isEnabled());
        context.getInput().pressKey(GLFW.GLFW_KEY_V);
        context.waitTicks(2);
        require(context.computeOnClient(client -> !client.player.getAbilities().flying),
                "Flight Assist activated while the Please Stop master toggle was off");

        context.getInput().pressKey(GLFW.GLFW_KEY_B);
        context.waitFor(client -> readConfig().isEnabled());
        singleplayer.getServer().runOnServer(server -> server.getPlayerManager().getPlayerList()
                .forEach(player -> player.changeGameMode(GameMode.SURVIVAL)));
        context.waitFor(client -> client.player != null && !client.player.isCreative());
        context.getInput().pressKey(GLFW.GLFW_KEY_V);
        context.waitTicks(2);
        require(context.computeOnClient(client -> !client.player.getAbilities().flying),
                "Flight Assist activated outside Creative mode");
    }

    private static PleaseStopConfig readConfig() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(PleaseStopConfig.FILE_NAME);
        return PleaseStopConfig.load(path);
    }

    private static void require(boolean condition, String failure) {
        if (!condition) {
            throw new AssertionError(PROOF + ": " + failure);
        }
    }
}
