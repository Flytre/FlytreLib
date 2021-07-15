package net.flytre.flytre_lib.config.reference.fluid;

import net.flytre.flytre_lib.config.reference.Reference;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class FluidReference extends Reference<Fluid> implements ConfigFluid {

    public FluidReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public FluidReference(@NotNull Fluid value) {
        super(value, Registry.FLUID);
    }


    public FluidReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable Fluid getValue(World world) {
        return getValue(Registry.FLUID_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<Fluid>> references) {
        return references.contains(this);
    }
}
