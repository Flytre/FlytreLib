package net.flytre.flytre_lib.api.config.reference;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AttributeReference extends Reference<EntityAttribute> {

    public AttributeReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public AttributeReference(@NotNull EntityAttribute value) {
        super(value, Registry.ATTRIBUTE);
    }


    public AttributeReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable EntityAttribute getValue(World world) {
        return getValue(Registry.ATTRIBUTE_KEY, world);
    }


}
