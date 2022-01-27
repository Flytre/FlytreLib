package net.flytre.flytre_lib_test;

import net.fabricmc.api.ModInitializer;

public class TestInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.init();
    }
}
