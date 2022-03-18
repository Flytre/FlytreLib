package net.flytre.flytre_lib.api.base.util;

import net.flytre.flytre_lib.impl.base.packet.PacketUtilsImpl;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class PacketUtils {

    /**
     * Used to register a client-bound packet to the network state, allowing the packet to be sent through Minecraft's packet API
     */
    public static <P extends Packet<ClientPlayPacketListener>> void registerS2CPacket(Class<P> type, Function<PacketByteBuf, P> function) {
        PacketUtilsImpl.registerS2CPacket(type, function);
    }

    /**
     * Used to register a server-bound packet to the network state, allowing the packet to be sent through Minecraft's packet API
     */
    public static <P extends Packet<ServerPlayPacketListener>> void registerC2SPacket(Class<P> type, Function<PacketByteBuf, P> function) {
        PacketUtilsImpl.registerC2SPacket(type, function);
    }


    public static <T> void toPacket(PacketByteBuf buf, List<T> list, BiConsumer<T, PacketByteBuf> func) {
        PacketUtilsImpl.toPacket(buf, list, func);
    }

    public static <T> void toPacket(PacketByteBuf buf, Set<T> set, BiConsumer<T, PacketByteBuf> func) {
        PacketUtilsImpl.toPacket(buf, set, func);
    }

    public static <T, K> void toPacket(PacketByteBuf buf, Map<T, K> map, BiConsumer<T, PacketByteBuf> keyToPacket, BiConsumer<K, PacketByteBuf> valToPacket) {
        PacketUtilsImpl.toPacket(buf, map, keyToPacket, valToPacket);

    }

    public static <T> List<T> listFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> func) {
        return PacketUtilsImpl.listFromPacket(buf, func);
    }

    public static <T> Set<T> setFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> func) {
        return PacketUtilsImpl.setFromPacket(buf, func);
    }

    public static <T, K> Map<T, K> mapFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> packetToKey, Function<PacketByteBuf, K> packetToVal) {
        return PacketUtilsImpl.mapFromPacket(buf, packetToKey, packetToVal);

    }

    private PacketUtils() {
        throw new AssertionError();
    }
}
