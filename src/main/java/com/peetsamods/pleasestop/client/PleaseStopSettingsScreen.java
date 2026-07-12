package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.PleaseStopConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.network.chat.Component;

final class PleaseStopSettingsScreen extends Screen {
    private final Screen parent;
    private final PleaseStopConfig config;
    private final Runnable saveConfig;

    PleaseStopSettingsScreen(Screen parent, PleaseStopConfig config, Runnable saveConfig) {
        super(Component.translatable("screen.please_stop.title"));
        this.parent = parent;
        this.config = config;
        this.saveConfig = saveConfig;
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        int y = height / 4 + 12;
        addRenderableWidget(Button.builder(enabledMessage(), button -> {
            config.setEnabled(!config.isEnabled());
            saveConfig.run();
            button.setMessage(enabledMessage());
        }).tooltip(Tooltip.create(Component.translatable("tooltip.please_stop.enabled")))
                .bounds(left, y, 200, 20).build());
        addRenderableWidget(Button.builder(flightAssistMessage(), button -> {
            config.setCreativeFlightAssistMode(config.creativeFlightAssistMode().next());
            saveConfig.run();
            button.setMessage(flightAssistMessage());
            button.setTooltip(Tooltip.create(flightAssistTooltip()));
        }).tooltip(Tooltip.create(flightAssistTooltip()))
                .bounds(left, y + 28, 200, 20).build());
        addRenderableWidget(Button.builder(toastsMessage(), button -> {
            config.setShowToasts(!config.showToasts());
            saveConfig.run();
            button.setMessage(toastsMessage());
        }).tooltip(Tooltip.create(Component.translatable("tooltip.please_stop.toasts")))
                .bounds(left, y + 56, 200, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.please_stop.controls"), button ->
                minecraft.setScreenAndShow(new ControlsScreen(this, minecraft.options))
        ).tooltip(Tooltip.create(Component.translatable("tooltip.please_stop.controls")))
                .bounds(left, y + 92, 200, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left, y + 120, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xE0101010);
        context.centeredText(font, title, width / 2, height / 4 - 18, 0xFFFFFF);
        context.centeredText(font, Component.translatable("screen.please_stop.help"), width / 2, height / 4 - 2, 0xA0A0A0);
        context.centeredText(font, Component.translatable("screen.please_stop.key_hint"), width / 2, height / 4 + 164, 0xA0A0A0);
        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        saveConfig.run();
        minecraft.setScreenAndShow(parent);
    }

    private Component enabledMessage() {
        return Component.translatable("screen.please_stop.enabled", state(config.isEnabled()));
    }
    private Component flightAssistMessage() {
        return Component.translatable("screen.please_stop.flight_assist",
                Component.translatable(config.creativeFlightAssistMode().translationKey()));
    }

    private Component flightAssistTooltip() {
        return Component.translatable(switch (config.creativeFlightAssistMode()) {
            case VANILLA -> "tooltip.please_stop.flight_assist.vanilla";
            case PERSISTENT_AFTER_ACTIVATION -> "tooltip.please_stop.flight_assist.persistent";
            case ALWAYS_ON_IN_CREATIVE -> "tooltip.please_stop.flight_assist.always_on";
        });
    }
    private Component toastsMessage() {
        return Component.translatable("screen.please_stop.toasts", state(config.showToasts()));
    }
    private Component state(boolean enabled) {
        return Component.translatable(enabled ? "option.please_stop.on" : "option.please_stop.off");
    }
}
