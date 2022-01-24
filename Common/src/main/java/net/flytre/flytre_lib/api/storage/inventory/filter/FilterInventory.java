package net.flytre.flytre_lib.api.storage.inventory.filter;

import net.flytre.flytre_lib.api.base.util.Formatter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A filtered inventory - blacklist and whitelist options!
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

    public static FilterInventory readNbt(NbtCompound tag, int defaultHeight) {
        int height = tag.contains("height") ? tag.getInt("height") : defaultHeight;
        int filterType = tag.getInt("type");
        boolean matchNbt = tag.getBoolean("nbtMatch");
        boolean matchMod = tag.getBoolean("modMatch");
        DefaultedList<ItemStack> items = DefaultedList.ofSize(height * 9, ItemStack.EMPTY);
        Inventories.readNbt(tag, items);
        return new FilterInventory(items, filterType, height, matchNbt, matchMod);
    }

    public static void appendFilterToTooltip(FilterInventory inv, List<Text> tooltip) {

        Style itemStyle = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        Style modStyle = Style.EMPTY.withColor(Formatting.BLUE).withItalic(true);

        int len = 0;
        int sz;
        if (!inv.matchMod) {
            Set<Item> filter = inv.getFilterItems();
            sz = filter.size();
            for (Item i : filter) {
                tooltip.add((new TranslatableText(i.getTranslationKey())).setStyle(itemStyle));
                if (++len > 8)
                    break;
            }
        } else {
            Set<String> mods = inv.items.stream().map(i -> Formatter.getModFromModId(Registry.ITEM.getId(i.getItem()).getNamespace())).collect(Collectors.toSet());
            sz = mods.size();
            for (String mod : mods) {
                tooltip.add((new LiteralText(mod)).setStyle(itemStyle));
                if (++len > 8)
                    break;
            }
        }

        if (len >= 9) {
            tooltip.add((new LiteralText("§7§o"))
                    .append(new TranslatableText("flytre_lib.filter.tooltip", sz - 9).setStyle(itemStyle)));
        }


        Style red_text = Style.EMPTY.withColor(Formatting.RED);
        Style green_text = Style.EMPTY.withColor(Formatting.GREEN);
        MutableText whitelist = new TranslatableText("flytre_lib.filter.whitelist").setStyle(green_text);
        MutableText blacklist = new TranslatableText("flytre_lib.filter.blacklist").setStyle(red_text);

        MutableText matchMod = new TranslatableText("flytre_lib.filter.mod_match.true").setStyle(green_text);
        MutableText ignoreMod = new TranslatableText("flytre_lib.filter.mod_match.false").setStyle(red_text);

        MutableText matchNbt = new TranslatableText("flytre_lib.filter.nbt_match.true").setStyle(green_text);
        MutableText ignoreNbt = new TranslatableText("flytre_lib.filter.nbt_match.false").setStyle(red_text);


        tooltip.add(new LiteralText("").append(inv.getFilterType() == 0 ? whitelist : blacklist));
        tooltip.add(new LiteralText("").append(inv.isMatchMod() ? matchMod : ignoreMod));
        tooltip.add(new LiteralText("").append(inv.isMatchNbt() ? matchNbt : ignoreNbt));
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    public int getInventoryWidth() {
        return 9;
    }

    public int getInventoryHeight() {
        return height;
    }

    @Override
    public int size() {
        return getInventoryWidth() * getInventoryHeight();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean put(ItemStack stack) {

        ItemStack copy = stack.copy();
        copy.setCount(1);

        if (!matchNbt && this.containsAny(Collections.singleton(stack.getItem())))
            return false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, copy);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stack.setCount(1);
        this.items.set(slot, stack);
    }

    @Override
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
            String stackMod = Registry.ITEM.getId(stack.getItem()).getNamespace();

            if (matchNbt) {
                return (filterType == 0) == items.stream().anyMatch(test -> stackMod.equals(Registry.ITEM.getId(test.getItem()).getNamespace()) && ItemStack.areNbtEqual(test, stack));
            } else {
                return (filterType == 0) == filterItems.stream().anyMatch(test -> stackMod.equals(Registry.ITEM.getId(test).getNamespace()));
            }
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
            if (test.getItem() == stack.getItem() && ItemStack.areNbtEqual(test, stack))
                return true;
        }

        return false;

    }

    @Override
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

    public void toggleNbtMatch() {
        this.matchNbt = !this.matchNbt;
    }

    public void toggleModMatch() {
        this.matchMod = !this.matchMod;
    }

    public void toggleFilterType() {
        this.filterType = (this.filterType == 1 ? 0 : 1);
    }

    public NbtCompound writeNbt() {
        NbtCompound tag = new NbtCompound();
        Inventories.writeNbt(tag, this.items);
        tag.putInt("type", this.filterType);
        tag.putInt("height", this.height);
        tag.putBoolean("nbtMatch", this.matchNbt);
        tag.putBoolean("modMatch", this.matchMod);
        return tag;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    @Override
    public void onClose(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.PLAYERS, 1f, 1f);
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

}
