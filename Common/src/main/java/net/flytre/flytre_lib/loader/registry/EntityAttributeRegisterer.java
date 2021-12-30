package net.flytre.flytre_lib.loader.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

public interface EntityAttributeRegisterer {

    void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer.Builder attributes);
}
