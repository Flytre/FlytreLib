package net.flytre.flytre_lib.config.reference;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DimensionReference extends Reference<DimensionType> {

    public static DimensionReference OVERWORLD = new DimensionReference("minecraft", "overworld");
    public static DimensionReference NETHER = new DimensionReference("minecraft", "the_nether");
    public static DimensionReference END = new DimensionReference("minecraft", "the_end");

    public DimensionReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public DimensionReference(@NotNull DimensionType value, World world) {
        super(value, world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY));
    }

    public DimensionReference(RegistryKey<DimensionType> key, World world) {
        super(world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getOrThrow(key), world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY));
    }


    public DimensionReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable DimensionType getValue(World world) {
        return getValue(Registry.DIMENSION_TYPE_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<DimensionType>> references) {
        return references.contains(this);
    }
}
