package net.flytre.flytre_lib.api.storage.inventory.filter.packet;

import net.flytre.flytre_lib.api.storage.inventory.filter.Filtered;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockModMatchC2SPacket implements Packet<ServerPlayPacketListener> {


    private final BlockPos pos;
    private final int mode;

    public BlockModMatchC2SPacket(BlockPos pos, int mode) {
        this.pos = pos;
        this.mode = mode;
    }

    public BlockModMatchC2SPacket(PacketByteBuf buf) {
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
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) listener).getPlayer();
        ServerWorld world = player.getWorld();
        world.getServer().execute(() -> {
            if (pos.getSquaredDistance(player.getX(), player.getY(), player.getZ(), false) > 36)
                return;

            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof Filtered)
                ((Filtered) entity).getFilter().setMatchMod(mode == 1);
            if (entity instanceof FilterEventHandler) {
                ((FilterEventHandler) entity).onPacketReceived();
            }
        });
    }
}
