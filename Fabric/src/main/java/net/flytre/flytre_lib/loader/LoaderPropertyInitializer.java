package net.flytre.flytre_lib.loader;

import net.fabricmc.loader.api.FabricLoader;

public final class LoaderPropertyInitializer {


    private LoaderPropertyInitializer() {
        throw new AssertionError();
    }

    /**
     * Called before anything else happens in the game via mixin.
     */
    public static void init() {

        LoaderPropertiesImpl.init();
        LoaderAgnosticRegistryImpl.init();
        EntityAttributeSetterImpl.init();

        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            ScreenLoaderUtilsImpl.init();
            ItemTabCreatorImpl.init();
            RenderLayerRegistryImpl.init();
        }
    }
}
