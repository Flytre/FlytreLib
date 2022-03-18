package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

final class ScreenLoaderUtilsImpl implements ScreenLoaderUtils.ScreenLoaderUtilsDelegate {


    private ScreenLoaderUtilsImpl() {

    }

    public static void init() {
        ScreenLoaderUtils.setDelegate(new ScreenLoaderUtilsImpl());
    }

    @Override
    public void open(ServerPlayerEntity player, CustomScreenHandlerFactory factory) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
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
        });
    }
}
