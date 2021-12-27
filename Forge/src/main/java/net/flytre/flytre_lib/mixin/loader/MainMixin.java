package net.flytre.flytre_lib.mixin.loader;

import net.flytre.flytre_lib.impl.loader.LoaderPropertyInitializer;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 1)
public class MainMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$setLoaderProperties(String[] args, CallbackInfo ci) {
        LoaderPropertyInitializer.init(args);
    }
}
