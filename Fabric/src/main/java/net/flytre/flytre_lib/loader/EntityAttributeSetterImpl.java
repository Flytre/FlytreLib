package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.api.base.registry.EntityAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.function.Supplier;

final class EntityAttributeSetterImpl implements EntityAttributeSetter.Delegate {

    private EntityAttributeSetterImpl() {

    }

    public static void init() {
        EntityAttributeSetter.setDelegate(new EntityAttributeSetterImpl());
    }


    @Override
    public void set(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attributes) {
        EntityAttributeRegistry.register(entityType, attributes.get());
    }
}
