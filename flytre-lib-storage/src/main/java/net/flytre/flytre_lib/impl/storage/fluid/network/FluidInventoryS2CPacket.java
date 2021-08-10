package net.flytre.flytre_lib.impl.storage.fluid.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class FluidInventoryS2CPacket implements Packet<ClientPlayPacketListener> {

    private final int syncId;
    private final DefaultedList<FluidStack> stacks;

    public FluidInventoryS2CPacket(int syncId, DefaultedList<FluidStack> stacks) {
        this.syncId = syncId;
        this.stacks = stacks;
    }

    public FluidInventoryS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readInt();
        List<FluidStack> temp = PacketUtils.listFromPacket(buf, FluidStack::fromPacket);
        this.stacks = DefaultedList.ofSize(temp.size(), FluidStack.EMPTY);
        for (int j = 0; j < stacks.size(); ++j)
            stacks.set(j, temp.get(j));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(syncId);
        PacketUtils.toPacket(buf, stacks, FluidStack::toPacket);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void apply(ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            if(playerEntity == null)
                return;
            if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                ((FluidHandler) playerEntity.currentScreenHandler).updateFluidSlotStacks(stacks);
            }
        });
    }
}
