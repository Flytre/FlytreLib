package net.flytre.flytre_lib.mixin.config;


import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.impl.config.client.ConfigListerScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Custom F3 actions
 * https://www.glfw.org/docs/latest/group__keys.html
 */
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {


    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void debugLog(String key, Object... args);

    @Inject(method = "processF3", at = @At("TAIL"), cancellable = true)
    public void flytre_lib$f3ClientConfigReload(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 89) {
            int i = ConfigRegistry.reloadClientConfigs();
            this.debugLog("flytre_lib.debug.reload_client_configs.message", i);
            cir.setReturnValue(true);
        }
        if (key == 77) {
            client.setScreen(
                    new ConfigListerScreen(null));
            this.debugLog("flytre_lib.debug.load_config_screen.message");
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getChatHud()Lnet/minecraft/client/gui/hud/ChatHud;", ordinal = 1, shift = At.Shift.AFTER))
    public void flytre_lib$addReloadConfigHelpMessage(int key, CallbackInfoReturnable<Boolean> cir) {
        this.client.inGameHud.getChatHud().addMessage(new TranslatableText("flytre_lib.debug.reload_client_configs.help"));
        this.client.inGameHud.getChatHud().addMessage(new TranslatableText("flytre_lib.debug.load_config_screen.help"));

    }
}
