package net.flytre.flytre_lib.api.storage.inventory;

import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * IOTypes control the input-output status of a specific side of a block.
 */
public enum IOType {

    INPUT(1, true, false),
    OUTPUT(0, false, true),
    BOTH(2, true, true),
    NEITHER(3, false, false);

    private final int index;
    private final boolean insert;
    private final boolean extract;

    IOType(int i, boolean insert, boolean extract) {
        this.index = i;
        this.insert = insert;
        this.extract = extract;
    }

    public static IOType byId(int id) {
        for (IOType type : IOType.values()) {
            if (type.index == id)
                return type;
        }
        throw new AssertionError("Invalid id");
    }

    public static Map<Direction, IOType> intToMap(int n) {
        int[] array = fromInt(n, 6);
        HashMap<Direction, IOType> result = new HashMap<>();
        result.put(Direction.NORTH, byId(array[0]));
        result.put(Direction.WEST, byId(array[1]));
        result.put(Direction.EAST, byId(array[2]));
        result.put(Direction.UP, byId(array[3]));
        result.put(Direction.DOWN, byId(array[4]));
        result.put(Direction.SOUTH, byId(array[5]));
        return result;
    }

    public static int mapToInt(Map<Direction, IOType> map) {
        int[] array = new int[]{
                map.get(Direction.NORTH).index,
                map.get(Direction.WEST).index,
                map.get(Direction.EAST).index,
                map.get(Direction.UP).index,
                map.get(Direction.DOWN).index,
                map.get(Direction.SOUTH).index};
        return toInt(array);
    }

    public static int toInt(int[] arr) {
        int n = 0;
        for (int i = -1; i < arr.length - 1; n += arr[++i] * Math.pow(IOType.values().length, i)) ;
        return n;
    }

    public static int[] fromInt(int n, int size) {
        int[] result = new int[size];
        for (int i = size - 1, c = (int) Math.pow(IOType.values().length, i); i-- >= 0; c /= IOType.values().length) {
            result[i + 1] = c > 0 ? n / c : 0;
            n %= c;
        }
        return result;
    }

    public boolean canInsert() {
        return insert;
    }

    public boolean canExtract() {
        return extract;
    }

    public int getIndex() {
        return index;
    }


}
