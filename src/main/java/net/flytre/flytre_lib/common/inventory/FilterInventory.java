package net.flytre.flytre_lib.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/*
A filtered inventory - blacklist and whitelist options!
 */
public class FilterInventory implements Inventory {

    private final int height;
    public DefaultedList<ItemStack> items;
    private boolean matchNbt;
    private boolean matchMod;
    private int filterType;

    public FilterInventory(DefaultedList<ItemStack> items, int filterType, int height, boolean matchNbt, boolean matchMod) {
        this.items = items;
        this.filterType = filterType;
        this.height = height;
        this.matchNbt = matchNbt;
        this.matchMod = matchMod;
    }

    public static FilterInventory fromTag(CompoundTag tag, int defaultHeight) {
        int height = tag.contains("height") ? tag.getInt("height") : defaultHeight;
        int filterType = tag.getInt("type");
        boolean matchNbt = tag.getBoolean("nbtMatch");
        boolean matchMod = tag.getBoolean("modMatch");
        DefaultedList<ItemStack> items = DefaultedList.ofSize(height * 9, ItemStack.EMPTY);
        Inventories.fromTag(tag, items);
        return new FilterInventory(items, filterType, height, matchNbt, matchMod);
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public void clear() {
        this.items.clear();
    }

    public int getInventoryWidth() {
        return 9;
    }

    public int getInventoryHeight() {
        return height;
    }

    public int size() {
        return getInventoryWidth() * getInventoryHeight();
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean put(ItemStack stack) {

        ItemStack copy = stack.copy();
        copy.setCount(1);

        if(!matchNbt && this.containsAny(Collections.singleton(stack.getItem())))
            return false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, copy);
                return true;
            }
        }
        return false;
    }

    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.items, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        stack.setCount(1);
        this.items.set(slot, stack);
    }

    public void markDirty() {
        fixItems();
    }

    private void fixItems() {
        DefaultedList<ItemStack> fixed = DefaultedList.ofSize(this.items.size(), ItemStack.EMPTY);
        int index = 0;
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                fixed.set(index++, stack);
            }
        }
        this.items = fixed;
    }

    public Set<Item> getFilterItems() {
        HashSet<Item> res = new HashSet<>();
        for (ItemStack item : items)
            res.add(item.getItem());
        res.remove(Items.AIR);
        return res;
    }

    public boolean passFilterTest(ItemStack stack) {
        Set<Item> filterItems = getFilterItems();

        if (matchMod) {
            Set<String> mods = filterItems.stream().map(Registry.ITEM::getId).map(Identifier::getNamespace).collect(Collectors.toSet());
            String itemId = Registry.ITEM.getId(stack.getItem()).getNamespace();
            return (filterType == 0) == mods.contains(itemId);
        }

        boolean bl = (filterType == 0) == filterItems.contains(stack.getItem());
        if (!matchNbt) {
            return bl;
        }

        //match nbt and item
        if (!bl)
            return false;

        Set<ItemStack> stacks = items.stream().filter(i -> !i.isEmpty()).collect(Collectors.toSet());
        for (ItemStack test : stacks) {
            if (test.getItem() == stack.getItem() && ItemStack.areTagsEqual(test, stack))
                return true;
        }

        return false;

    }

    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public boolean isMatchNbt() {
        return matchNbt;
    }

    public void setMatchNbt(boolean matchNbt) {
        this.matchNbt = matchNbt;
    }

    public boolean isMatchMod() {
        return matchMod;
    }

    public void setMatchMod(boolean matchMod) {
        this.matchMod = matchMod;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        Inventories.toTag(tag, this.items);
        tag.putInt("type", this.filterType);
        tag.putInt("height", this.height);
        tag.putBoolean("nbtMatch", this.matchNbt);
        tag.putBoolean("modMatch", this.matchMod);
        return tag;
    }

    public void onOpen(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void onClose(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.PLAYERS, 1f, 1f);
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }


}
