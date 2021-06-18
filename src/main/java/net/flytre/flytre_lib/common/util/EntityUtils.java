package net.flytre.flytre_lib.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class EntityUtils {

    public static HitResult raycastNoFluid(Entity entity, double maxDistance) {
        Vec3d origin = new Vec3d(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
        return entity.getEntityWorld().raycast(new RaycastContext(origin, origin.add(entity.getRotationVector().normalize().multiply(maxDistance)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));

    }

}
