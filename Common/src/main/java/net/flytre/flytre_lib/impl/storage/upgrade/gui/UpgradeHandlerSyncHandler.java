package net.flytre.flytre_lib.impl.storage.upgrade.gui;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeInventoryS2CPacket;
import net.flytre.flytre_lib.impl.storage.upgrade.network.UpgradeSlotUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface UpgradeHandlerSyncHandler {
    void updateUpgradeState(UpgradeHandler handler, DefaultedList<ItemStack> stacks);

    void updateSlot(UpgradeHandler handler, int slot, ItemStack stack);


    class Impl implements UpgradeHandlerSyncHandler {
        private final ServerPlayerEntity me;

        public Impl(ServerPlayerEntity me) {
            this.me = me;
        }

        @Override
        public void updateUpgradeState(UpgradeHandler handler, DefaultedList<ItemStack> stacks) {
            me.networkHandler.sendPacket(new UpgradeInventoryS2CPacket(handler.get().syncId, stacks));
            me.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_CURSOR_SYNC_ID, handler.get().nextRevision(), -1, me.currentScreenHandler.getCursorStack()));
        }

        @Override
        public void updateSlot(UpgradeHandler handler, int slot, ItemStack stack) {
            me.networkHandler.sendPacket(new UpgradeSlotUpdateS2CPacket(handler.get().syncId, slot, stack));
        }
    }
}
