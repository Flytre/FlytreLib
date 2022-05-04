package net.flytre.flytre_lib.loader;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class RenderLayerRegistryImpl implements RenderLayerRegistry.Delegate {

    private static final List<Pair<RenderLayer,Supplier<Block>>> BLOCK_LAYERS = new ArrayList<>();
    private static final List<Pair<RenderLayer,Supplier<Fluid>>> FLUID_LAYERS = new ArrayList<>();


    private RenderLayerRegistryImpl() {

    }

    public static void init() {
        RenderLayerRegistry.setDelegate(new RenderLayerRegistryImpl());
    }

    @Override
    public void registerBlockLayer(RenderLayer type, Supplier<Block> block) {
        BLOCK_LAYERS.add(new Pair<>(type,block));
    }

    @Override
    public void registerFluidLayer(RenderLayer type, Supplier<Fluid> fluid) {
        FLUID_LAYERS.add(new Pair<>(type,fluid));
    }

    public static List<Pair<RenderLayer, Supplier<Block>>> getBlockLayers() {
        return ImmutableList.copyOf(BLOCK_LAYERS);
    }

    public static List<Pair<RenderLayer, Supplier<Fluid>>> getFluidLayers() {
        return ImmutableList.copyOf(FLUID_LAYERS);
    }
}
