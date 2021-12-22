package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {


    public static boolean INITIALIZED = false;

    @Override
    public void onInitialize() {
        INITIALIZED = true;
    }
}
