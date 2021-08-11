package net.flytre.flytre_lib.api.base.util.reflection;

import java.lang.reflect.Field;

/**
 * A field match contains a field paired with its serialized name
 */
public record FieldMatch(Field field, String serializedName) {
}
