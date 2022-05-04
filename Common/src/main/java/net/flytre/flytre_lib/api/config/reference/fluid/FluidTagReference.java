package net.flytre.flytre_lib.api.config.reference.fluid;

import net.flytre.flytre_lib.api.config.reference.TagReference;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public final class FluidTagReference extends TagReference<Fluid> implements ConfigFluid {
    public FluidTagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public FluidTagReference(String namespace, String path) {
        super(namespace, path);
    }

    public FluidTagReference(TagKey<Fluid> tag) {
        super(tag);
    }

    @Override
    public RegistryKey<Registry<Fluid>> getRegistry() {
        return Registry.FLUID_KEY;
    }
}
