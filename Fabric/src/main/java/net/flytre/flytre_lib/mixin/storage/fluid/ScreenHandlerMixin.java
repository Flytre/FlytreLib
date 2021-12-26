package net.flytre.flytre_lib.mixin.storage.fluid;

import com.google.common.base.Suppliers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidSlot;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerListener;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerSyncHandler;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

;

/**
 * Implements FluidHandler onto every Screen Handler
 */
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements FluidHandler {

    @Unique
    public DefaultedList<FluidSlot> fluidSlots = DefaultedList.of();

    @Unique
    private DefaultedList<FluidStack> trackedFluidStacks = DefaultedList.of();

    @Unique
    private DefaultedList<FluidStack> previousTrackedFluidStacks = DefaultedList.of();
    @Shadow
    @Final
    private List<ScreenHandlerListener> listeners;

    @Unique
    private @Nullable FluidHandlerSyncHandler fluidSyncHandler;


    @Inject(method="<init>*", at = @At("TAIL"))
    public void flytre_lib$init(ScreenHandlerType<?> type, int syncId, CallbackInfo ci) {
        fluidSlots = DefaultedList.of();
        trackedFluidStacks = DefaultedList.of();
        previousTrackedFluidStacks = DefaultedList.of();
    }

    @Shadow
    public abstract void syncState();

    @Shadow public abstract ItemStack getCursorStack();

    @Shadow public abstract void setCursorStack(ItemStack stack);

    @Shadow @Final public DefaultedList<Slot> slots;

    @Shadow protected abstract boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

    @Shadow protected abstract Slot addSlot(Slot slot);

    @Shadow private boolean disableSync;

    @Override
    public FluidSlot addSlot(FluidSlot slot) {
        slot.id = this.fluidSlots.size();
        this.fluidSlots.add(slot);
        this.trackedFluidStacks.add(FluidStack.EMPTY);
        this.previousTrackedFluidStacks.add(FluidStack.EMPTY);
        return slot;
    }

    @Override
    public DefaultedList<FluidStack> getFluidStacks() {
        DefaultedList<FluidStack> defaultedList = DefaultedList.of();

        for (FluidSlot fluidSlot : this.fluidSlots) {
            defaultedList.add(fluidSlot.getStack());
        }
        return defaultedList;
    }

    @Override
    public void updateSyncHandler(FluidHandlerSyncHandler handler) {
        this.fluidSyncHandler = handler;
        this.syncState();
    }

    @Inject(method = "syncState", at = @At("HEAD"))
    public void flytre_lib$syncFluidState(CallbackInfo ci) {
        for (int k = 0, l = this.fluidSlots.size(); k < l; ++k)
            this.previousTrackedFluidStacks.set(k, this.fluidSlots.get(k).getStack().copy());

        if (this.fluidSyncHandler != null) {
            this.fluidSyncHandler.updateFluidState(this, this.previousTrackedFluidStacks);
        }
    }

    @Override
    public ScreenHandler get() {
        return (ScreenHandler) (Object) this;
    }

    @Inject(method = "sendContentUpdates", at = @At("RETURN"))
    public void flytre_lib$sendFluidContentUpdates(CallbackInfo ci) {
        for (int j = 0; j < this.fluidSlots.size(); ++j) {
            FluidStack fluidStack = this.fluidSlots.get(j).getStack();
            Objects.requireNonNull(fluidStack);
            Supplier<FluidStack> supplier = Suppliers.memoize(fluidStack::copy);
            this.updateTrackedSlot(j, fluidStack, supplier);
            this.checkSlotUpdates(j, fluidStack, supplier);
        }
    }

    @Unique
    private void updateTrackedSlot(int slot, FluidStack stack, Supplier<FluidStack> copyMaker) {
        FluidStack fluidStack = this.trackedFluidStacks.get(slot);
        if (!FluidStack.areEqual(fluidStack, stack)) {
            FluidStack fluidStack2 = copyMaker.get();
            this.trackedFluidStacks.set(slot, fluidStack2);

            for (var listener : listeners)
                if (listener instanceof FluidHandlerListener)
                    ((FluidHandlerListener) listener).onSlotUpdate(this, slot, fluidStack2);
        }

    }

    @Unique
    private void checkSlotUpdates(int slot, FluidStack stack, Supplier<FluidStack> copyMaker) {
        if (!disableSync) {
            FluidStack fluidStack = this.previousTrackedFluidStacks.get(slot);
            if (!FluidStack.areEqual(fluidStack, stack)) {
                FluidStack fluidStack2 = copyMaker.get();
                this.previousTrackedFluidStacks.set(slot, fluidStack2);
                if (this.fluidSyncHandler != null) {
                    this.fluidSyncHandler.updateSlot(this, slot, fluidStack2);
                }
            }

        }
    }

    @Override
    public void setPreviousTrackedSlot(int slot, FluidStack stack) {
        this.previousTrackedFluidStacks.set(slot, stack);
    }


    @Override
    public FluidSlot getFluidSlot(int index) {
        return this.fluidSlots.get(index);
    }

    @Override
    public void setFluidStackInSlot(int slot, FluidStack stack) {
        this.getFluidSlot(slot).setStack(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void updateFluidSlotStacks(List<FluidStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            this.getFluidSlot(i).setStack(stacks.get(i));
        }

    }

    @Override
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

    @Override
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
    public DefaultedList<FluidSlot> getFluidSlots() {
        return fluidSlots;
    }
}
