package net.flytre.flytre_lib.loader.registry;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface EntityRegisterer {

    <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id);
}
