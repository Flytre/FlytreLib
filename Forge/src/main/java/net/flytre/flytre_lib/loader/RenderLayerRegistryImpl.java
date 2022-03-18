package net.flytre.flytre_lib.loader;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.Fluid;

final class RenderLayerRegistryImpl implements RenderLayerRegistry.Delegate {

    private RenderLayerRegistryImpl() {

    }

    public static void init() {
        RenderLayerRegistry.setDelegate(new RenderLayerRegistryImpl());
    }

    @Override
    public void register(RenderLayer type, Block... blocks) {
        for (Block block : blocks) {
            RenderLayers.setRenderLayer(block, type);
        }
    }

    @Override
    public void register(RenderLayer type, Fluid... fluids) {
        for (Fluid fluid : fluids) {
            RenderLayers.setRenderLayer(fluid, type);
        }
    }
}
