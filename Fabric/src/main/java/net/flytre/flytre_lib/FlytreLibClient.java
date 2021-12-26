package net.flytre.flytre_lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.impl.base.SetupRenderUtils;

public class FlytreLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        if (FabricLoader.getInstance().isModLoaded("fabric-rendering-fluids-v1"))
            SetupRenderUtils.setup();
    }
}
