package net.flytre.flytre_lib.mixin.base;


import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flytre.flytre_lib.impl.base.packet.ReasonablePacketHandler;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Function;

/**
 * Adds a replacement register method with less strict parameters
 */
@Mixin(NetworkState.PacketHandler.class)
public class NetworkState$PacketHandlerMixin<T extends PacketListener> implements ReasonablePacketHandler<T> {

    @Shadow
    @Final
    private List<Function<PacketByteBuf, ? extends Packet<T>>> packetFactories;

    @Shadow
    @Final
    private Object2IntMap<Class<? extends Packet<T>>> packetIds;

    //Copied from base method
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public NetworkState.PacketHandler<T> register(Class<? extends Packet<T>> type, Function<PacketByteBuf, ? extends Packet<T>> function) {
        int nextId = this.packetFactories.size();
        int didContain = this.packetIds.put(type, nextId);
        if (didContain != -1) {
            String string = "Packet " + type + " is already registered to ID " + didContain;
            LogManager.getLogger().fatal(string);
            throw new IllegalArgumentException(string);
        } else {
            this.packetFactories.add(function);
            return (NetworkState.PacketHandler<T>) (Object) this;
        }
    }
}
