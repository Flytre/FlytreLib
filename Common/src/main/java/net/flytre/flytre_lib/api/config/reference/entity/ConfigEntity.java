package net.flytre.flytre_lib.api.config.reference.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


sealed public interface ConfigEntity permits EntityReference, EntityTagReference {


    /**
     * Not to be used unless very rarely
     */
    static Set<EntityType<?>> values(Set<ConfigEntity> entities, World world) {
        Set<EntityType<?>> result = new HashSet<>();
        for (ConfigEntity entity : entities) {
            if (entity instanceof EntityReference) {
                result.add(((EntityReference) entity).getValue(world));
            } else {
                Set<EntityType<?>> list = ((EntityTagReference) entity).getValue(world);
                if (list != null)
                    result.addAll(list);
            }
        }
        return result;
    }

    /**
     * To be used! O(1) search time for entities not in tags, or O(n) for entities in tags / not present
     */
    static boolean contains(Set<ConfigEntity> entities, EntityType<?> entity, World world) {
        if (entities.contains(new EntityReference(entity)))
            return true;
        return entities.stream().anyMatch(i -> {
            if (!(i instanceof EntityTagReference))
                return false;
            Set<EntityType<?>> list = ((EntityTagReference) i).getValue(world);
            return list != null && list.contains(entity);
        });
    }

    static Set<ConfigEntity> of(Set<EntityType<?>> values) {
        return values.stream().map(EntityReference::new).collect(Collectors.toSet());
    }
}
