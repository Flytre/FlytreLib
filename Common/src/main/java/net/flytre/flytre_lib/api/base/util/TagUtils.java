package net.flytre.flytre_lib.api.base.util;

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * More just to remember how to get the values of a tag than anything
 */
public final class TagUtils {
    private TagUtils() {
        throw new AssertionError();
    }

    public static <T> List<T> getKeyValuesAsList(@NotNull DynamicRegistryManager manager, TagKey<T> tagKey) {
        return getKeyValuesAsList(manager.get(tagKey.registry()), tagKey);
    }

    public static <T> Set<T> getKeyValuesAsSet(@NotNull DynamicRegistryManager manager, TagKey<T> tagKey) {
        return getKeyValuesAsSet(manager.get(tagKey.registry()), tagKey);
    }

    private static <T> Stream<T> getKeyValues(Registry<T> registry, TagKey<T> tagKey) {
        var entries = registry.getEntryList(tagKey);
        if (entries.isEmpty())
            throw new AssertionError("Registry Entry List not found for Tag Key " + tagKey.id());
        RegistryEntryList.Named<T> named = entries.get();

        return named.stream().map(RegistryEntry::value);
    }

    public static <T> List<T> getKeyValuesAsList(Registry<T> registry, TagKey<T> tagKey) {
        return getKeyValues(registry,tagKey).toList();
    }

    public static <T> Set<T> getKeyValuesAsSet(Registry<T> registry, TagKey<T> tagKey) {
        return getKeyValues(registry,tagKey).collect(Collectors.toSet());
    }
}
