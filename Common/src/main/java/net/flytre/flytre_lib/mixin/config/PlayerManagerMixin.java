package net.flytre.flytre_lib.mixin.config;


import net.flytre.flytre_lib.api.config.network.SyncedConfig;
import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
import net.flytre.flytre_lib.impl.config.ConfigS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sync configs to the player when they join a server (Server side -> client)
 */
@Mixin(PlayerManager.class)
class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    public void flytre_lib$syncConfig(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ConfigRegistryImpl.getServerConfigs().forEach(i -> {
            if (i.getConfig() instanceof SyncedConfig)
                player.networkHandler.sendPacket(new ConfigS2CPacket(i));
        });
    }
}
