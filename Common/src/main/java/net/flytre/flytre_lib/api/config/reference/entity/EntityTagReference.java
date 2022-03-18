package net.flytre.flytre_lib.api.config.reference.entity;

import net.flytre.flytre_lib.api.config.reference.TagReference;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public final class EntityTagReference extends TagReference<EntityType<?>> implements ConfigEntity {
    public EntityTagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public EntityTagReference(String namespace, String path) {
        super(namespace, path);
    }

    public EntityTagReference(Tag.Identified<EntityType<?>> tag) {
        super(tag);
    }

    @Override
    public RegistryKey<Registry<EntityType<?>>> getRegistry() {
        return Registry.ENTITY_TYPE_KEY;
    }
}
