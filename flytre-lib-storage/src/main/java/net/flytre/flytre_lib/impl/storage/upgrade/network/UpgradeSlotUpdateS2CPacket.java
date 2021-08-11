package net.flytre.flytre_lib.impl.storage.upgrade.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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
    @Environment(EnvType.CLIENT)
    public void apply(ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            assert playerEntity != null;
            if (syncId == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                ((UpgradeHandler) playerEntity.currentScreenHandler).setUpgradeStackInSlot(slot, stack);
            }
        });
    }
}
