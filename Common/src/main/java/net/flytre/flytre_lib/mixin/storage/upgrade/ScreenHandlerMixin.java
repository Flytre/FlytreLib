package net.flytre.flytre_lib.mixin.storage.upgrade;


import com.google.common.base.Suppliers;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeSlot;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerListener;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerSyncHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Implements UpgradeHandler onto every Screen Handler
 */
@Mixin(ScreenHandler.class)
abstract class ScreenHandlerMixin implements UpgradeHandler {


    @Unique
    public DefaultedList<UpgradeSlot> upgradeSlots = DefaultedList.of();
    @Shadow
    @Final
    public DefaultedList<Slot> slots;
    @Unique
    private DefaultedList<ItemStack> trackedUpgradeStacks = DefaultedList.of();
    @Unique
    private DefaultedList<ItemStack> previousTrackedUpgradeStacks = DefaultedList.of();
    @Unique
    private @Nullable UpgradeHandlerSyncHandler upgradeSyncHandler;
    @Shadow
    @Final
    private List<ScreenHandlerListener> listeners;
    @Shadow
    @Final
    @Nullable
    private ScreenHandlerType<?> type;
    @Shadow
    private boolean disableSync;

    @Shadow
    public abstract void syncState();

    @Shadow
    public abstract ItemStack getCursorStack();

    @Shadow
    public abstract void setCursorStack(ItemStack stack);

    @Shadow
    public abstract void sendContentUpdates();

    @Shadow
    protected abstract boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

    @Shadow protected abstract Slot addSlot(Slot slot);

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$init(ScreenHandlerType<?> type, int syncId, CallbackInfo ci) {
        upgradeSlots = DefaultedList.of();
        trackedUpgradeStacks = DefaultedList.of();
        previousTrackedUpgradeStacks = DefaultedList.of();
    }

    @Override
    public UpgradeSlot addSlot(UpgradeSlot slot) {
        slot.id = this.upgradeSlots.size();
        this.upgradeSlots.add(slot);
        this.trackedUpgradeStacks.add(ItemStack.EMPTY);
        this.previousTrackedUpgradeStacks.add(ItemStack.EMPTY);
        return slot;
    }

    @Override
    public DefaultedList<ItemStack> getUpgradeStacks() {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();

        for (UpgradeSlot upgradeSlot : this.upgradeSlots)
            defaultedList.add(upgradeSlot.getStack());

        return defaultedList;
    }

    @Override
    public void updateSyncHandler(UpgradeHandlerSyncHandler handler) {
        this.upgradeSyncHandler = handler;
        this.syncState();
    }

    @Inject(method = "syncState", at = @At("HEAD"))
    public void flytre_lib$syncUpgradeState(CallbackInfo ci) {
        for (int k = 0, l = this.upgradeSlots.size(); k < l; ++k)
            this.previousTrackedUpgradeStacks.set(k, this.upgradeSlots.get(k).getStack().copy());

        if (this.upgradeSyncHandler != null) {
            this.upgradeSyncHandler.updateUpgradeState(this, this.previousTrackedUpgradeStacks);
        }
    }

    @Inject(method = "sendContentUpdates", at = @At("RETURN"))
    public void flytre_lib$sendUpgradeContentUpdates(CallbackInfo ci) {
        for (int j = 0; j < this.upgradeSlots.size(); ++j) {
            ItemStack itemStack = this.upgradeSlots.get(j).getStack();
            Objects.requireNonNull(itemStack);
            Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
            this.updateUpgradeTrackedSlot(j, itemStack, supplier);
            this.checkUpgradeSlotUpdates(j, itemStack, supplier);
        }
    }

    private void updateUpgradeTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copyMaker) {
        ItemStack itemStack = this.trackedUpgradeStacks.get(slot);
        if (!ItemStack.areEqual(itemStack, stack)) {
            ItemStack itemStack2 = copyMaker.get();
            this.trackedUpgradeStacks.set(slot, itemStack2);

            for (var listener : listeners)
                if (listener instanceof UpgradeHandlerListener)
                    ((UpgradeHandlerListener) listener).onUpgradeSlotUpdate(this, slot, itemStack2);
        }

    }

    private void checkUpgradeSlotUpdates(int slot, ItemStack stack, Supplier<ItemStack> copyMaker) {
        if (!disableSync) {
            ItemStack itemStack = this.previousTrackedUpgradeStacks.get(slot);
            if (!ItemStack.areEqual(itemStack, stack)) {
                ItemStack fluidStack2 = copyMaker.get();
                this.previousTrackedUpgradeStacks.set(slot, fluidStack2);
                if (this.upgradeSyncHandler != null) {
                    this.upgradeSyncHandler.updateSlot(this, slot, fluidStack2);
                }
            }

        }
    }

    @Override
    public void setPreviousTrackedUpgradeSlot(int slot, ItemStack stack) {
        this.previousTrackedUpgradeStacks.set(slot, stack);
    }


    @Override
    public UpgradeSlot getUpgradeSlot(int index) {
        return this.upgradeSlots.get(index);
    }

    @Override
    public void setUpgradeStackInSlot(int slot, ItemStack stack) {
        this.getUpgradeSlot(slot).setStack(stack);
    }


    @Override
    public void updateUpgradeSlotStacks(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getUpgradeSlot(i).setStack(stacks.get(i));
        }

    }

    @Override
    public void onUpgradeSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        try {
            this.upgradeSlotClickHelper(slotId, clickData, actionType, playerEntity);
        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Mechanix Upgrade Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> this.type != null ? Objects.requireNonNull(Registry.SCREEN_HANDLER.getId(this.type)).toString() : "<no type>");
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Upgrade Slot Count", this.upgradeSlots.size());
            crashReportSection.add("Slot", slotId);
            crashReportSection.add("Button", clickData);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    private void upgradeSlotClickHelper(int slotId, int clickData, SlotActionType slotActionType, PlayerEntity playerEntity) {
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
                        if (currentSlot.hasStack() && UpgradeHandler.canInsertItemIntoSlot(currentSlot, stack, true) && currentSlot.canTakeItems(playerEntity)) {
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

    @Override
    public void addStandardUpgradeSlots(UpgradeInventory entity) {
        this.addSlot(new UpgradeSlot(entity, 0, 191, 84));
        this.addSlot(new UpgradeSlot(entity, 1, 211, 84));
        this.addSlot(new UpgradeSlot(entity, 2, 191, 104));
        this.addSlot(new UpgradeSlot(entity, 3, 211, 104));
    }


    @Override
    public DefaultedList<UpgradeSlot> getUpgradeSlots() {
        return upgradeSlots;
    }


    /**
     * @return the remainder of the stack after the transfer. empty = success
     */
    @Override
    public ItemStack transferUpgradeSlot(PlayerEntity player, int index) {
        UpgradeSlot slot = this.upgradeSlots.get(index);
        ItemStack stack = ItemStack.EMPTY;
        if (slot.hasStack()) {
            stack = slot.getStack();

            int minIndex = 0;
            while (minIndex < slots.size() && !slots.get(minIndex).inventory.equals(player.getInventory()))
                minIndex++;

            int maxIndex = minIndex;
            while (maxIndex < slots.size() && slots.get(maxIndex).inventory.equals(player.getInventory()))
                maxIndex++;

            if (!this.insertItem(stack, minIndex, maxIndex, false))
                return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void addInventorySlots(PlayerInventory playerInventory) {
        int o;
        int n;
        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for (o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }
    }


    @Override
    public ScreenHandler get() {
        return (ScreenHandler) (Object) this;
    }

}
