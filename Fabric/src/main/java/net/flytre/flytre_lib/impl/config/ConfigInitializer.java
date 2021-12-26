package net.flytre.flytre_lib.impl.config;

import net.fabricmc.api.ModInitializer;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.event.CommandRegistrationEvent;

public class ConfigInitializer implements ModInitializer {


    @Override
    public void onInitialize() {
        CommandRegistrationEvent.EVENT.register((dispatcher, dedicated) -> ReloadConfigCommand.register(dispatcher));
        PacketUtils.registerS2CPacket(ConfigS2CPacket.class, ConfigS2CPacket::new);
    }
}
