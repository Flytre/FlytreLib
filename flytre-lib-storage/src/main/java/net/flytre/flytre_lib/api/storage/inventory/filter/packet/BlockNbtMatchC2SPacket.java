package net.flytre.flytre_lib.api.storage.inventory.filter.packet;

import net.flytre.flytre_lib.api.storage.inventory.filter.Filtered;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNbtMatchC2SPacket implements Packet<ServerPlayPacketListener> {


    private final BlockPos pos;
    private final int mode;

    public BlockNbtMatchC2SPacket(BlockPos pos, int mode) {
        this.pos = pos;
        this.mode = mode;
    }

    public BlockNbtMatchC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.mode = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(mode);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayNetworkHandler handler = ((ServerPlayNetworkHandler) listener);
        World world = handler.getPlayer().world;
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof Filtered)
            ((Filtered) entity).getFilter().setMatchNbt(mode == 1);
        if (entity instanceof FilterEventHandler) {
            ((FilterEventHandler) entity).onPacketReceived();
        }
    }
}
