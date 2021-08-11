package net.flytre.flytre_lib.impl.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.base.util.Formatter;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.config.annotation.Range;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {

    public static String asString(Range range) {
        return "[min: " + Formatter.formatNumber(range.min()) + ", max: " + Formatter.formatNumber(range.max()) + "]";
    }

    public static String getEnumName(Enum<?> val) throws NoSuchFieldException {
        Field field = val.getClass().getField(val.name());

        DisplayName displayName = field.getAnnotation(DisplayName.class);
        if (displayName != null)
            return displayName.value();

        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        if (serializedName != null)
            return serializedName.value();

        return val.name();
    }

    public static String enumAsStringArray(Class<?> type) {
        assert type.isEnum();
        Enum<?>[] objs = (Enum<?>[]) type.getEnumConstants();
        List<String> result = new ArrayList<>();
        for (Enum<?> val : objs) {
            try {
                result.add(ConfigHelper.getEnumName(val));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        return result.toString();
    }
}
