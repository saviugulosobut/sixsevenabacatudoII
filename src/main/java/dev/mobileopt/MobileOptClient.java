package dev.mobileopt;

import net.fabricmc.api.ClientModInitializer;

public class MobileOptClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OptConfig.load();
    }
}
