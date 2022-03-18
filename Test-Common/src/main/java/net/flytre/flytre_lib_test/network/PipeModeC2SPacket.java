package net.flytre.flytre_lib_test.network;

import net.flytre.flytre_lib_test.PipeEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PipeModeC2SPacket implements Packet<ServerPlayPacketListener> {

    private final BlockPos pos;
    private final int val;

    public PipeModeC2SPacket(BlockPos pos, int val) {
        this.pos = pos;
        this.val = val;
    }

    public PipeModeC2SPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.val = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(val);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) listener).getPlayer();
        ServerWorld world = player.getWorld();
        world.getServer().execute(() -> {
            if (pos.getSquaredDistance(player.getX(), player.getY(), player.getZ(), false) > 36)
                return;

            BlockEntity entity = world.getBlockEntity(pos);
            if (!(entity instanceof PipeEntity pipe))
                return;
            pipe.setRoundRobinMode(val != 0);
        });
    }
}
