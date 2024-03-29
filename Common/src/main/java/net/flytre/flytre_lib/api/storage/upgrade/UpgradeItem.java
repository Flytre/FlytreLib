package net.flytre.flytre_lib.api.storage.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Set;

public interface UpgradeItem {

    /**
     * How many of this upgrade the UpgradeInventory can contain
     */
    default int maxCount() {
        return 1;
    }


    /**
     * How many more of this upgrade can be inserted into the given inventory
     */
    default int remainingCount(UpgradeInventory inventory) {
        return Math.max(0, maxCount() - inventory.upgradeQuantity(get()));
    }


    /**
     * Ignoring count, whether this upgrade is valid to be inserted into a slot
     */
    default boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
        return incompatibleUpgrades()
                .stream()
                .noneMatch(inv::hasUpgrade);
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
