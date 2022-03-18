package net.flytre.flytre_lib.loader;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class EntityAttributeSetterImpl implements EntityAttributeSetter.Delegate {

    private static final List<EntityAttributeEntries> ENTITY_ATTRIBUTES = new ArrayList<>();


    private EntityAttributeSetterImpl() {

    }

    public static List<EntityAttributeEntries> getEntityAttributes() {
        return ImmutableList.copyOf(ENTITY_ATTRIBUTES);
    }

    public static void init() {
        EntityAttributeSetter.setDelegate(new EntityAttributeSetterImpl());
    }


    @Override
    public void set(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attributes) {
        ENTITY_ATTRIBUTES.add(new EntityAttributeEntries(entityType, attributes));
    }

    record EntityAttributeEntries(EntityType<? extends LivingEntity> entityType,
                                  Supplier<DefaultAttributeContainer.Builder> attributes) {
    }
}
