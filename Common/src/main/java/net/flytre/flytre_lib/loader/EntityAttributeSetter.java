package net.flytre.flytre_lib.loader;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

import java.util.function.Supplier;

/**
 * Used to set the default attributes of an entity
 */
public final class EntityAttributeSetter {

    private static Delegate DELEGATE;

    private EntityAttributeSetter() {
        throw new AssertionError();
    }

    public static void setDelegate(Delegate delegate) {
        EntityAttributeSetter.DELEGATE = delegate;
    }

    public static void set(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attributes) {
        DELEGATE.set(entityType, attributes);
    }

    interface Delegate {
        void set(EntityType<? extends LivingEntity> entityType, Supplier<DefaultAttributeContainer.Builder> attributes);

    }
}
