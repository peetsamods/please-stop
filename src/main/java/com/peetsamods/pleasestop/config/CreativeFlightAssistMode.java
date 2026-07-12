package com.peetsamods.pleasestop.config;

public enum CreativeFlightAssistMode {
    VANILLA("option.please_stop.flight_assist.vanilla"),
    PERSISTENT_AFTER_ACTIVATION("option.please_stop.flight_assist.persistent"),
    ALWAYS_ON_IN_CREATIVE("option.please_stop.flight_assist.always_on");

    private final String translationKey;

    CreativeFlightAssistMode(String translationKey) { this.translationKey = translationKey; }
    public String translationKey() { return translationKey; }
    public CreativeFlightAssistMode next() {
        CreativeFlightAssistMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }
}
