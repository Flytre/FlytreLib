package net.flytre.flytre_lib.api.storage.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * An output slot cannot have items manually added to it, only removed
 */
public class OutputSlot extends Slot {


    public OutputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public boolean canInsert(ItemStack stack) {
        return false;
    }

}
