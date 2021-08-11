package net.flytre.flytre_lib.impl.storage.upgrade.gui;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface UpgradeHandlerListener {

    void onUpgradeSlotUpdate(UpgradeHandler handler, int slotId, ItemStack stack);
}
