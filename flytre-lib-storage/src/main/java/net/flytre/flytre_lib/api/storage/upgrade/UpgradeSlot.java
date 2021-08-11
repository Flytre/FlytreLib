package net.flytre.flytre_lib.api.storage.upgrade;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class UpgradeSlot extends Slot {
    public final UpgradeInventory upgradeInventory;
    private final int index;

    public UpgradeSlot(UpgradeInventory upgradeInventory, int index, int x, int y) {
        super(new SimpleInventory(9), index, x, y);
        this.upgradeInventory = upgradeInventory;
        this.index = index;
    }

    public boolean canInsert(ItemStack stack) {
        return canInsert(stack, stack.getCount());
    }

    public boolean canInsert(ItemStack stack, int qty) {
        if (!upgradeInventory.isValidUpgrade(stack))
            return false;
        boolean isUpgradeItem = stack.getItem() instanceof UpgradeItem;
        ItemStack copy = stack.copy();
        copy.setCount(qty);

        return !isUpgradeItem || ((UpgradeItem) stack.getItem()).isValid(upgradeInventory, copy, index);
    }


    @Override
    public ItemStack getStack() {
        return this.upgradeInventory.getUpgrade(this.index);
    }

    @Override
    public void setStack(ItemStack stack) {
        this.upgradeInventory.setUpgrade(this.index, stack);
        this.markDirty();
    }

    @Override
    public void markDirty() {
        this.upgradeInventory.markUpgradesDirty();
    }

    @Override
    public int getMaxItemCount() {
        return this.upgradeInventory.getMaxUpgradeSlotCount();
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {

        int ct = getMaxItemCount();

        if (stack.getItem() instanceof UpgradeItem) {
            ct = Math.min(ct, ((UpgradeItem) stack.getItem()).maxCount());
        }
        return ct;
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.upgradeInventory.removeUpgrade(this.index, amount);
    }

}
