package net.flytre.flytre_lib.loader;

import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.api.base.registry.EntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

final class LoaderAgnosticClientRegistryImpl implements LoaderAgnosticClientRegistry.Delegate {

    private LoaderAgnosticClientRegistryImpl() {

    }

    public static void init() {
        LoaderAgnosticClientRegistry.setDelegate(new LoaderAgnosticClientRegistryImpl());
    }


    @Override
    public <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, ScreenFactory<H, S> screenFactory) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            FabricLoaderAgnosticClientRegistry.register(type, screenFactory);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            FabricLoaderAgnosticClientRegistry.register(blockEntityType, blockEntityRendererFactory);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        EntityRendererRegistry.register(type, factory);
    }
}
