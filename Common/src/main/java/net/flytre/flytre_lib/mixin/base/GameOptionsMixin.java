package net.flytre.flytre_lib.mixin.base;

import net.flytre.flytre_lib.impl.base.KeyBindUtilsImpl;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Registers custom keybinds to Minecraft
 */
@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Mutable
    @Final
    @Shadow
    public KeyBinding[] keysAll;

    @Inject(at = @At("HEAD"), method = "load()V")
    public void flytre_lib$registerCustomBinds(CallbackInfo info) {
        keysAll = KeyBindUtilsImpl.process(keysAll);
    }
}
