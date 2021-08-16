package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.ModInitializer;
import net.flytre.flytre_lib.api.base.util.BakeHelper;

public class Initializer implements ModInitializer {


    public static boolean INITIALIZED = false;

    @Override
    public void onInitialize() {
        BakeHelper.fullBake("flytre_lib","lib",null);
        INITIALIZED = true;
    }
}
