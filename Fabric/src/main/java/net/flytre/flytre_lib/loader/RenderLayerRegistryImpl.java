package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;

import java.util.function.Supplier;

final class RenderLayerRegistryImpl implements RenderLayerRegistry.Delegate {

    private RenderLayerRegistryImpl() {
    }

    public static void init() {
        RenderLayerRegistry.setDelegate(new RenderLayerRegistryImpl());
    }

    @Override
    public void registerBlockLayer(RenderLayer type, Supplier<Block> block) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, block.get());
    }

    @Override
    public void registerFluidLayer(RenderLayer type, Supplier<Fluid> fluid) {
        BlockRenderLayerMap.INSTANCE.putFluids(type, fluid.get());
    }
}
