package net.flytre.flytre_lib.config.reference.fluid;

import net.flytre.flytre_lib.config.reference.TagReference;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public class FluidTagReference extends TagReference<Fluid> implements ConfigFluid {
    public FluidTagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public FluidTagReference(String namespace, String path) {
        super(namespace, path);
    }

    public FluidTagReference(Tag.Identified<Fluid> tag) {
        super(tag);
    }

    @Override
    public RegistryKey<Registry<Fluid>> getRegistry() {
        return Registry.FLUID_KEY;
    }
}
