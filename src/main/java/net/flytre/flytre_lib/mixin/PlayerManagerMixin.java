package net.flytre.flytre_lib.mixin;


import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.flytre.flytre_lib.config.network.ConfigS2CPacket;
import net.flytre.flytre_lib.config.network.SyncedConfig;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    public void flytre_lib$syncConfig(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ConfigRegistry.getServerConfigs().forEach(i -> {
            if (i.getConfig() instanceof SyncedConfig)
                ServerPlayNetworking.send(player, ConfigS2CPacket.PACKET_ID, new ConfigS2CPacket(i).toPacket());
        });
    }
}
