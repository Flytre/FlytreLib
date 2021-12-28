package net.flytre.flytre_lib.mixin.loader;


import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(GameData.class)
public abstract class GameDataMixin {


    @Inject(method = "checkPrefix", at = @At(value = "HEAD"), cancellable = true)
    private static void flytre_lib$silenceWarnings(String name, boolean warnOverrides, CallbackInfoReturnable<Identifier> cir) {
        String namespace = ModLoadingContext.get().getActiveNamespace();
        if (namespace.equals("flytre_lib") && warnOverrides) {
            int index = name.lastIndexOf(58);
            String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
            name = index == -1 ? name : name.substring(index + 1);
            cir.setReturnValue(new Identifier(oldPrefix, name));
        }
    }

}
