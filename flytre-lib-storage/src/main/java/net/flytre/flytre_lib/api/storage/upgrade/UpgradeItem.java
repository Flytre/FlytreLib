package net.flytre.flytre_lib.api.storage.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface UpgradeItem {

    boolean isValid(UpgradeInventory inv, ItemStack stack, int slot);

    Item get();
}
