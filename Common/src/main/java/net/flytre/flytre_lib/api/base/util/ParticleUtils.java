package net.flytre.flytre_lib.api.base.util;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public final class ParticleUtils {

    private ParticleUtils() {
        throw new AssertionError();
    }

    public static void drawLineTo(Vec3d a, Vec3d b, ParticleEffect particle, ServerWorld world) {
        drawLineTo(a, b, particle, world, 1, true);
    }

    public static void drawLineTo(Vec3d a, Vec3d b, ParticleEffect particle, ServerWorld world, int count, boolean force) {
        double dist = a.distanceTo(b);

        Collection<ServerPlayerEntity> players = PlayerSearchUtils.tracking(world, new BlockPos(a.x, a.y, a.z));
        players.addAll(PlayerSearchUtils.tracking(world, new BlockPos(b.x, b.y, b.z)));

        for (double i = 0; i < 1; i += Math.min(0.1, 1 / (dist * 2))) {
            double x = a.getX() + (b.getX() - a.getX()) * i;
            double y = a.getY() + 1 + (b.getY() - a.getY()) * i;
            double z = a.getZ() + 1 + (b.getZ() - a.getZ()) * i;
            for (ServerPlayerEntity playerEntity : players)
                world.spawnParticles(playerEntity, particle, force, x, y, z, count, 0, 0, 0, 0);
        }
    }

    public static Vec3d caretToAbsolute(Vec3d caretValues, Vec2f rotation, Vec3d anchorPosition) {
        float f = MathHelper.cos((rotation.y + 90.0F) * 0.017453292F);
        float g = MathHelper.sin((rotation.y + 90.0F) * 0.017453292F);
        float h = MathHelper.cos(-rotation.x * 0.017453292F);
        float i = MathHelper.sin(-rotation.x * 0.017453292F);
        float j = MathHelper.cos((-rotation.x + 90.0F) * 0.017453292F);
        float k = MathHelper.sin((-rotation.x + 90.0F) * 0.017453292F);
        Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
        Vec3d vec3d3 = new Vec3d(f * j, k, g * j);
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0D);
        double d = vec3d2.x * caretValues.z + vec3d3.x * caretValues.y + vec3d4.x * caretValues.x;
        double e = vec3d2.y * caretValues.z + vec3d3.y * caretValues.y + vec3d4.y * caretValues.x;
        double l = vec3d2.z * caretValues.z + vec3d3.z * caretValues.y + vec3d4.z * caretValues.x;
        return new Vec3d(anchorPosition.x + d, anchorPosition.y + e, anchorPosition.z + l);
    }
}
