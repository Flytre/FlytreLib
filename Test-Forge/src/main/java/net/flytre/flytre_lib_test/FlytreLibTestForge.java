package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.loader.LoaderCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("UtilityClassWithPublicConstructor")
@Mod("flytre_lib_test")
@Mod.EventBusSubscriber(modid = "flytre_lib_test", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FlytreLibTestForge {

    public FlytreLibTestForge() {
        LoaderCore.registerForgeMod("pipe", Registry::init);
        if (FMLEnvironment.dist == Dist.CLIENT)
            ClientRegistry.init();
    }


    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
    }
}