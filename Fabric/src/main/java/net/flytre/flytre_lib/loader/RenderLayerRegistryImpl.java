package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;

final class RenderLayerRegistryImpl implements RenderLayerRegistry.Delegate {

    private RenderLayerRegistryImpl() {
    }

    public static void init() {
        RenderLayerRegistry.setDelegate(new RenderLayerRegistryImpl());
    }

    @Override
    public void register(RenderLayer type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);

    }

    @Override
    public void register(RenderLayer type, Fluid... fluids) {
        BlockRenderLayerMap.INSTANCE.putFluids(type, fluids);
    }
}
