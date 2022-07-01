package net.flytre.flytre_lib.impl.storage.fluid.network;

import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public final class ClientHelper {

    private ClientHelper() {

    }

    static void apply(FluidInventoryS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            if (playerEntity == null)
                return;
            if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                ((FluidHandler) playerEntity.currentScreenHandler).updateFluidSlotStacks(packet.getStacks());
            }
        });
    }

    static void apply(FluidSlotUpdateS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity playerEntity = client.player;
        client.execute(() -> {
            assert playerEntity != null;
            if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && playerEntity.currentScreenHandler instanceof FluidHandler) {
                ((FluidHandler) playerEntity.currentScreenHandler).setFluidStackInSlot(packet.getSlot(), packet.getStack());
            }
        });
    }
}
