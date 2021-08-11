package net.flytre.flytre_lib.api.config.reference.entity;

import net.flytre.flytre_lib.api.config.reference.Reference;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class EntityReference extends Reference<EntityType<?>> implements ConfigEntity {

    public EntityReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public EntityReference(@NotNull EntityType<?> value) {
        super(value, Registry.ENTITY_TYPE);
    }


    public EntityReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable EntityType<?> getValue(World world) {
        return getValue(Registry.ENTITY_TYPE_KEY, world);
    }


}
