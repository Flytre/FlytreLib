package net.flytre.flytre_lib.mixin.config;


import net.flytre.flytre_lib.api.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Trigger config onServerStatus changed event
 */
@Mixin(MinecraftClient.class)
class MinecraftClientMixin {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void flytre_lib$onDisconnect(Screen screen, CallbackInfo ci) {
        for (ConfigHandler<?> handler : ConfigRegistryImpl.getClientConfigs()) {
            if (handler.getConfig() instanceof ConfigEventAcceptor) {
                ((ConfigEventAcceptor) handler.getConfig()).onServerStatusChanged();
            }
        }
    }
}
