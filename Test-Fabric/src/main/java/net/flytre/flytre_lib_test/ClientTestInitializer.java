package net.flytre.flytre_lib_test;

import net.fabricmc.api.ClientModInitializer;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;

public class ClientTestInitializer implements ClientModInitializer {

    public static ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "aim_plus","config.aim_plus");


    @Override
    public void onInitializeClient() {
        ClientRegistry.init();

        ConfigRegistry.registerClientConfig(CONFIG);
        CONFIG.handle();
    }
}
