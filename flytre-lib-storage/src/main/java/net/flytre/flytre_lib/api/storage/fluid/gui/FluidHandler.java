package net.flytre.flytre_lib.api.storage.fluid.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerSyncHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;


/**
 * Implemented by ScreenHandler
 */
public interface FluidHandler {


    FluidSlot addSlot(FluidSlot slot);

    DefaultedList<FluidStack> getFluidStacks();


    @ApiStatus.Internal
    void updateSyncHandler(FluidHandlerSyncHandler handler);


    ScreenHandler get();

    void setPreviousTrackedSlot(int slot, FluidStack stack);

    FluidSlot getFluidSlot(int index);

    void setFluidStackInSlot(int slot, FluidStack stack);

    @Environment(EnvType.CLIENT)
    void updateFluidSlotStacks(List<FluidStack> stacks);

    FluidStack onFluidSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity playerEntity);

    ItemStack simpleTransferSlot(PlayerEntity player, int index);


    void addInventorySlots(PlayerInventory playerInventory);


    DefaultedList<FluidSlot> getFluidSlots();
}
