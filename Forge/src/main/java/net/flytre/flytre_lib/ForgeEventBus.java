package net.flytre.flytre_lib;

import net.flytre.flytre_lib.impl.config.ReloadConfigCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventBus {

    public ForgeEventBus() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e) {
        ReloadConfigCommand.register(e.getDispatcher());
    }
}
