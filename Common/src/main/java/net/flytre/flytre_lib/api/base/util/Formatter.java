package net.flytre.flytre_lib.api.base.util;

import com.google.common.collect.Maps;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to format text and numbers in various situations
 */
public final class Formatter {

    private static final String[] PREFIX_VALUES = new String[]{"", "k", "M", "G", "T", "P"};
    private static final Map<String, String> MOD_NAME_CACHE = Maps.newHashMap();
    private static final String FORMAT_INT = "%d";
    private static final String FORMAT_FLOAT = "%.2f";


    private Formatter() {
        throw new AssertionError();
    }

    /**
     * Stores a map into an integer than can then be converted back into a map for serialization
     * purposes.
     */
    public static int mapToInt(Map<Direction, Boolean> map) {
        boolean[] array = new boolean[]{
                map.get(Direction.NORTH),
                map.get(Direction.WEST),
                map.get(Direction.EAST),
                map.get(Direction.UP),
                map.get(Direction.DOWN),
                map.get(Direction.SOUTH)};
        return booleansToInt(array);
    }

    /**
     * Converts a direction map represented as a boolean back into a full scale map
     */
    public static Map<Direction, Boolean> intToMap(int n) {
        boolean[] array = intsToBoolean(n, 6);
        HashMap<Direction, Boolean> result = new HashMap<>();
        result.put(Direction.NORTH, array[0]);
        result.put(Direction.WEST, array[1]);
        result.put(Direction.EAST, array[2]);
        result.put(Direction.UP, array[3]);
        result.put(Direction.DOWN, array[4]);
        result.put(Direction.SOUTH, array[5]);
        return result;
    }

    /**
     * Convery a boolean array to an int
     *
     * @param arr the arr
     * @return the int
     */
    public static int booleansToInt(boolean[] arr) {
        int n = 0;
        for (int i = -1; i < arr.length - 1; n += arr[++i] ? 1 << i : 0) ;
        return n;
    }

    /**
     * Convert an int to a boolean array
     *
     * @param n    the n
     * @param size the size
     * @return the boolean [ ]
     */
    public static boolean[] intsToBoolean(int n, int size) {
        boolean[] result = new boolean[size];
        for (int i = size - 1, c = 1 << i; i >= 0; c = 1 << --i) {
            result[i] = n >= c;
            n = n >= c ? n - c : n;
        }
        return result;
    }

    /**
     * Split an int into mock-scientific notation
     *
     * @param x the x
     * @return the int [ ]
     */
    public static int[] splitInt(int x) {
        int[] result = new int[]{x, x == 0 ? 0 : Math.max((int) Math.log10(x) - 3, 0)};
        while (result[0] >= 10000)
            result[0] /= 10;
        return result;
    }

    /**
     * Unsplit int from mock-scientific notation.
     *
     * @param split the split
     * @return the int
     */
    public static int unsplit(int[] split) {
        return (int) (split[0] * Math.pow(10, split[1]));
    }

    /**
     * Format a number to a String, for example 1000B = 1kB.
     *
     * @param num    the num
     * @param suffix the suffix
     * @return the string
     */
    public static String formatNumber(double num, String suffix) {
        return formatNumber(num, suffix, false);
    }

    public static String formatNumber(double num, String suffix, boolean space) {
        int suffixIndex = 0;
        while (num >= 1000.0) {
            suffixIndex++;
            num /= 1000.0;
        }

        String prefix = PREFIX_VALUES[suffixIndex];

        if (num == 0)
            prefix = PREFIX_VALUES[0];
        else if (num < 1) {
            num *= 1000;
            prefix = "m";
        }

        int format = num >= 100 ? 1 : 2;
        return String.format("%." + format + "f", num) + (space ? " " : "") + prefix + suffix;
    }

    /**
     * Formats a number with no units
     */
    public static String formatNumber(double value) {
        if (value > 1000)
            return Formatter.formatNumber(value, "", false);
        else if (value == (int) value)
            return String.format(FORMAT_INT, (int) value);
        return String.format(FORMAT_FLOAT, value);
    }

    /**
     * Gets the name of a mod from its id
     *
     * @param modId the mod to get the name of
     * @return Human-readable name of the mod
     */
    public static String getModFromModId(String modId) {
        if (modId == null)
            return "";
        String any = MOD_NAME_CACHE.getOrDefault(modId, null);
        if (any != null)
            return any;
        String name = LoaderProperties.getModName(modId);
        MOD_NAME_CACHE.put(modId, name);
        return name;
    }

    /**
     * @param modId the mod to get the name of
     * @return Human-readable text formatted name of the mod
     */
    public static Text getModNameToolTip(String modId) {
        MutableText name = Text.literal(getModFromModId(modId)).append(Text.empty());
        name.setStyle(Style.EMPTY.withColor(Formatting.BLUE).withItalic(true));
        return name;
    }

    /**
     * @param arr an int[3] array storing the position of the block pos
     * @return The block pos representing the position
     */
    public static BlockPos arrToPos(int[] arr) {
        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    /**
     * @param pos The block pos
     * @return An int array tag representing the block pos
     */

    public static NbtIntArray writePosToNbt(BlockPos pos) {
        return new NbtIntArray(new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    /**
     * Get energy from delegate (hardcoded indices 3 and 4)
     *
     * @param propertyDelegate the property delegate
     * @return the int
     */
    public static int energy(PropertyDelegate propertyDelegate) {
        return Formatter.unsplit(new int[]{propertyDelegate.get(3), propertyDelegate.get(4)});
    }

    /**
     * Get max energy from delegate (hardcoded indices 5 and 6)
     *
     * @param propertyDelegate the property delegate
     * @return the int
     */
    public static int maxEnergy(PropertyDelegate propertyDelegate) {
        return Math.max(Formatter.unsplit(new int[]{propertyDelegate.get(5), propertyDelegate.get(6)}), 1);
    }

    /**
     * Convert Mechanix -> TR and vice versa
     */
    public static double sigmasEU(double joules) {
        return joules / 10;
    }

    public static double EUsigmas(double EU) {
        return EU * 10;
    }

    /**
     * Convert an integer to a formatted readable hex string, i.e. #45AC2B
     */
    public static String intToHexString(int value) {
        String hex = String.format("#%02x%02x%02x%02x", value >> 24 & 255, value >> 16 & 255, value >> 8 & 255, value & 255);
        if ((hex.startsWith("#00") && hex.length() == 9))
            hex = "#" + hex.substring(3);
        return hex;
    }

    /**
     * Convert a formatted hex string to an int
     */
    public static int fromHexString(String hex) {
        int color;
        if (hex.startsWith("#") && hex.length() == 9) {
            Color awt = Color.decode("#" + hex.substring(3));
            int alpha = Integer.decode(hex.substring(0, 3));
            color = awt.getRGB() + (alpha << 24);
        } else {
            color = Color.decode(hex).getRGB();
        }
        return color;
    }
}
