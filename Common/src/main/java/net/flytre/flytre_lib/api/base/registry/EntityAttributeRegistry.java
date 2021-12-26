package net.flytre.flytre_lib.api.base.registry;

import net.flytre.flytre_lib.mixin.base.DefaultAttributeRegistryInvoker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

public class EntityAttributeRegistry {


    private EntityAttributeRegistry() {
        throw new AssertionError();
    }

    public static void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer attributes) {
        DefaultAttributeRegistryInvoker.flytre_lib$getRegistry().put(entityType, attributes);
    }

    public static void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer.Builder attributes) {
        DefaultAttributeRegistryInvoker.flytre_lib$getRegistry().put(entityType, attributes.build());
    }

}
