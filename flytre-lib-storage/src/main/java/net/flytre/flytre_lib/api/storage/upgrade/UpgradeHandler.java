package net.flytre.flytre_lib.api.storage.upgrade;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerListener;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerSyncHandler;
import net.flytre.flytre_lib.mixin.storage.upgrade.ScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class UpgradeHandler extends FluidHandler {

    public final DefaultedList<UpgradeSlot> upgradeSlots = DefaultedList.of();
    private final DefaultedList<ItemStack> trackedUpgradeStacks = DefaultedList.of();
    private final DefaultedList<ItemStack> previousTrackedUpgradeStacks;
    private final ScreenHandlerType<?> type;
    private @Nullable UpgradeHandlerSyncHandler syncHandler;


    protected UpgradeHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
        this.previousTrackedUpgradeStacks = DefaultedList.of();
        this.type = type;
    }

    public static boolean canInsertItemIntoSlot(@Nullable UpgradeSlot slot, ItemStack stack, boolean allowOverflow) {
        boolean bl = slot == null || !slot.hasStack();
        if (!bl && stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areNbtEqual(slot.getStack(), stack)) {
            return slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount();
        } else {
            return bl;
        }
    }

    protected UpgradeSlot addSlot(UpgradeSlot slot) {
        slot.id = this.upgradeSlots.size();
        this.upgradeSlots.add(slot);
        this.trackedUpgradeStacks.add(ItemStack.EMPTY);
        this.previousTrackedUpgradeStacks.add(ItemStack.EMPTY);
        return slot;
    }

    public DefaultedList<ItemStack> getUpgradeStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();

        for (UpgradeSlot upgradeSlot : this.upgradeSlots)
            defaultedList.add(upgradeSlot.getStack());

        return defaultedList;
    }

    private List<ScreenHandlerListener> listeners() {
        return ((ScreenHandlerAccessor) this).getListeners();
    }


    public void updateSyncHandler(UpgradeHandlerSyncHandler handler) {
        this.syncHandler = handler;
        this.syncState();
    }

    @Override
    public void syncState() {
        for (int k = 0, l = this.upgradeSlots.size(); k < l; ++k)
            this.previousTrackedUpgradeStacks.set(k, this.upgradeSlots.get(k).getStack().copy());

        if (this.syncHandler != null) {
            this.syncHandler.updateUpgradeState(this, this.previousTrackedUpgradeStacks);
        }

        super.syncState();
    }

    @Override
    public void sendContentUpdates() {

        super.sendContentUpdates();

        for (int j = 0; j < this.upgradeSlots.size(); ++j) {
            ItemStack itemStack = this.upgradeSlots.get(j).getStack();
            Objects.requireNonNull(itemStack);
            Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
            this.updateTrackedSlot(j, itemStack, supplier);
            this.checkSlotUpdates(j, itemStack, supplier);
        }


    }

    private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copyMaker) {
        ItemStack itemStack = this.trackedUpgradeStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copyMaker.get();
            this.trackedUpgradeStacks.set(slot, itemStack2);

            for (var listener : this.listeners())
                if (listener instanceof UpgradeHandlerListener)
                    ((UpgradeHandlerListener) listener).onUpgradeSlotUpdate(this, slot, itemStack2);
        }

    }

    private void checkSlotUpdates(int slot, ItemStack stack, Supplier<ItemStack> copyMaker) {
        if (!((ScreenHandlerAccessor) this).isSyncingDisabled()) {
            ItemStack itemStack = this.previousTrackedUpgradeStacks.get(slot);
            if (!ItemStack.areEqual(itemStack, stack)) {
                ItemStack fluidStack2 = copyMaker.get();
                this.previousTrackedUpgradeStacks.set(slot, fluidStack2);
                if (this.syncHandler != null) {
                    this.syncHandler.updateSlot(this, slot, fluidStack2);
                }
            }

        }
    }


    public void setPreviousTrackedUpgradeSlot(int slot, ItemStack stack) {
        this.previousTrackedUpgradeStacks.set(slot, stack);
    }

    public UpgradeSlot getUpgradeSlot(int index) {
        return this.upgradeSlots.get(index);
    }

    public void setUpgradeStackInSlot(int slot, ItemStack stack) {
        this.getUpgradeSlot(slot).setStack(stack);
    }

    @Environment(EnvType.CLIENT)
    public void updateUpgradeSlotStacks(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getUpgradeSlot(i).setStack(stacks.get(i));
        }

    }


    public void onUpgradeSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        try {
            this.slotClickHelper(slotId, clickData, actionType, playerEntity);
        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Mechanix Upgrade Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Objects.requireNonNull(Registry.SCREEN_HANDLER.getId(this.type)).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotId);
            crashReportSection.add("Button", clickData);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    public ItemStack transferUpgradeSlot(PlayerEntity player, int index) {
        UpgradeSlot slot = this.upgradeSlots.get(index);
        ItemStack stack = ItemStack.EMPTY;
        if (slot.hasStack()) {
            stack = slot.getStack();
            if (!this.insertItem(stack, 0, slots.size(), false))
                return ItemStack.EMPTY;
        }
        return stack;
    }


    private void slotClickHelper(int slotId, int clickData, SlotActionType slotActionType, PlayerEntity playerEntity) {
        PlayerInventory playerInventory = playerEntity.getInventory();
        ItemStack stack;
        ItemStack stack2;
        int n;
        int l;
        UpgradeSlot slot;
        int q;
        if ((slotActionType == SlotActionType.PICKUP || slotActionType == SlotActionType.QUICK_MOVE) && (clickData == 0 || clickData == 1)) {
            if (slotId == -999) {
                if (!getCursorStack().isEmpty()) {
                    if (clickData == 0) {
                        playerEntity.dropItem(getCursorStack(), true);
                        setCursorStack(ItemStack.EMPTY);
                    }

                    if (clickData == 1) {
                        playerEntity.dropItem(getCursorStack().split(1), true);
                    }
                }
            } else if (slotActionType == SlotActionType.QUICK_MOVE) {
                if (slotId < 0) {
                    return;
                }

                slot = this.upgradeSlots.get(slotId);
                if (!slot.canTakeItems(playerEntity)) {
                    return;
                }

                for (stack = this.transferUpgradeSlot(playerEntity, slotId); !stack.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot.getStack(), stack); stack = this.transferUpgradeSlot(playerEntity, slotId))
                    ;
            } else {
                if (slotId < 0) {
                    return;
                }

                slot = this.upgradeSlots.get(slotId);
                stack = slot.getStack();
                stack2 = getCursorStack();
                if (stack.isEmpty()) {
                    q = clickData == 0 ? stack2.getCount() : 1;
                    if (!stack2.isEmpty() && slot.canInsert(stack2, q)) {
                        if (q > slot.getMaxItemCount(stack2)) {
                            q = slot.getMaxItemCount(stack2);
                        }

                        slot.setStack(stack2.split(q));
                    }
                } else if (slot.canTakeItems(playerEntity)) {
                    if (stack2.isEmpty()) {
                        if (stack.isEmpty()) {
                            slot.setStack(ItemStack.EMPTY);
                            setCursorStack(ItemStack.EMPTY);
                        } else {
                            q = clickData == 0 ? stack.getCount() : (stack.getCount() + 1) / 2;
                            setCursorStack(slot.takeStack(q));
                            if (stack.isEmpty()) {
                                slot.setStack(ItemStack.EMPTY);
                            }

                            slot.onTakeItem(playerEntity, getCursorStack());
                        }
                    } else if (slot.canInsert(stack2, clickData == 0 ? stack2.getCount() : 1)) {
                        if (InventoryUtils.canMergeItems(stack, stack2)) {
                            q = clickData == 0 ? stack2.getCount() : 1;
                            if (q > slot.getMaxItemCount(stack2) - stack.getCount()) {
                                q = slot.getMaxItemCount(stack2) - stack.getCount();
                            }

                            if (q > stack2.getMaxCount() - stack.getCount()) {
                                q = stack2.getMaxCount() - stack.getCount();
                            }

                            stack2.decrement(q);
                            stack.increment(q);
                        } else if (stack2.getCount() <= slot.getMaxItemCount(stack2)) {
                            slot.setStack(stack2);
                            setCursorStack(stack);
                        }
                    } else if (stack2.getMaxCount() > 1 && InventoryUtils.canMergeItems(stack, stack2) && !stack.isEmpty()) {
                        q = stack.getCount();
                        if (q + stack2.getCount() <= stack2.getMaxCount()) {
                            stack2.increment(q);
                            stack = slot.takeStack(q);
                            if (stack.isEmpty()) {
                                slot.setStack(ItemStack.EMPTY);
                            }

                            slot.onTakeItem(playerEntity, getCursorStack());
                        }
                    }
                }

                slot.markDirty();
            }
        } else if (slotActionType == SlotActionType.SWAP) {
            slot = this.upgradeSlots.get(slotId);
            stack = playerInventory.getStack(clickData);
            stack2 = slot.getStack();
            if (!stack.isEmpty() || !stack2.isEmpty()) {
                if (stack.isEmpty()) {
                    if (slot.canTakeItems(playerEntity)) {
                        playerInventory.setStack(clickData, stack2);
//                        slot4.onTake(itemStack8.getCount());
                        slot.setStack(ItemStack.EMPTY);
                        slot.onTakeItem(playerEntity, stack2);
                    }
                } else if (stack2.isEmpty()) {
                    if (slot.canInsert(stack)) {
                        q = slot.getMaxItemCount(stack);
                        if (stack.getCount() > q) {
                            slot.setStack(stack.split(q));
                        } else {
                            slot.setStack(stack);
                            playerInventory.setStack(clickData, ItemStack.EMPTY);
                        }
                    }
                } else if (slot.canTakeItems(playerEntity) && slot.canInsert(stack)) {
                    q = slot.getMaxItemCount(stack);
                    if (stack.getCount() > q) {
                        slot.setStack(stack.split(q));
                        slot.onTakeItem(playerEntity, stack2);
                        if (!playerInventory.insertStack(stack2)) {
                            playerEntity.dropItem(stack2, true);
                        }
                    } else {
                        slot.setStack(stack);
                        playerInventory.setStack(clickData, stack2);
                        slot.onTakeItem(playerEntity, stack2);
                    }
                }
            }
        } else if (slotActionType == SlotActionType.CLONE && playerEntity.getAbilities().creativeMode && getCursorStack().isEmpty() && slotId >= 0) {
            slot = this.upgradeSlots.get(slotId);
            if (slot.hasStack()) {
                stack = slot.getStack().copy();
                stack.setCount(stack.getMaxCount());
                setCursorStack(stack);
            }
        } else if (slotActionType == SlotActionType.THROW && getCursorStack().isEmpty() && slotId >= 0) {
            slot = this.upgradeSlots.get(slotId);
            if (slot.hasStack() && slot.canTakeItems(playerEntity)) {
                stack = slot.takeStack(clickData == 0 ? 1 : slot.getStack().getCount());
                slot.onTakeItem(playerEntity, stack);
                playerEntity.dropItem(stack, true);
            }
        } else if (slotActionType == SlotActionType.PICKUP_ALL && slotId >= 0) {
            slot = this.upgradeSlots.get(slotId);
            stack = getCursorStack();
            if (!stack.isEmpty() && (!slot.hasStack() || !slot.canTakeItems(playerEntity))) {
                l = clickData == 0 ? 0 : this.upgradeSlots.size() - 1;
                q = clickData == 0 ? 1 : -1;

                for (int w = 0; w < 2; ++w) {
                    for (int x = l; x >= 0 && x < this.upgradeSlots.size() && stack.getCount() < stack.getMaxCount(); x += q) {
                        UpgradeSlot currentSlot = this.upgradeSlots.get(x);
                        if (currentSlot.hasStack() && canInsertItemIntoSlot(currentSlot, stack, true) && currentSlot.canTakeItems(playerEntity)) {
                            ItemStack currentStack = currentSlot.getStack();
                            if (w != 0 || currentStack.getCount() != currentStack.getMaxCount()) {
                                n = Math.min(stack.getMaxCount() - stack.getCount(), currentStack.getCount());
                                ItemStack taken = currentSlot.takeStack(n);
                                stack.increment(n);
                                if (taken.isEmpty()) {
                                    currentSlot.setStack(ItemStack.EMPTY);
                                }

                                currentSlot.onTakeItem(playerEntity, taken);
                            }
                        }
                    }
                }
            }

            this.sendContentUpdates();
        }

    }

    protected void addStandardUpgradeSlots(UpgradeInventory entity) {
        this.addSlot(new UpgradeSlot(entity, 0, 191, 84));
        this.addSlot(new UpgradeSlot(entity, 1, 211, 84));
        this.addSlot(new UpgradeSlot(entity, 2, 191, 104));
        this.addSlot(new UpgradeSlot(entity, 3, 211, 104));
    }


}
