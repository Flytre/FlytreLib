package net.flytre.flytre_lib;

import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockFilterModeC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockModMatchC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockNbtMatchC2SPacket;
import net.flytre.flytre_lib.impl.config.ConfigS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeClickSlotC2SPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeSlotUpdateS2CPacket;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlytreLibForge {

    public FlytreLibForge() {
        PacketUtils.registerS2CPacket(ConfigS2CPacket.class, ConfigS2CPacket::new);

        PacketUtils.registerC2SPacket(BlockModMatchC2SPacket.class, BlockModMatchC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockFilterModeC2SPacket.class, BlockFilterModeC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockNbtMatchC2SPacket.class, BlockNbtMatchC2SPacket::new);


        PacketUtils.registerS2CPacket(UpgradeInventoryS2CPacket.class, UpgradeInventoryS2CPacket::new);
        PacketUtils.registerS2CPacket(UpgradeSlotUpdateS2CPacket.class, UpgradeSlotUpdateS2CPacket::new);
        PacketUtils.registerC2SPacket(UpgradeClickSlotC2SPacket.class, UpgradeClickSlotC2SPacket::new);
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {

    }
}