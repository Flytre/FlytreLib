package net.flytre.flytre_lib.api.config;

import com.google.gson.*;
import net.flytre.flytre_lib.api.base.util.Formatter;

import java.lang.reflect.Type;

public class ConfigColor {

    public int value;

    public ConfigColor(int value) {
        this.value = value;
    }

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
