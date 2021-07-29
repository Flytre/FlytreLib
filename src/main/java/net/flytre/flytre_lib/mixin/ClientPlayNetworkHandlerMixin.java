package net.flytre.flytre_lib.mixin;


import net.flytre.flytre_lib.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Trigger config onServerStatus changed event
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {


    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void flytre_lib$onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        for (ConfigHandler<?> handler : ConfigRegistry.getClientConfigs()) {
            if (handler.getConfig() instanceof ConfigEventAcceptor) {
                ((ConfigEventAcceptor) handler.getConfig()).onServerStatusChanged();
            }
        }
    }
}
