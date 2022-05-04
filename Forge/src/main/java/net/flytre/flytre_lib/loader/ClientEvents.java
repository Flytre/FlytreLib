package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.FlytreLibConstants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FlytreLibConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientEvents {

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers registerRenderersEvent) {
        for (var entry : LoaderAgnosticClientRegistryImpl.getEntityRenderers()) {
            unpackAndRegister(registerRenderersEvent, entry);
        }

        for (var entry : LoaderAgnosticClientRegistryImpl.getBlockEntityRenderers()) {
            unpackAndRegister(registerRenderersEvent, entry);
        }
    }


    private static <T extends Entity> void unpackAndRegister(EntityRenderersEvent.RegisterRenderers registerRenderersEvent, LoaderAgnosticClientRegistryImpl.EntityRendererEntry<T> entry) {
        EntityType<? extends T> type = entry.type().get();
        EntityRendererFactory<T> factory = entry.factory();
        registerRenderersEvent.registerEntityRenderer(type, factory);
    }

    public static <E extends BlockEntity> void unpackAndRegister(EntityRenderersEvent.RegisterRenderers registerRenderersEvent, LoaderAgnosticClientRegistryImpl.BlockEntityRendererEntry<E> entry) {
        BlockEntityType<E> type = entry.blockEntityType().get();
        BlockEntityRendererFactory<? super E> factory = entry.blockEntityRendererFactory();
        registerRenderersEvent.registerBlockEntityRenderer(type, factory);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RenderLayerRegistryImpl.getBlockLayers().forEach(pair -> RenderLayers.setRenderLayer(pair.getRight().get(), pair.getLeft()));
            RenderLayerRegistryImpl.getFluidLayers().forEach(pair -> RenderLayers.setRenderLayer(pair.getRight().get(), pair.getLeft()));
            LoaderAgnosticClientRegistryImpl.getScreenRegistryEntries().forEach(ClientEvents::unpack);
        });
    }

    private static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void unpack(LoaderAgnosticClientRegistryImpl.ScreenRegistryEntry<H, S> entry) {
        ScreenFactory<H, S> factory = entry.screenFactory();
        ScreenHandlerType<? extends H> type = entry.type().get();
        HandledScreens.register(type, factory::create);
    }
}
