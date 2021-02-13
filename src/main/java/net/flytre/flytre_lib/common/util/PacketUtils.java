package net.flytre.flytre_lib.common.util;

import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketUtils {


    public static <T> void toPacket(PacketByteBuf buf, List<T> list, BiConsumer<T, PacketByteBuf> func) {
        buf.writeInt(list.size());
        for (T element : list) {
            func.accept(element, buf);
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

    public static <T, K> void toPacket(PacketByteBuf buf, Map<T, K> map, BiConsumer<T, PacketByteBuf> keyToPacket, BiConsumer<K, PacketByteBuf> valToPacket) {
        buf.writeInt(map.keySet().size());
        for (T key : map.keySet()) {
            keyToPacket.accept(key, buf);
            valToPacket.accept(map.get(key), buf);
        }
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
}
