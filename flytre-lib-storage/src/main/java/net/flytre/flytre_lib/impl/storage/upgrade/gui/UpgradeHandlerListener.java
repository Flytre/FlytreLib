package net.flytre.flytre_lib.impl.storage.upgrade.gui;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.item.ItemStack;

public interface UpgradeHandlerListener {

    void onUpgradeSlotUpdate(UpgradeHandler handler, int slotId, ItemStack stack);
}
