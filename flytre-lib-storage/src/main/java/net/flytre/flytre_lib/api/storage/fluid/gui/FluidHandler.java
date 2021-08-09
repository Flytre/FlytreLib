package net.flytre.flytre_lib.api.storage.fluid.gui;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerListener;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerSyncHandler;;
import net.flytre.flytre_lib.mixin.storage.fluid.BucketItemAccessor;
import net.flytre.flytre_lib.mixin.storage.fluid.ScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Superclass for all ScreenHandlers that need Fluid Slots
 * Use FluidHandler#addSlot
 */
public abstract class FluidHandler extends ScreenHandler {

    public final DefaultedList<FluidSlot> fluidSlots = DefaultedList.of();
    private final DefaultedList<FluidStack> trackedFluidStacks = DefaultedList.of();
    private final DefaultedList<FluidStack> previousTrackedFluidStacks;
    private @Nullable FluidHandlerSyncHandler syncHandler;


    protected FluidHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
        this.previousTrackedFluidStacks = DefaultedList.of();
    }

    protected FluidSlot addSlot(FluidSlot slot) {
        slot.id = this.fluidSlots.size();
        this.fluidSlots.add(slot);
        this.trackedFluidStacks.add(FluidStack.EMPTY);
        this.previousTrackedFluidStacks.add(FluidStack.EMPTY);
        return slot;
    }

    public DefaultedList<FluidStack> getFluidStacks() {
        DefaultedList<FluidStack> defaultedList = DefaultedList.of();

        for (FluidSlot fluidSlot : this.fluidSlots) {
            defaultedList.add(fluidSlot.getStack());
        }
        return defaultedList;
    }

    private List<ScreenHandlerListener> listeners() {
        return ((ScreenHandlerAccessor) this).getListeners();
    }


    @Override
    public void addListener(ScreenHandlerListener listener) {
        if (!this.listeners().contains(listener)) {
            this.listeners().add(listener);
            this.sendContentUpdates();
        }
    }

    public void updateSyncHandler(FluidHandlerSyncHandler handler) {
        this.syncHandler = handler;
        this.syncState();
    }

    @Override
    public void syncState() {
        for (int k = 0, l = this.fluidSlots.size(); k < l; ++k)
            this.previousTrackedFluidStacks.set(k, this.fluidSlots.get(k).getStack().copy());

        if (this.syncHandler != null) {
            this.syncHandler.updateFluidState(this, this.previousTrackedFluidStacks);
        }

        super.syncState();
    }

    public void sendContentUpdates() {

        super.sendContentUpdates();

        for (int j = 0; j < this.fluidSlots.size(); ++j) {
            FluidStack fluidStack = this.fluidSlots.get(j).getStack();
            Objects.requireNonNull(fluidStack);
            Supplier<FluidStack> supplier = Suppliers.memoize(fluidStack::copy);
            this.updateTrackedSlot(j, fluidStack, supplier);
            this.checkSlotUpdates(j, fluidStack, supplier);
        }


    }

    private void updateTrackedSlot(int slot, FluidStack stack, Supplier<FluidStack> copyMaker) {
        FluidStack fluidStack = this.trackedFluidStacks.get(slot);
        if (!FluidStack.areEqual(fluidStack, stack)) {
            FluidStack fluidStack2 = copyMaker.get();
            this.trackedFluidStacks.set(slot, fluidStack2);

            for (var listener : this.listeners())
                if (listener instanceof FluidHandlerListener)
                    ((FluidHandlerListener) listener).onSlotUpdate(this, slot, fluidStack2);
        }

    }

    private void checkSlotUpdates(int slot, FluidStack stack, Supplier<FluidStack> copyMaker) {
        if (!((ScreenHandlerAccessor) this).isSyncingDisabled()) {
            FluidStack fluidStack = this.previousTrackedFluidStacks.get(slot);
            if (!FluidStack.areEqual(fluidStack, stack)) {
                FluidStack fluidStack2 = copyMaker.get();
                this.previousTrackedFluidStacks.set(slot, fluidStack2);
                if (this.syncHandler != null) {
                    this.syncHandler.updateSlot(this, slot, fluidStack2);
                }
            }

        }
    }

    public void setPreviousTrackedSlot(int slot, FluidStack stack) {
        this.previousTrackedFluidStacks.set(slot, stack);
    }


    public FluidSlot getFluidSlot(int index) {
        return this.fluidSlots.get(index);
    }

    public void setFluidStackInSlot(int slot, FluidStack stack) {
        this.getFluidSlot(slot).setStack(stack);
    }

    @Environment(EnvType.CLIENT)
    public void updateFluidSlotStacks(List<FluidStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getFluidSlot(i).setStack(stacks.get(i));
        }

    }

    public FluidStack onFluidSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity playerEntity) {


        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE) {
            //UNSUPPORTED OPERATION
            return FluidStack.EMPTY;
        }


        FluidSlot slot = fluidSlots.get(slotId);
        FluidStack slotStack = slot.getStack();
        ItemStack cursorStack = getCursorStack();

        if (cursorStack.getItem() instanceof BucketItem && ((BucketItemAccessor) cursorStack.getItem()).getFluid() == Fluids.EMPTY) {
            do {
                if (slotStack.getAmount() >= FluidStack.UNITS_PER_BUCKET & slotStack.getFluid() != Fluids.EMPTY) {
                    setCursorStack(ItemUsage.exchangeStack(cursorStack, playerEntity, new ItemStack(slotStack.getFluid().getBucketItem())));
                    slotStack.decrement(FluidStack.UNITS_PER_BUCKET);
                }
            } while (actionType == SlotActionType.QUICK_MOVE && !cursorStack.isEmpty() && slotStack.getAmount() >= FluidStack.UNITS_PER_BUCKET & slotStack.getFluid() != Fluids.EMPTY);
            slot.markDirty();
            return slotStack;
        } else if (cursorStack.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) cursorStack.getItem();
            Fluid fluid = ((BucketItemAccessor) item).getFluid();


            FluidStack temp = new FluidStack(fluid, FluidStack.UNITS_PER_BUCKET);
            if (slot.inventory.isValidExternal(slot.getIndex(), temp)) {
                if (slot.getStack().isEmpty()) {
                    slot.setStack(temp.copy());
                } else
                    slotStack.increment(FluidStack.UNITS_PER_BUCKET);
                setCursorStack(!playerEntity.getAbilities().creativeMode ? new ItemStack(Items.BUCKET) : cursorStack);
                if (actionType == SlotActionType.QUICK_MOVE) {
                    List<Slot> matchingSlots = new ArrayList<>();
                    slots.forEach(i -> {
                        if (i.getStack().getItem() instanceof BucketItem && ((BucketItemAccessor) i.getStack().getItem()).getFluid() == fluid)
                            matchingSlots.add(i);
                    });
                    for (Slot matchingSlot : matchingSlots) {
                        if (slot.inventory.isValidExternal(slot.getIndex(), temp)) {
                            slot.getStack().increment(FluidStack.UNITS_PER_BUCKET);
                            matchingSlot.setStack(!playerEntity.getAbilities().creativeMode ? new ItemStack(Items.BUCKET) : matchingSlot.getStack());
                        } else
                            break;
                    }
                }
                slot.markDirty();
                return temp.copy();
            }
        }

        return FluidStack.EMPTY;
    }


    public ItemStack simpleTransferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 26) {
                if (!this.insertItem(stack, 27, 36, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 27, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


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
}
