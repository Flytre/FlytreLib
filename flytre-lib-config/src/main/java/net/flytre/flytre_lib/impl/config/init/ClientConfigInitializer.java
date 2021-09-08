package net.flytre.flytre_lib.impl.config.init;

import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;

@Environment(EnvType.CLIENT)
public class ClientConfigInitializer implements ClientModInitializer {

    public static final ConfigHandler<FlytreLibConfig> HANDLER = new ConfigHandler<>(new FlytreLibConfig(), "flytre_lib", new GsonBuilder().setPrettyPrinting().create());

    @Override
    public void onInitializeClient() {
    }
}
