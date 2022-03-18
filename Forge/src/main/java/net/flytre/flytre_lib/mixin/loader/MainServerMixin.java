package net.flytre.flytre_lib.mixin.loader;

import net.flytre.flytre_lib.loader.LoaderPropertyInitializer;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainServerMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$setLoaderProperties(String[] args, CallbackInfo ci) {
        LoaderPropertyInitializer.init(args);
    }
}
