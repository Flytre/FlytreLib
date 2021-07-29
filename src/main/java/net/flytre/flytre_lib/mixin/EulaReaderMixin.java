package net.flytre.flytre_lib.mixin;


import net.fabricmc.loader.api.FabricLoader;
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
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            cir.setReturnValue(true);
    }
}
