package net.flytre.flytre_lib.api.config;

import com.google.gson.*;
import net.flytre.flytre_lib.api.base.util.Formatter;

import java.lang.reflect.Type;

/**
 * Represents a color as an object.
 */
public final class ConfigColor {

    public int value;

    public ConfigColor(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigColor{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigColor that = (ConfigColor) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * Custom serializer for ConfigColor
     */
    public static class ColorSerializer implements JsonSerializer<ConfigColor>, JsonDeserializer<ConfigColor> {

        @Override
        public ConfigColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ConfigColor(Formatter.fromHexString(json.getAsString()));
        }

        @Override
        public JsonElement serialize(ConfigColor src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Formatter.intToHexString(src.value));
        }
    }
}
