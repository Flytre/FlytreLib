package net.flytre.flytre_lib.api.storage.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UpgradeItem {

    /**
     * How many of this upgrade the UpgradeInventory can contain
     */
    default int maxCount() {
        return 1;
    }

    /**
     * Whether the given stack is valid to be inserted into the upgrade inventory
     */
    default boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
        if (incompatibleUpgrades()
                .stream()
                .anyMatch(inv::hasUpgrade)) {
            return false;
        }

        if (maxCount() == 1)
            return !inv.hasUpgrade(get()) && stack.getCount() == 1;

        return inv.upgradeQuantity(get()) + stack.getMaxCount() <= maxCount();
    }

    /**
     * The collection of upgrades that this upgrade is not compatible with
     */
    default Collection<Item> incompatibleUpgrades() {
        return Set.of();
    }

    /**
     * Get this upgrade as an item;
     */
    Item get();
}
