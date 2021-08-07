package net.flytre.flytre_lib.impl.base.packet;

import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

import java.util.function.Function;

public interface ReasonablePacketHandler<T extends PacketListener> {

    NetworkState.PacketHandler<T> register(Class<? extends Packet<T>> type, Function<PacketByteBuf, ? extends Packet<T>> function);
}
