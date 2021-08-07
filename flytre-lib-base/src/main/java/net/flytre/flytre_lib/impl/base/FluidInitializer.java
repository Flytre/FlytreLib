package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class FluidInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if(FabricLoader.getInstance().isModLoaded("fabric-rendering-fluids-v1"))
            SetupRenderUtils.setup();
    }
}
