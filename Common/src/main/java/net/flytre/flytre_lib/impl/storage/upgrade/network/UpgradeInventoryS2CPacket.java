package net.flytre.flytre_lib.impl.storage.upgrade.network;


import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class UpgradeInventoryS2CPacket implements Packet<ClientPlayPacketListener> {

    private final int syncId;
    private final DefaultedList<ItemStack> stacks;

    public UpgradeInventoryS2CPacket(int syncId, DefaultedList<ItemStack> stacks) {
        this.syncId = syncId;
        this.stacks = stacks;
    }

    public UpgradeInventoryS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readInt();
        List<ItemStack> temp = PacketUtils.listFromPacket(buf, PacketByteBuf::readItemStack);
        this.stacks = DefaultedList.ofSize(temp.size(), ItemStack.EMPTY);
        for (int j = 0; j < stacks.size(); ++j)
            stacks.set(j, temp.get(j));
    }


    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(syncId);
        PacketUtils.toPacket(buf, stacks, (stack, p) -> p.writeItemStack(stack));
    }


    public int getSyncId() {
        return syncId;
    }

    public DefaultedList<ItemStack> getStacks() {
        return stacks;
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ClientHelper.apply(this, listener);
    }
}
