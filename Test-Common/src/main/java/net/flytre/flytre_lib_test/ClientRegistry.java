package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.loader.LoaderAgnosticClientRegistry;
import net.flytre.flytre_lib.loader.RenderLayerRegistry;
import net.flytre.flytre_lib_test.client.PipeRenderer;
import net.flytre.flytre_lib_test.client.PipeScreen;
import net.minecraft.client.render.RenderLayer;

public final class ClientRegistry {

    private ClientRegistry() {
    }

    public static void init() {
        RenderLayerRegistry.register(RenderLayer.getCutout(), Registry.ITEM_PIPE);
        RenderLayerRegistry.register(RenderLayer.getCutout(), Registry.FAST_PIPE);


        LoaderAgnosticClientRegistry.register(Registry.ITEM_PIPE_BLOCK_ENTITY, PipeRenderer::new);
        LoaderAgnosticClientRegistry.register(Registry.ITEM_PIPE_SCREEN_HANDLER, PipeScreen::new);
    }
}
