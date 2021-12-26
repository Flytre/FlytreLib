package net.flytre.flytre_lib.impl.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.base.util.Formatter;
import net.flytre.flytre_lib.api.base.util.reflection.FieldMatch;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.config.annotation.Range;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {

    public static String asString(Range range) {
        return "[min: " + Formatter.formatNumber(range.min()) + ", max: " + Formatter.formatNumber(range.max()) + "]";
    }

    public static String getEnumName(Enum<?> val, boolean useDisplayName) {
        Field field;
        try {
            field = val.getClass().getField(val.name());
        } catch (NoSuchFieldException e) {
            throw new AssertionError(); //will never happen
        }

        DisplayName display = field.getAnnotation(DisplayName.class);
        if (display != null && useDisplayName)
            return display.translationKey() ? I18n.translate(display.value()) : display.value();

        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        if (serializedName != null)
            return serializedName.value();

        return val.name();
    }

    public static String getName(ConfigHandler<?> handler, FieldMatch fieldMatch) {


        DisplayName display = fieldMatch.field().getAnnotation(DisplayName.class);
        if (display != null)
            return display.translationKey() ? I18n.translate(display.value()) : display.value();

        if (handler.getTranslationPrefix() != null && I18n.hasTranslation(handler.getTranslationPrefix() + "." + fieldMatch.serializedName())) {
            return I18n.translate(handler.getTranslationPrefix() + "." + fieldMatch.serializedName());
        }

        String base = fieldMatch.serializedName() != null ? fieldMatch.serializedName() : fieldMatch.field().getName();
        base = base.replaceAll("_", " ");
        return WordUtils.capitalize(base);
    }

    public static String enumAsStringArray(Class<?> type) {
        assert type.isEnum();
        Enum<?>[] objs = (Enum<?>[]) type.getEnumConstants();
        List<String> result = new ArrayList<>();

        for (Enum<?> val : objs)
            result.add(ConfigHelper.getEnumName(val,false));

        return result.toString();
    }

    public static String getDescription(FieldMatch match) {
        Description descriptionAnnotation = match.field().getAnnotation(Description.class);
        return descriptionAnnotation == null ? "" : descriptionAnnotation.value();
    }

    public static int getWidth() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static Object convertDouble(Class<?> fieldClass, double value) {
        if (Integer.class.isAssignableFrom(fieldClass) || int.class.isAssignableFrom(fieldClass))
            return (int) value;
        if (Double.class.isAssignableFrom(fieldClass) || double.class.isAssignableFrom(fieldClass))
            return value;
        if (Long.class.isAssignableFrom(fieldClass) || long.class.isAssignableFrom(fieldClass))
            return (long) value;
        if (Byte.class.isAssignableFrom(fieldClass) || byte.class.isAssignableFrom(fieldClass))
            return (byte) value;
        if (Short.class.isAssignableFrom(fieldClass) || short.class.isAssignableFrom(fieldClass))
            return (short) value;
        if (Float.class.isAssignableFrom(fieldClass) || float.class.isAssignableFrom(fieldClass))
            return (float) value;
        if (BigInteger.class.isAssignableFrom(fieldClass))
            return BigDecimal.valueOf(value).toBigInteger();
        if (BigDecimal.class.isAssignableFrom(fieldClass))
            return BigDecimal.valueOf(value);

        throw new IllegalArgumentException("Unknown class:" + fieldClass);
    }

}