package net.flytre.flytre_lib.mixin.base;


import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.flytre.flytre_lib.impl.base.registry.DefaultAttributeRegistryImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Used to register entity attributes to the default attribute registry
 */
@Mixin(value = DefaultAttributeRegistry.class, priority = 1)
public class DefaultAttributeRegistryMixin {


    @Mutable
    @Shadow @Final private static Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> DEFAULT_ATTRIBUTE_REGISTRY;

    @Inject(method="<clinit>", at = @At("TAIL"))
    private static void flytre_lib$addDefaultAttributes(CallbackInfo ci) {
        DEFAULT_ATTRIBUTE_REGISTRY =  ImmutableMap.<EntityType<? extends LivingEntity>, DefaultAttributeContainer>builder().putAll(DEFAULT_ATTRIBUTE_REGISTRY).putAll(DefaultAttributeRegistryImpl.REGISTRY).build();
    }
}
