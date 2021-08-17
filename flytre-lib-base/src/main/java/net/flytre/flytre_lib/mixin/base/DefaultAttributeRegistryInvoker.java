package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.api.base.registry.DefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DefaultAttributeRegistry.class)
public interface DefaultAttributeRegistryInvoker {

    @Invoker("register")
    static void flytre_lib$register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer attributes) {
        throw new AssertionError();
    }
}
