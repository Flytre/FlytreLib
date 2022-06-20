package net.flytre.flytre_lib.api.config.reference.item;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


sealed public interface ConfigItem permits ItemReference, ItemTagReference {


    static Set<Item> values(Set<ConfigItem> items, World world) {
        Set<Item> result = new HashSet<>();
        for (ConfigItem item : items) {
            if (item instanceof ItemReference) {
                result.add(((ItemReference) item).getValue(world));
            } else {
                Set<Item> list = ((ItemTagReference) item).getValue(world);
                if (list != null)
                    result.addAll(list);
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
            Set<Item> list = ((ItemTagReference) i).getValue(world);
            return list != null && list.contains(item);
        });
    }

    static Set<ConfigItem> of(Set<Item> values) {
        return values.stream().map(ItemReference::new).collect(Collectors.toSet());
    }
}
