package net.flytre.flytre_lib.impl.storage.upgrade.network;


import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class UpgradeSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {

    private final int syncId;
    private final int slot;
    private final ItemStack stack;

    public UpgradeSlotUpdateS2CPacket(int syncId, int slot, ItemStack stack) {
        this.syncId = syncId;
        this.slot = slot;
        this.stack = stack;
    }

    public UpgradeSlotUpdateS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readInt();
        this.slot = buf.readInt();
        this.stack = buf.readItemStack();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(syncId);
        buf.writeInt(slot);
        buf.writeItemStack(stack);
    }

    @Override

    public void apply(ClientPlayPacketListener listener) {
        ClientHelper.apply(this, listener);
    }

    public int getSyncId() {
        return syncId;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
    }
}
