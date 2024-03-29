package net.flytre.flytre_lib.api.storage.upgrade;


import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerSyncHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Implemented by ScreenHandler.
 * Cast to this class to invoke upgrade related methods, i.e. adding upgrade slots
 * to a screen handler
 */
public interface UpgradeHandler {

    static boolean canInsertItemIntoSlot(@Nullable UpgradeSlot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl = slot == null || !slot.hasStack();
        if (!bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areNbtEqual(slot.getStack(), stack)) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        } else {
            return bl;
        }
    }

    UpgradeSlot addSlot(UpgradeSlot slot);

    DefaultedList<ItemStack> getUpgradeStacks();


    @ApiStatus.Internal
    void updateSyncHandler(UpgradeHandlerSyncHandler handler);


    void setPreviousTrackedUpgradeSlot(int slot, ItemStack stack);


    UpgradeSlot getUpgradeSlot(int index);

    void setUpgradeStackInSlot(int slot, ItemStack stack);


    void updateUpgradeSlotStacks(List<ItemStack> stacks);

    void onUpgradeSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity);

    ItemStack transferUpgradeSlot(PlayerEntity player, int index);

    void addStandardUpgradeSlots(UpgradeInventory entity);

    DefaultedList<UpgradeSlot> getUpgradeSlots();

    void addInventorySlots(PlayerInventory playerInventory);

    ScreenHandler get();

}
