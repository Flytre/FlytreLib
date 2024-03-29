package net.flytre.flytre_lib.loader;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

/**
 * Used when registering screen handler screens
 */
@FunctionalInterface
public interface ScreenFactory<H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> {
    S create(H handler, PlayerInventory inventory, Text title);
}
