package net.flytre.flytre_lib.impl.base.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.HashMap;
import java.util.Map;

public class DefaultAttributeRegistryImpl {

    public static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> REGISTRY = new HashMap<>();


    public static void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer attributes) {
        REGISTRY.put(entityType, attributes);
    }

}
