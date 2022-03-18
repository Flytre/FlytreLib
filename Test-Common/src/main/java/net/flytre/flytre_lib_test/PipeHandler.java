package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.storage.inventory.filter.FilterInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class PipeHandler extends ScreenHandler {
    private final FilterInventory inv;
    private BlockPos pos;
    private boolean synced;
    private int filterType;
    private boolean matchMod;
    private boolean matchNbt;
    private boolean isRoundRobin;

    public PipeHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new PipeEntity(BlockPos.ORIGIN, Registry.ITEM_PIPE.getDefaultState()));
        pos = buf.readBlockPos();
        synced = true;
        filterType = buf.readInt();
        matchMod = buf.readBoolean();
        matchNbt = buf.readBoolean();
        isRoundRobin = buf.readBoolean();
    }


    public PipeHandler(int syncId, PlayerInventory playerInventory, PipeEntity entity) {
        super(Registry.ITEM_PIPE_SCREEN_HANDLER, syncId);
        this.inv = entity.getFilter();
        pos = BlockPos.ORIGIN;

        inv.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inv, l + m * 3, 62 + l * 18, 17 + m * 18) {
                    @Override
                    public void markDirty() {
                        entity.clearNetworkCache();
                    }
                });
            }
        }

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if (slotId >= 0) {
            ItemStack stack = getSlot(slotId).getStack();
            boolean isPlayerInventory = slotId >= inv.size();
            if (!isPlayerInventory) {
                inv.removeStack(slotId);
            } else {
                HashSet<Item> items = new HashSet<>();
                items.add(stack.getItem());
                if (!inv.containsAny(items))
                    inv.put(stack);
                else
                    return;
            }

            getSlot(slotId).inventory.markDirty();
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 9) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inv.onClose(player);
    }

    public boolean getSynced() {
        return synced;
    }

    public int getFilterType() {
        return filterType;
    }

    public int getModMatch() {
        return matchMod ? 1 : 0;
    }

    public int getNbtMatch() {
        return matchNbt ? 1 : 0;
    }


    public int getRoundRobinMode() {
        return isRoundRobin ? 1 : 0;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        return false;
    }
}
