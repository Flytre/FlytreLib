package net.flytre.flytre_lib.api.base.registry;

import net.flytre.flytre_lib.impl.base.registry.DefaultAttributeRegistryImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

public class DefaultAttributeRegistry {


    public static void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer attributes) {
        DefaultAttributeRegistryImpl.register(entityType, attributes);
    }

    private DefaultAttributeRegistry() {
        throw new AssertionError();
    }

}
