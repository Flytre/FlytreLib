package net.flytre.flytre_lib.api.storage.upgrade;

import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * A "second" inventory that is used to handle upgrades, i.e. hopper upgrades or mechanix machine
 * upgrades.
 * Make sure to call toTag and fromTag when you implement this
 */
public interface UpgradeInventory {

    static NbtCompound toTag(NbtCompound tag, DefaultedList<ItemStack> stacks) {
        return InventoryUtils.writeNbt(tag, stacks, "Upgrades");
    }

    static NbtCompound fromTag(NbtCompound tag, DefaultedList<ItemStack> stacks) {
        return InventoryUtils.readNbt(tag, stacks, "Upgrades");
    }

    DefaultedList<ItemStack> getUpgrades();

    default int upgradeSlots() {
        return getUpgrades().size();
    }

    default boolean hasNoUpgrades() {
        for (int i = 0; i < upgradeSlots(); i++) {
            ItemStack stack = getUpgrade(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    default ItemStack getUpgrade(int slot) {
        return getUpgrades().get(slot);
    }

    default ItemStack removeUpgrade(int slot, int amount) {
        ItemStack result = Inventories.splitStack(getUpgrades(), slot, amount);
        if (!result.isEmpty()) {
            markUpgradesDirty();
        }
        return result;
    }

    default ItemStack removeUpgrade(int slot) {
        ItemStack stack = Inventories.removeStack(getUpgrades(), slot);
        markUpgradesDirty();
        return stack;
    }

    default void setUpgrade(int slot, ItemStack stack) {
        getUpgrades().set(slot, stack);
        if (stack.getCount() > getMaxUpgradeSlotCount()) {
            stack.setCount(getMaxUpgradeSlotCount());
        }
        markUpgradesDirty();
    }

    default void clearUpgrades() {
        getUpgrades().clear();
    }

    default int[] getAvailableUpgradeSlots() {
        return IntStream.range(0, upgradeSlots()).toArray();
    }

    default int upgradeQuantity(Item item) {
        int i = 0;

        for (int j = 0; j < this.upgradeSlots(); ++j) {
            ItemStack itemStack = this.getUpgrade(j);
            if (itemStack.getItem().equals(item)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    default boolean hasUpgrade(Item upgrade) {
        return getUpgrades().stream().anyMatch(i -> i.getItem() == upgrade);
    }

    default int getMaxUpgradeSlotCount() {
        return 64;
    }

    default void markUpgradesDirty() {
    }

    default Set<Item> validUpgrades() {
        return new HashSet<>();
    }

    default boolean isValidUpgrade(ItemStack stack) {
        return validUpgrades().contains(stack.getItem());
    }
}
