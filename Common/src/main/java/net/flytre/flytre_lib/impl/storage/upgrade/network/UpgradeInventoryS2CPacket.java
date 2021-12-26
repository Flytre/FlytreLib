package net.flytre.flytre_lib.impl.storage.upgrade.network;


import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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

    @Override
 
    public void apply(ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            if(playerEntity == null)
                return;
            if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                ((UpgradeHandler) playerEntity.currentScreenHandler).updateUpgradeSlotStacks(stacks);
            }
        });
    }
}
