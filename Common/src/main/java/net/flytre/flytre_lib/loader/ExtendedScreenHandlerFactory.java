package net.flytre.flytre_lib.loader;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

/**
 * For screen handlers that require custom packet data
 */
@FunctionalInterface
public interface ExtendedScreenHandlerFactory<T extends ScreenHandler> {
    T create(int syncId, PlayerInventory inventory, PacketByteBuf buf);
}
