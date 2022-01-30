package net.flytre.flytre_lib.impl.loader;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.flytre_lib.api.loader.screen.ScreenLoaderUtils;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.flytre_lib.loader.registry.ScreenHandlerRegisterer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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

        ScreenLoaderUtils.setScreenOpener(((player, factory) -> player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                factory.sendPacket(buf);
            }

            @Override
            public Text getDisplayName() {
                return factory.getDisplayName();
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return factory.createMenu(syncId, inv, player);
            }
        })));
    }
}
