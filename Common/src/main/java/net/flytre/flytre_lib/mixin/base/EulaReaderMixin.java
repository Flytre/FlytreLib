package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.server.dedicated.EulaReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Automatically agree to EULA in the dev environment
 */
@Mixin(EulaReader.class)
public class EulaReaderMixin {

    @Inject(method = "isEulaAgreedTo", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$ignoreEulaCheck(CallbackInfoReturnable<Boolean> cir) {
        if (LoaderProperties.isDevEnvironment())
            cir.setReturnValue(true);
    }
}
