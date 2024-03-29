package net.flytre.flytre_lib.api.base.util.reflection;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ReflectionUtils {


    /**
     * @return all fields, including superclass fields and inaccessible fields
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();

        do {
            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return result;
    }


    /**
     * @param fields         the collection of fields to try matching
     * @param serializedName the name of the field to locate
     * @return a field that has the serializedName specified when serialized, or null if none is found
     */
    public static @Nullable FieldMatch match(Collection<Field> fields, String serializedName) {
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()))
                continue;

            SerializedName nameAnnotation = field.getAnnotation(SerializedName.class);
            if ((nameAnnotation != null && nameAnnotation.value().equals(serializedName)) || (nameAnnotation == null && field.getName().equals(serializedName))) {
                String key = nameAnnotation != null ? nameAnnotation.value() : field.getName();
                return new FieldMatch(field, key);
            }
        }
        return null;
    }


    private ReflectionUtils() {
        throw new AssertionError();
    }

}
