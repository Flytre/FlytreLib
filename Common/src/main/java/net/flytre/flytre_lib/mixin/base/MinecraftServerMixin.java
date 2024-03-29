package net.flytre.flytre_lib.mixin.base;

import com.mojang.authlib.GameProfile;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Automatic OP when in dev environment
 */
@Mixin(MinecraftServer.class)
class MinecraftServerMixin {


    @Inject(method = "getPermissionLevel", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$always_op(GameProfile profile, CallbackInfoReturnable<Integer> cir) {
        if (LoaderProperties.isDevEnvironment())
            cir.setReturnValue(4);
    }
}
