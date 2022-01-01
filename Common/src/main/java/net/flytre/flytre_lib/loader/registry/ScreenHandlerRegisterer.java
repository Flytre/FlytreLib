package net.flytre.flytre_lib.loader.registry;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public interface ScreenHandlerRegisterer {

    <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleFactory<T> factory, String mod, String id);

    <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedFactory<T> factory, String mod, String id);


    public interface SimpleFactory<T extends ScreenHandler> {
        T create(int syncId, PlayerInventory inventory);
    }

    public interface ExtendedFactory<T extends ScreenHandler> {
        T create(int syncId, PlayerInventory inventory, PacketByteBuf buf);
    }
}
