package net.flytre.flytre_lib.config.reference;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class BiomeReference extends Reference<Biome> {

    public BiomeReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public BiomeReference(@NotNull Biome value, World world) {
        super(value, world.getRegistryManager().get(Registry.BIOME_KEY));
    }

    public BiomeReference(RegistryKey<Biome> key, World world) {
        super(world.getRegistryManager().get(Registry.BIOME_KEY).getOrThrow(key), world.getRegistryManager().get(Registry.BIOME_KEY));
    }

    /**
     * To access vanilla defaults via key
     */
    public BiomeReference(RegistryKey<Biome> key) {
        super(Objects.requireNonNull(BuiltinRegistries.BIOME.get(key)), BuiltinRegistries.BIOME);
    }



    public BiomeReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable Biome getValue(World world) {
        return getValue(Registry.BIOME_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<Biome>> references) {
        return references.contains(this);
    }
}
