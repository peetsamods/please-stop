package com.peetsamods.pleasestop.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(PleaseStopNeoForge.MOD_ID)
public final class PleaseStopNeoForge {
    public static final String MOD_ID = "please_stop";

    public PleaseStopNeoForge(IEventBus modBus) {
        if (NeoForgeRuntimeCompatibility.isClientDistribution()) {
            PleaseStopNeoForgeClient.register(modBus);
        }
    }
}
