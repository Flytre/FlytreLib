package net.flytre.flytre_lib.impl.config;

import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.init.FlytreLibConfig;
import net.flytre.flytre_lib.loader.LoaderProperties;

@Environment(EnvType.CLIENT)
public class ClientConfigInitializer implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
    }
}
