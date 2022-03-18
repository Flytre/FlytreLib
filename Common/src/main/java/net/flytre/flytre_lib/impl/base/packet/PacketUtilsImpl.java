package net.flytre.flytre_lib.impl.base.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@ApiStatus.Internal
public final class PacketUtilsImpl {


    public static final Map<Identifier, PacketData> REGISTERED_IDS = new HashMap<>();
    public static final Map<Class<?>, PacketData> REGISTERED_TYPES = new HashMap<>();
    public static final List<Triad<? extends Packet<ServerPlayPacketListener>>> PLAY_C2S_PACKET = new ArrayList<>();
    public static final List<Triad<? extends Packet<ClientPlayPacketListener>>> PLAY_S2C_PACKET = new ArrayList<>();

    private PacketUtilsImpl() {
    }


    private static Identifier toId(Class<?> type) {
        return new Identifier("flytre_lib", type.getName().toLowerCase().replaceAll("[^a-zA-Z0-9_/.-]", "_"));
    }

    public static <P extends Packet<ClientPlayPacketListener>> void registerS2CPacket(Class<P> type, Function<PacketByteBuf, P> function) {
        Identifier id = toId(type);
        PLAY_S2C_PACKET.add(new Triad<>(type, function, toId(type)));
        PacketData data = new PacketData(true, PLAY_S2C_PACKET.size() - 1);
        REGISTERED_IDS.put(id, data);
        REGISTERED_TYPES.put(type, data);
    }

    public static <P extends Packet<ServerPlayPacketListener>> void registerC2SPacket(Class<P> type, Function<PacketByteBuf, P> function) {
        Identifier id = toId(type);
        PLAY_C2S_PACKET.add(new Triad<>(type, function, toId(type)));
        PacketData data = new PacketData(false, PLAY_C2S_PACKET.size() - 1);
        REGISTERED_IDS.put(id, data);
        REGISTERED_TYPES.put(type, data);
    }


    public static <T> void toPacket(PacketByteBuf buf, List<T> list, BiConsumer<T, PacketByteBuf> func) {
        buf.writeInt(list.size());
        for (T element : list) {
            func.accept(element, buf);
        }
    }

    public static <T> void toPacket(PacketByteBuf buf, Set<T> set, BiConsumer<T, PacketByteBuf> func) {
        buf.writeInt(set.size());
        for (T element : set) {
            func.accept(element, buf);
        }
    }

    public static <T, K> void toPacket(PacketByteBuf buf, Map<T, K> map, BiConsumer<T, PacketByteBuf> keyToPacket, BiConsumer<K, PacketByteBuf> valToPacket) {
        buf.writeInt(map.keySet().size());
        for (T key : map.keySet()) {
            keyToPacket.accept(key, buf);
            valToPacket.accept(map.get(key), buf);
        }
    }

    public static <T> List<T> listFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> func) {
        int size = buf.readInt();
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(func.apply(buf));
        }
        return result;
    }

    public static <T> Set<T> setFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> func) {
        int size = buf.readInt();
        Set<T> result = new HashSet<>();
        for (int i = 0; i < size; i++) {
            result.add(func.apply(buf));
        }
        return result;
    }

    public static <T, K> Map<T, K> mapFromPacket(PacketByteBuf buf, Function<PacketByteBuf, T> packetToKey, Function<PacketByteBuf, K> packetToVal) {
        Map<T, K> result = new HashMap<>();
        int hashSize = buf.readInt();
        for (int i = 0; i < hashSize; i++) {
            T key = packetToKey.apply(buf);
            K value = packetToVal.apply(buf);
            result.put(key, value);
        }
        return result;
    }


    public record Triad<P>(Class<P> type, Function<PacketByteBuf, P> creator, Identifier channel) {

    }

    public record PacketData(boolean clientbound, int index) {
    }
}
