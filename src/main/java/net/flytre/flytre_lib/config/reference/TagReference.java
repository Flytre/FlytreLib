package net.flytre.flytre_lib.config.reference;

import com.google.gson.JsonSyntaxException;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class TagReference<E> extends Reference<Tag<E>> {
    protected TagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    protected TagReference(String namespace, String path) {
        super(namespace, path);
    }

    protected TagReference(Tag.Identified<E> tag) {
        super(tag.getId(), tag);
    }

    public abstract RegistryKey<Registry<E>> getRegistry();

    @Override
    public @Nullable Tag<E> getValue(World world) {
        return value != null ? value : world.getTagManager().getTag(getRegistry(),identifier, (exc) -> new JsonSyntaxException("Unknown tag '" + exc + "'"));
    }

    @Override
    public boolean isIn(Collection<? extends Reference<Tag<E>>> references) {
        return references.contains(this);
    }
}
