package net.flytre.flytre_lib.impl.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.base.util.Formatter;
import net.flytre.flytre_lib.api.base.util.reflection.FieldMatch;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.config.annotation.MemberLocalizationFunction;
import net.flytre.flytre_lib.api.config.annotation.Range;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static String asString(Range range) {
        return "[min: " + Formatter.formatNumber(range.min()) + ", max: " + Formatter.formatNumber(range.max()) + "]";
    }

    public static String getEnumName(Enum<?> val, boolean useDisplayName) {
        Field field;
        try {
            field = val.getClass().getField(val.name());
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Enum value " + val + " was remapped. Tell the developer to modify obfuscation settings."); //will never happen
        }

        if (useDisplayName) {
            Method[] methods = val.getClass().getMethods();
            for (Method method : methods) {
                MemberLocalizationFunction anno = method.getAnnotation(MemberLocalizationFunction.class);
                if (anno != null) {
                    method.setAccessible(true);
                    try {
                        Object result = method.invoke(val);
                        return result.toString();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new AssertionError("Member Localization Function " + method.getName() + " in class " + val.getClass().getName() + " failed");
                    }
                }
            }
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


        if (objs == null) {
            return "[Error: could not get possible values]";
        }

        List<String> result = new ArrayList<>();

        for (Enum<?> val : objs)
            result.add(ConfigHelper.getEnumName(val, false));

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
