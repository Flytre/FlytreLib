package net.flytre.flytre_lib.impl.storage.init;

import net.fabricmc.api.ModInitializer;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockFilterModeC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockModMatchC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockNbtMatchC2SPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidClickSlotC2SPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidSlotUpdateS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.StorageRegistry;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeClickSlotC2SPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeSlotUpdateS2CPacket;

public class StorageModInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        PacketUtils.registerC2SPacket(BlockModMatchC2SPacket.class, BlockModMatchC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockFilterModeC2SPacket.class, BlockFilterModeC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockNbtMatchC2SPacket.class, BlockNbtMatchC2SPacket::new);


        PacketUtils.registerS2CPacket(FluidInventoryS2CPacket.class, FluidInventoryS2CPacket::new);
        PacketUtils.registerS2CPacket(FluidSlotUpdateS2CPacket.class, FluidSlotUpdateS2CPacket::new);
        PacketUtils.registerC2SPacket(FluidClickSlotC2SPacket.class, FluidClickSlotC2SPacket::new);

        PacketUtils.registerS2CPacket(UpgradeInventoryS2CPacket.class, UpgradeInventoryS2CPacket::new);
        PacketUtils.registerS2CPacket(UpgradeSlotUpdateS2CPacket.class, UpgradeSlotUpdateS2CPacket::new);
        PacketUtils.registerC2SPacket(UpgradeClickSlotC2SPacket.class, UpgradeClickSlotC2SPacket::new);
    }
}
