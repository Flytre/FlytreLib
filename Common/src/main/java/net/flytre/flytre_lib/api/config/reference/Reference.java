package net.flytre.flytre_lib.api.config.reference;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;


/**
 * References are used to add compatibility (by default) to modded things (i.e. modded items / entities) and also to
 * add support for dynamic registries (i.e. biomes) without using an identifier set
 * <p>
 * Remember that some registries default so please use the identifier constructor if the value could possibly default!
 *  For example, entities default to pigs
 */
public abstract class Reference<T> {

    protected final @NotNull Identifier identifier;

    //lazy-loaded
    protected @Nullable T value;


    /**
     * For modded additions and dynamic values
     */
    protected Reference(@NotNull Identifier identifier) {
        this.identifier = identifier;
        this.value = null;
    }

    protected Reference(String namespace, String path) {
        this(new Identifier(namespace, path));
    }


    protected Reference(@NotNull Identifier id, @Nullable T value) {
        this.identifier = id;
        this.value = value;
    }

    /**
     * For those registries without dynamic values
     */
    protected Reference(@NotNull T value, Registry<T> registry) {
        this.identifier = Objects.requireNonNull(registry.getId(value));
        this.value = value;
    }


    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    protected @Nullable T getValue(RegistryKey<? extends Registry<? extends T>> key, World world) {
        return value != null ? value : (world.getRegistryManager().get(key).getOrEmpty(identifier).orElse(null));
    }


    public boolean hasValue(World world) {
        return getValue(world) != null;
    }

    public abstract @Nullable T getValue(World world);

    public boolean isIn(Collection<? extends Reference<T>> references) {
        return references.contains(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference<?> reference = (Reference<?>) o;

        return identifier.equals(reference.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "identifier=" + identifier +
                ", value=" + value +
                '}';
    }
}
