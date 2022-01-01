package net.flytre.flytre_lib.loader.registry;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public interface ScreenRegisterer {


    <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, Factory<H, S> screenFactory);

    @FunctionalInterface
    interface Factory<H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> {
        S create(H handler, PlayerInventory inventory, Text title);
    }
}
