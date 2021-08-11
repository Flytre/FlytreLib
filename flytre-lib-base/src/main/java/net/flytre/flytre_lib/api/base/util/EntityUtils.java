package net.flytre.flytre_lib.api.base.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityUtils {

    public static HitResult raycastNoFluid(Entity entity, double maxDistance) {
        Vec3d origin = new Vec3d(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
        return entity.getEntityWorld().raycast(new RaycastContext(origin, origin.add(entity.getRotationVector().normalize().multiply(maxDistance)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));
    }

    /**
     * @return ALL entities that the player is currently looking at (a line out from the cross hair would intersect with the entity)
     */
    public static Set<Entity> getEntitiesLookedAt(Entity looker, double maxDistance) {
        return getEntityLookedAtHelper(looker, maxDistance).all;
    }

    /**
     * @return The entity that the player's crosshair is directly over
     */
    public static Entity getEntityLookedAt(Entity looker, double maxDistance) {
        return getEntityLookedAtHelper(looker, maxDistance).main;
    }

    private static LookedAtEntities getEntityLookedAtHelper(Entity looker, double maxDistance) {

        HitResult hitResult = EntityUtils.raycastNoFluid(looker, maxDistance);
        Vec3d lookerPosition = looker.getPos().add(0, looker.getEyeHeight(looker.getPose()), 0);

        double distance = hitResult == null ? maxDistance : hitResult.getPos().distanceTo(lookerPosition);


        Vec3d length = new Vec3d(looker.getRotationVector().x * maxDistance, looker.getRotationVector().y * maxDistance, looker.getRotationVector().z * maxDistance);
        Vec3d end = lookerPosition.add(length);


        Entity selectedEntity = null;
        Entity foundEntity = null;
        Set<Entity> foundEntities = new HashSet<>();
        double distanceToSelected = distance;

        for (Entity entity : looker.getEntityWorld().getOtherEntities(looker, looker.getBoundingBox().stretch(length).expand(1.0F))) {
            if (entity.collides()) {
                Box collisionBox = entity.getVisibilityBoundingBox();
                Optional<Vec3d> intercept = collisionBox.raycast(lookerPosition, end);
                if (collisionBox.contains(lookerPosition)) {
                    if (distanceToSelected >= 0) {
                        selectedEntity = entity;
                        distanceToSelected = 0;
                    }
                } else if (intercept.isPresent()) {
                    double currDist = lookerPosition.distanceTo(intercept.get());
                    if (currDist < distanceToSelected || distanceToSelected == 0) {
                        selectedEntity = entity;
                        distanceToSelected = currDist;
                    }
                }
            }
            if (selectedEntity != null && (distanceToSelected < distance || hitResult == null)) {
                foundEntity = selectedEntity;
                foundEntities.add(selectedEntity);

            }
        }
        return new LookedAtEntities(foundEntity, foundEntities);
    }

    private record LookedAtEntities(Entity main, Set<Entity> all) {

    }


}
