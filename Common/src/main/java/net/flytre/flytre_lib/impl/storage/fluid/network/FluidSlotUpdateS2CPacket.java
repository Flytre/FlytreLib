package net.flytre.flytre_lib.impl.storage.fluid.network;

import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FluidSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {

    private final int syncId;
    private final int slot;
    private final FluidStack stack;

    public FluidSlotUpdateS2CPacket(int syncId, int slot, FluidStack stack) {
        this.syncId = syncId;
        this.slot = slot;
        this.stack = stack;
    }

    public FluidSlotUpdateS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readInt();
        this.slot = buf.readInt();
        this.stack = FluidStack.fromPacket(buf);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(syncId);
        buf.writeInt(slot);
        stack.toPacket(buf);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            assert playerEntity != null;
            if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                ((FluidHandler) playerEntity.currentScreenHandler).setFluidStackInSlot(slot, stack);
            }
        });
    }
}
