package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.api.base.util.BakeHelper;

public class Initializer implements ModInitializer {


    public static boolean INITIALIZED = false;

    @Override
    public void onInitialize() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment())
            BakeHelper.fullBake("flytre_lib", "lib", null);
        INITIALIZED = true;
    }
}
