package net.flytre.flytre_lib.api.base.util;

import net.flytre.flytre_lib.mixin.base.EntityTrackerAccessor;
import net.flytre.flytre_lib.mixin.base.ThreadedAnvilChunkStorageAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public final class PlayerSearchUtils {

    public static Collection<ServerPlayerEntity> allPlayers(@NotNull MinecraftServer server) {

        if (server.getPlayerManager() != null) {
            return Collections.unmodifiableCollection(server.getPlayerManager().getPlayerList());
        }

        return Collections.emptyList();
    }

    public static Collection<ServerPlayerEntity> allInWorld(@NotNull ServerWorld world) {
        return Collections.unmodifiableCollection(world.getPlayers());
    }

    public static Collection<ServerPlayerEntity> tracking(@NotNull ServerWorld world, @NotNull ChunkPos pos) {
        return world.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(pos, false);
    }

    public static Collection<ServerPlayerEntity> tracking(@NotNull BlockEntity blockEntity) {

        //noinspection ConstantConditions
        if (!blockEntity.hasWorld() || blockEntity.getWorld().isClient()) {
            throw new IllegalArgumentException("Only supported on server worlds!");
        }

        return tracking((ServerWorld) blockEntity.getWorld(), blockEntity.getPos());
    }


    public static Collection<ServerPlayerEntity> tracking(@NotNull ServerWorld world, @NotNull BlockPos pos) {
        return tracking(world, new ChunkPos(pos));
    }

    public static Collection<ServerPlayerEntity> around(@NotNull ServerWorld world, @NotNull Vec3d pos, double radius) {
        double radiusSquared = radius * radius;

        return allInWorld(world)
                .stream()
                .filter((p) -> p.squaredDistanceTo(pos) <= radiusSquared)
                .collect(Collectors.toList());
    }

    public static Collection<ServerPlayerEntity> around(@NotNull ServerWorld world, @NotNull Vec3i pos, double radius) {
        double radiusSquared = radius * radius;

        return allInWorld(world)
                .stream()
                .filter((p) -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= radiusSquared)
                .collect(Collectors.toList());
    }

    public static Collection<ServerPlayerEntity> tracking(@NotNull Entity entity) {
        ChunkManager manager = entity.world.getChunkManager();

        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;
            EntityTrackerAccessor tracker = ((ThreadedAnvilChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            if (tracker != null) {
                return tracker.getPlayersTracking()
                        .stream()
                        .map(EntityTrackingListener::getPlayer)
                        .collect(Collectors.toUnmodifiableSet());
            }

            return Collections.emptySet();
        }

        throw new IllegalArgumentException("Only supported on server worlds!");
    }

    private PlayerSearchUtils() {
    }
}
