package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.impl.base.packet.PacketUtilsImpl;
import net.flytre.flytre_lib.impl.base.packet.ReasonablePacketHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Registers custom packets to Minecraft
 */
@Mixin(NetworkState.class)
public class NetworkStateMixin {


    @Shadow
    @Final
    private Map<NetworkSide, ? extends NetworkState.PacketHandler<?>> packetHandlers;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("TAIL"))
    public void flytre_lib$registerPackets(String enumName, int ordinal, int id, NetworkState.PacketHandlerInitializer packetHandlerInitializer, CallbackInfo ci) {
        if (id == 0) {
            Map<NetworkSide, ? extends NetworkState.PacketHandler<?>> map = this.packetHandlers;
            NetworkState.PacketHandler<?> serverBound = map.get(NetworkSide.SERVERBOUND);
            NetworkState.PacketHandler<?> clientBound = map.get(NetworkSide.CLIENTBOUND);

            for (var packet : PacketUtilsImpl.getPlayC2SPackets()) {
                ((ReasonablePacketHandler<ServerPlayPacketListener>) serverBound).register(packet.getLeft(), packet.getRight());
            }

            for (var packet : PacketUtilsImpl.getPlayS2CPackets()) {
                ((ReasonablePacketHandler<ClientPlayPacketListener>) clientBound).register(packet.getLeft(), packet.getRight());
            }
        }
    }
}
