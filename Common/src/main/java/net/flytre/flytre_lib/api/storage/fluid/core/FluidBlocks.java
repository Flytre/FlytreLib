package net.flytre.flytre_lib.api.storage.fluid.core;

import net.minecraft.block.FluidBlock;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashSet;

/**
 * Used to cache all blocks that have fluids (water , lava, molten perlium, etc.)
 */
public final class FluidBlocks {

    private static FluidBlocks INSTANCE;
    private final Collection<FluidBlock> fluidBlocks = new HashSet<>();

    private FluidBlocks() {
    }

    public static FluidBlocks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FluidBlocks();
        }
        return INSTANCE;
    }

    public Collection<FluidBlock> getFluidBlocks() {
        if (fluidBlocks.isEmpty()) {
            Registry.BLOCK.forEach(i -> {
                if (i instanceof FluidBlock)
                    fluidBlocks.add((FluidBlock) i);
            });
        }
        return fluidBlocks;
    }
}
