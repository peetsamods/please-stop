package com.peetsamods.pleasestop.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PleaseStopClient implements ClientModInitializer {
    public static final String MOD_ID = "please_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Please Stop client scaffold loaded.");
    }
}
