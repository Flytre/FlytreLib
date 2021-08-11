package net.flytre.flytre_lib.impl.storage.fluid.gui;

import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.fluid.network.FluidSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface FluidHandlerSyncHandler {
    void updateFluidState(FluidHandler handler, DefaultedList<FluidStack> stacks);

    void updateSlot(FluidHandler handler, int slot, FluidStack stack);


    class Impl implements FluidHandlerSyncHandler {
        private final ServerPlayerEntity me;

        public Impl(ServerPlayerEntity me) {
            this.me = me;
        }

        @Override
        public void updateFluidState(FluidHandler handler, DefaultedList<FluidStack> stacks) {
            me.networkHandler.sendPacket(new FluidInventoryS2CPacket(handler.get().syncId,stacks));
            me.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_CURSOR_SYNC_ID, handler.get().nextRevision(), -1, me.currentScreenHandler.getCursorStack()));
        }

        @Override
        public void updateSlot(FluidHandler handler, int slot, FluidStack stack) {
            me.networkHandler.sendPacket(new FluidSlotUpdateS2CPacket(handler.get().syncId,slot,stack));
        }
    }
}
