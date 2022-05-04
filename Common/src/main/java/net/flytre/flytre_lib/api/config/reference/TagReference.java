package net.flytre.flytre_lib.api.config.reference;

import net.flytre.flytre_lib.api.base.util.TagUtils;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class TagReference<E> extends Reference<Set<E>> {
    protected TagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    protected TagReference(String namespace, String path) {
        super(namespace, path);
    }

    protected TagReference(TagKey<E> tag) {
        super(tag.id());
    }

    public abstract RegistryKey<Registry<E>> getRegistry();

    @Override
    public @Nullable Set<E> getValue(World world) {
        if (value == null)
            value = TagUtils.getKeyValuesAsSet(world.getRegistryManager(), TagKey.of(getRegistry(), identifier));
        return value;
    }


}
