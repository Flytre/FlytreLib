package net.flytre.flytre_lib.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;

@Environment(EnvType.CLIENT)
public class FlytreLibClient implements ClientModInitializer {

    public static final ConfigHandler<FlytreLibConfig> HANDLER = new ConfigHandler<>(new FlytreLibConfig(), "flytre_lib");

    @Override
    public void onInitializeClient() {
        ConfigRegistry.registerClientConfig(HANDLER);
    }
}
