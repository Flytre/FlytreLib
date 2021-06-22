package net.flytre.flytre_lib.mixin;


import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {


    @Shadow
    protected abstract void debugLog(String key, Object... args);

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "processF3", at = @At("TAIL"), cancellable = true)
    public void flytre_lib$f3ClientConfigReload(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 89) {
            int i = ConfigRegistry.reloadClientConfigs();
            this.debugLog("flytre_lib.debug.reload_client_configs.message", i);
            cir.setReturnValue(true);
        }
    }

    @Inject(method="processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getChatHud()Lnet/minecraft/client/gui/hud/ChatHud;", ordinal = 1, shift = At.Shift.AFTER))
    public void flytre_lib$addReloadConfigHelpMessage(int key, CallbackInfoReturnable<Boolean> cir) {
        this.client.inGameHud.getChatHud().addMessage(new TranslatableText("flytre_lib.debug.reload_client_configs.help"));
    }
}
