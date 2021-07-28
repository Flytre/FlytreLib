package net.flytre.flytre_lib.mixin;

import net.flytre.flytre_lib.client.util.FakeWorld;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(method = "hasLabel", at = @At("INVOKE"), cancellable = true)
    private void flytre_lib$hasLabelOverride(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity.world instanceof FakeWorld)
            cir.setReturnValue(false);
    }
}
