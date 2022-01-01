package net.flytre.flytre_lib.impl.loader;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.flytre_lib.loader.registry.ScreenHandlerRegisterer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class FabricLoaderPropertyInitializer {


    /**
     * Called before anything else happens in the game via mixin.
     */
    public static void init() {
        LoaderProperties.setScreenHandlerRegisterer(new ScreenHandlerRegisterer() {
            @Override
            public <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleFactory<T> factory, String mod, String id) {
                return ScreenHandlerRegistry.registerSimple(new Identifier(mod, id), factory::create);
            }

            @Override
            public <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedFactory<T> factory, String mod, String id) {
                return ScreenHandlerRegistry.registerExtended(new Identifier(mod, id), factory::create);
            }
        });
    }
}
