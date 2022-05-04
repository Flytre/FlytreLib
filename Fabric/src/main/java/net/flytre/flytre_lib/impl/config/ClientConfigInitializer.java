package net.flytre.flytre_lib.impl.config;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ClientConfigInitializer implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
    }
}
