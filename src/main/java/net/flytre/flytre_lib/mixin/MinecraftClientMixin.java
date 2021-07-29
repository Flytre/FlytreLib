package net.flytre.flytre_lib.mixin;



import net.flytre.flytre_lib.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
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
public class MinecraftClientMixin {

    @Inject(method="disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void flytre_lib$onDisconnect(Screen screen, CallbackInfo ci) {
        for (ConfigHandler<?> handler : ConfigRegistry.getClientConfigs()) {
            if (handler.getConfig() instanceof ConfigEventAcceptor) {
                ((ConfigEventAcceptor) handler.getConfig()).onServerStatusChanged();
            }
        }
    }
}
