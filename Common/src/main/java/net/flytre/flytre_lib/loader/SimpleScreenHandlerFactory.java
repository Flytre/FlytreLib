package net.flytre.flytre_lib.loader;


import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

/**
 * For screen handlers that don't require packet data
 */
@FunctionalInterface
public interface SimpleScreenHandlerFactory<T extends ScreenHandler> {
    T create(int syncId, PlayerInventory inventory);
}
