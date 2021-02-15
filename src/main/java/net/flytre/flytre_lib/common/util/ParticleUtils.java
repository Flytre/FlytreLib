package net.flytre.flytre_lib.common.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public class ParticleUtils {

    public static void drawLineTo(Vec3d a, Vec3d b, ParticleEffect particle, ServerWorld world) {
        drawLineTo(a, b, particle, world, 1, true);
    }

    public static void drawLineTo(Vec3d a, Vec3d b, ParticleEffect particle, ServerWorld world, int count, boolean force) {
        double dist = a.distanceTo(b);
        Collection<ServerPlayerEntity> players = PlayerLookup.tracking(world, new BlockPos(a.x, a.y, a.z));
        players.addAll(PlayerLookup.tracking(world, new BlockPos(b.x, b.y, b.z)));

        for (double i = 0; i < 1; i += Math.min(0.1, 1 / (dist * 2))) {
            double x = a.getX() + (b.getX() - a.getX()) * i;
            double y = a.getY() + 1 + (b.getY() - a.getY()) * i;
            double z = a.getZ() + 1 + (b.getZ() - a.getZ()) * i;
            for (ServerPlayerEntity playerEntity : players)
                world.spawnParticles(playerEntity, particle, force, x, y, z, count, 0, 0, 0, 0);
        }
    }


}
