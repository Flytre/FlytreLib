package net.flytre.flytre_lib.impl.storage.upgrade.network;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class UpgradeClickSlotC2SPacket implements Packet<ServerPlayPacketListener> {
    private final int syncId;
    private final int slot;
    private final int button;
    private final SlotActionType actionType;
    private final Map<Integer, ItemStack> modifiedStacks;
    private final ItemStack cursorStack;

    public UpgradeClickSlotC2SPacket(int syncId, int slot, int button, SlotActionType actionType, Map<Integer, ItemStack> modifiedStacks, ItemStack cursorStack) {
        this.syncId = syncId;
        this.slot = slot;
        this.button = button;
        this.actionType = actionType;
        this.modifiedStacks = modifiedStacks;
        this.cursorStack = cursorStack;
    }

    public UpgradeClickSlotC2SPacket(PacketByteBuf buf) {
        this.syncId = buf.readByte();
        this.slot = buf.readShort();
        this.button = buf.readByte();
        this.actionType = buf.readEnumConstant(SlotActionType.class);
        this.modifiedStacks = buf.readMap((packetByteBuf) -> (int) packetByteBuf.readShort(), PacketByteBuf::readItemStack);
        this.cursorStack = buf.readItemStack();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(syncId);
        buf.writeShort(slot);
        buf.writeByte(button);
        buf.writeEnumConstant(actionType);
        buf.writeMap(modifiedStacks, PacketByteBuf::writeShort, PacketByteBuf::writeItemStack);
        buf.writeItemStack(cursorStack);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) listener).getPlayer();
        MinecraftServer server = player.getServer();
        assert server != null;
        server.execute(() -> run(player));
    }

    public void run(ServerPlayerEntity player) {
        player.updateLastActionTime();
        if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler instanceof UpgradeHandler) {
            if (player.isSpectator()) {
                player.currentScreenHandler.syncState();
            } else {
                player.currentScreenHandler.disableSyncing();
                ((UpgradeHandler) player.currentScreenHandler).onUpgradeSlotClick(slot, button, actionType, player);

                for (var entry : modifiedStacks.entrySet())
                    ((UpgradeHandler) player.currentScreenHandler).setPreviousTrackedUpgradeSlot(entry.getKey(), entry.getValue());

                player.currentScreenHandler.setPreviousCursorStack(cursorStack);
                player.currentScreenHandler.enableSyncing();
                player.currentScreenHandler.sendContentUpdates();
            }
        }
    }
}
