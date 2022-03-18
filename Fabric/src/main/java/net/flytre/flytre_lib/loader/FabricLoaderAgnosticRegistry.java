package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

final class FabricLoaderAgnosticRegistry {

    private FabricLoaderAgnosticRegistry() {
        throw new AssertionError();
    }

    static <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        return ScreenHandlerRegistry.registerSimple(new Identifier(mod, id), factory::create);
    }

    static <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        return ScreenHandlerRegistry.registerExtended(new Identifier(mod, id), factory::create);
    }
}
