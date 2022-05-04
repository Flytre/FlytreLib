package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

final class FabricLoaderAgnosticRegistry {

    private FabricLoaderAgnosticRegistry() {
        throw new AssertionError();
    }

    static <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> register(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        ScreenHandlerType<T> screenHandlerType = ScreenHandlerRegistry.registerSimple(new Identifier(mod, id), factory::create);
        return () -> screenHandlerType;
    }

    static <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        ScreenHandlerType<T> screenHandlerType = ScreenHandlerRegistry.registerExtended(new Identifier(mod, id), factory::create);
        return () -> screenHandlerType;
    }
}
