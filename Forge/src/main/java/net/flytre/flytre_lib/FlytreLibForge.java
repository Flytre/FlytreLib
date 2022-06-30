package net.flytre.flytre_lib;

import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockFilterModeC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockModMatchC2SPacket;
import net.flytre.flytre_lib.api.storage.inventory.filter.packet.BlockNbtMatchC2SPacket;
import net.flytre.flytre_lib.impl.config.ConfigS2CPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidClickSlotC2SPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidSlotUpdateS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeClickSlotC2SPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeSlotUpdateS2CPacket;
import net.flytre.flytre_lib.loader.ClientEvents;
import net.flytre.flytre_lib.loader.LoaderCore;
import net.flytre.flytre_lib.loader.LoaderEvents;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@SuppressWarnings("UtilityClassWithPublicConstructor")
@Mod(FlytreLibConstants.MOD_ID)
@Mod.EventBusSubscriber(modid = FlytreLibConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FlytreLibForge {

    public FlytreLibForge() {
        PacketUtils.registerS2CPacket(ConfigS2CPacket.class, ConfigS2CPacket::new);

        PacketUtils.registerC2SPacket(BlockModMatchC2SPacket.class, BlockModMatchC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockFilterModeC2SPacket.class, BlockFilterModeC2SPacket::new);
        PacketUtils.registerC2SPacket(BlockNbtMatchC2SPacket.class, BlockNbtMatchC2SPacket::new);

        PacketUtils.registerS2CPacket(FluidInventoryS2CPacket.class, FluidInventoryS2CPacket::new);
        PacketUtils.registerS2CPacket(FluidSlotUpdateS2CPacket.class, FluidSlotUpdateS2CPacket::new);
        PacketUtils.registerC2SPacket(FluidClickSlotC2SPacket.class, FluidClickSlotC2SPacket::new);


        PacketUtils.registerS2CPacket(UpgradeInventoryS2CPacket.class, UpgradeInventoryS2CPacket::new);
        PacketUtils.registerS2CPacket(UpgradeSlotUpdateS2CPacket.class, UpgradeSlotUpdateS2CPacket::new);
        PacketUtils.registerC2SPacket(UpgradeClickSlotC2SPacket.class, UpgradeClickSlotC2SPacket::new);

        new ForgeEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEvents::registerRenderers);

        LoaderCore.registerForgeMod("flytre_lib", () -> {
        });

        if (FMLLoader.getDist().isClient())
            new ClientForgeEventBus();
    }


    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event)
    {
    }


    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
        LoaderEvents.preInit(event);
    }
}