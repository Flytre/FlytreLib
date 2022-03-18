package net.flytre.flytre_lib_test;

import net.fabricmc.api.ClientModInitializer;

public class ClientTestInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistry.init();
    }
}
