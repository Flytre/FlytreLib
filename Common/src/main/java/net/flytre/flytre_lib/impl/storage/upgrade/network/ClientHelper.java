package net.flytre.flytre_lib.impl.storage.upgrade.network;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClientHelper {


    private ClientHelper() {
    }

    static void apply(UpgradeInventoryS2CPacket packet, ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            if (playerEntity == null)
                return;
            if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                ((UpgradeHandler) playerEntity.currentScreenHandler).updateUpgradeSlotStacks(packet.getStacks());
            }
        });

    }

    static void apply(UpgradeSlotUpdateS2CPacket packet, ClientPlayPacketListener listener) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            assert playerEntity != null;
            if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof UpgradeHandler) {
                ((UpgradeHandler) playerEntity.currentScreenHandler).setUpgradeStackInSlot(packet.getSlot(), packet.getStack());
            }
        });

    }
}
