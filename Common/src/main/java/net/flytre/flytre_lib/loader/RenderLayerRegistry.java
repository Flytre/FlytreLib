package net.flytre.flytre_lib.loader;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;

import java.util.function.Supplier;

/**
 * Used to set how to render a block or fluid
 */
public final class RenderLayerRegistry {

    private RenderLayerRegistry() {
        throw new AssertionError();
    }

    private static Delegate DELEGATE;

    static void setDelegate(Delegate delegate) {
        RenderLayerRegistry.DELEGATE = delegate;
    }


    public static void registerBlockLayer(RenderLayer type, Supplier<Block> block) {
        DELEGATE.registerBlockLayer(type, block);
    }

    public static void registerFluidLayer(RenderLayer type, Supplier<Fluid> fluid) {
        DELEGATE.registerFluidLayer(type, fluid);
    }

    interface Delegate {
        void registerBlockLayer(RenderLayer type, Supplier<Block> block);

        void registerFluidLayer(RenderLayer type, Supplier<Fluid> fluid);
    }
}
