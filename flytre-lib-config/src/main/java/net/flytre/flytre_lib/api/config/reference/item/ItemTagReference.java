package net.flytre.flytre_lib.api.config.reference.item;

import net.flytre.flytre_lib.api.config.reference.TagReference;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public class ItemTagReference extends TagReference<Item> implements ConfigItem {
    public ItemTagReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public ItemTagReference(String namespace, String path) {
        super(namespace, path);
    }

    public ItemTagReference(Tag.Identified<Item> tag) {
        super(tag);
    }

    @Override
    public RegistryKey<Registry<Item>> getRegistry() {
        return Registry.ITEM_KEY;
    }
}
