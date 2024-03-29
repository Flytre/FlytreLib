package net.flytre.flytre_lib.mixin.base;

import net.flytre.flytre_lib.api.base.util.FakeWorld;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Make sure not to render name tags when simulating a world, as that causes crashes when it tries to calculate
 * name tag size / distance from play
 */
@Mixin(LivingEntityRenderer.class)
class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
    private void flytre_lib$hasLabelOverride(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity.world instanceof FakeWorld)
            cir.setReturnValue(false);
    }
}
