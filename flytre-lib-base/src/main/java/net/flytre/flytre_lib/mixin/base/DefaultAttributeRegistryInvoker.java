package net.flytre.flytre_lib.mixin.base;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DefaultAttributeRegistry.class)
public interface DefaultAttributeRegistryInvoker {

    @Accessor("DEFAULT_ATTRIBUTE_REGISTRY")
    static Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> flytre_lib$getRegistry() {
        throw new AssertionError("mixin dummy");
    }
}
