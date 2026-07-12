package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.PleaseStopConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

final class PleaseStopSettingsScreen extends Screen {
    private final Screen parent;
    private final PleaseStopConfig config;
    private final Runnable saveConfig;

    PleaseStopSettingsScreen(Screen parent, PleaseStopConfig config, Runnable saveConfig) {
        super(Text.translatable("screen.please_stop.title"));
        this.parent = parent;
        this.config = config;
        this.saveConfig = saveConfig;
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        int y = height / 4 + 12;
        addDrawableChild(ButtonWidget.builder(enabledMessage(), button -> {
            config.setEnabled(!config.isEnabled());
            saveConfig.run();
            button.setMessage(enabledMessage());
        }).tooltip(Tooltip.of(Text.translatable("tooltip.please_stop.enabled")))
                .dimensions(left, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(flightAssistMessage(), button -> {
            config.setCreativeFlightAssistMode(config.creativeFlightAssistMode().next());
            saveConfig.run();
            button.setMessage(flightAssistMessage());
            button.setTooltip(Tooltip.of(flightAssistTooltip()));
        }).tooltip(Tooltip.of(flightAssistTooltip()))
                .dimensions(left, y + 28, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(toastsMessage(), button -> {
            config.setShowToasts(!config.showToasts());
            saveConfig.run();
            button.setMessage(toastsMessage());
        }).tooltip(Tooltip.of(Text.translatable("tooltip.please_stop.toasts")))
                .dimensions(left, y + 56, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.please_stop.controls"), button ->
                client.setScreen(new ControlsOptionsScreen(this, client.options))
        ).tooltip(Tooltip.of(Text.translatable("tooltip.please_stop.controls")))
                .dimensions(left, y + 92, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
                .dimensions(left, y + 120, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 18, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.please_stop.help"), width / 2, height / 4 - 2, 0xA0A0A0);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.please_stop.key_hint"), width / 2, height / 4 + 164, 0xA0A0A0);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void close() {
        saveConfig.run();
        client.setScreen(parent);
    }

    private Text enabledMessage() {
        return Text.translatable("screen.please_stop.enabled", state(config.isEnabled()));
    }

    private Text flightAssistMessage() {
        return Text.translatable("screen.please_stop.flight_assist", Text.translatable(config.creativeFlightAssistMode().translationKey()));
    }

    private Text flightAssistTooltip() {
        return Text.translatable(switch (config.creativeFlightAssistMode()) {
            case VANILLA -> "tooltip.please_stop.flight_assist.vanilla";
            case PERSISTENT_AFTER_ACTIVATION -> "tooltip.please_stop.flight_assist.persistent";
            case ALWAYS_ON_IN_CREATIVE -> "tooltip.please_stop.flight_assist.always_on";
        });
    }

    private Text toastsMessage() {
        return Text.translatable("screen.please_stop.toasts", state(config.showToasts()));
    }

    private Text state(boolean enabled) {
        return Text.translatable(enabled ? "option.please_stop.on" : "option.please_stop.off");
    }
}
