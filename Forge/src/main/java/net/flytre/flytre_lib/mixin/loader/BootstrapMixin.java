package net.flytre.flytre_lib.mixin.loader;


import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sample registry mixin. You'll want one of these!
 */
@Mixin(Bootstrap.class)
public class BootstrapMixin {
    @Inject(method = "initialize", at = @At("RETURN"))
    private static void flytre_lib$test(CallbackInfo ci) {
    }

}
