package net.flytre.flytre_lib.api.storage.recipe;

import net.flytre.flytre_lib.api.base.util.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;

public class TaggedItem {
    private final Identifier path;
    private final int qty;

    public TaggedItem(Identifier path) {
        this(path,1);
    }

    public TaggedItem(Identifier path, int qty) {
        this.path = path;
        this.qty = qty;
    }

    public Item getItem() {
        List<Item> values = TagUtils.getValues(path, ItemTags.getTagGroup());
        return values.size() >= 1 ? values.get(0) : Items.AIR;
    }

    public ItemStack getItemStack() {
        return new ItemStack(getItem(),qty);
    }

    public int getQty() {
        return qty;
    }

    public Identifier getPath() {
        return path;
    }
}
