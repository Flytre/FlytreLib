package net.flytre.flytre_lib.impl.storage;

import net.fabricmc.api.ModInitializer;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockFilterModeC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockModMatchC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockNbtMatchC2SPacket;

public class StorageModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        PacketUtils.registerC2SPacket(BlockModMatchC2SPacket.class,BlockModMatchC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockFilterModeC2SPacket.class,BlockFilterModeC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockNbtMatchC2SPacket.class,BlockNbtMatchC2SPacket::new);
    }
}
