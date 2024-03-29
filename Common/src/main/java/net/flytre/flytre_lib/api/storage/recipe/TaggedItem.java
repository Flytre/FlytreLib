package net.flytre.flytre_lib.api.storage.recipe;

import net.flytre.flytre_lib.api.base.util.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

/**
 * A tagged item refers to an item stored in a tag;
 * Which item in the specified tag is subject to change
 */
public final class TaggedItem {
    private final Identifier path;
    private final int qty;


    public TaggedItem(Identifier path) {
        this(path, 1);
    }

    public TaggedItem(Identifier path, int qty) {
        this.path = path;
        this.qty = qty;
    }

    public Item getItem() {
        List<Item> values = TagUtils.getKeyValuesAsList(Registry.ITEM, TagKey.of(Registry.ITEM.getKey(), path));
        return values.size() >= 1 ? values.get(0) : Items.AIR;
    }

    public ItemStack getItemStack() {
        return new ItemStack(getItem(), qty);
    }

    public int getQty() {
        return qty;
    }

    public Identifier getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "TaggedItem{" +
                "path=" + path +
                ", qty=" + qty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaggedItem that = (TaggedItem) o;

        if (qty != that.qty) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + qty;
        return result;
    }
}
