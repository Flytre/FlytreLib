package net.flytre.flytre_lib_test.mixin;

import net.flytre.flytre_lib_test.PipeEntity;
import net.flytre.flytre_lib_test.Registry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {

    @Shadow
    @Final
    private ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
    @Shadow
    @Final
    ChunkPos pos;

    @Inject(method = "sendBlockEntityUpdatePacket", at = @At("HEAD"), cancellable = true)
    public void pipe$customPipeUpdates(World world, BlockPos pos, CallbackInfo ci) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PipeEntity pipeEntity))
            return;
        ci.cancel();
        Packet<ClientPlayPacketListener> updatePacket = pipeEntity.toUpdatePacket();
        this.pipe$sendPacketToNearPlayers(pos, updatePacket);
    }

    private void pipe$sendPacketToNearPlayers(BlockPos pos, Packet<ClientPlayPacketListener> packet) {
        int distance = Registry.PIPE_CONFIG.getConfig().maxItemRenderDistance;
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false).forEach((serverPlayerEntity) -> {
            if (serverPlayerEntity.getBlockPos().getSquaredDistance(pos) < distance * distance) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        });
    }
}
