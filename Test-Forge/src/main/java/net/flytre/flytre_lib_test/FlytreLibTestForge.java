package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.api.loader.LoaderCore;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod("flytre_lib_test")
@Mod.EventBusSubscriber(modid = "flytre_lib_test", bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlytreLibTestForge {

    public FlytreLibTestForge() {
        LoaderCore.registerForgeMod("flytre_lib_test", Registry::init);
    }


    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
    }
}