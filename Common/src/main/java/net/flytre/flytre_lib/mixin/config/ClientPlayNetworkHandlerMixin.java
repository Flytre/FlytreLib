package net.flytre.flytre_lib.mixin.config;


import net.flytre.flytre_lib.api.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
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
class ClientPlayNetworkHandlerMixin {


    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void flytre_lib$onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        for (ConfigHandler<?> handler : ConfigRegistryImpl.getClientConfigs()) {
            if (handler.getConfig() instanceof ConfigEventAcceptor) {
                ((ConfigEventAcceptor) handler.getConfig()).onServerStatusChanged();
            }
        }
    }
}
