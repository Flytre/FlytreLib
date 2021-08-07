package net.flytre.flytre_lib.api.config.reference.item;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface ConfigItem {


    static Set<Item> values(Set<ConfigItem> items, World world) {
        Set<Item> result = new HashSet<>();
        for (ConfigItem item : items) {
            if (item instanceof ItemReference) {
                result.add(((ItemReference) item).getValue(world));
            } else {
                Tag<Item> list = ((ItemTagReference) item).getValue(world);
                if (list != null)
                    result.addAll(list.values());
            }
        }
        return result;
    }

    /**
     * To be used! O(1) search time for items not in tags, or O(n) for items in tags / not present
     */
    static boolean contains(Set<ConfigItem> items, Item item, World world) {
        if (items.contains(new ItemReference(item)))
            return true;
        return items.stream().anyMatch(i -> {
            if (!(i instanceof ItemTagReference))
                return false;
            Tag<Item> list = ((ItemTagReference) i).getValue(world);
            return list != null && list.values().contains(item);
        });
    }

    static Set<ConfigItem> of(Set<Item> values) {
        return values.stream().map(ItemReference::new).collect(Collectors.toSet());
    }
}
