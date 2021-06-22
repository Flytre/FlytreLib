package net.flytre.flytre_lib.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class GsonHelper {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .registerTypeAdapter(Set.class, new IdentifierSetDeserializer())
            .create();



    private static class IdentifierSetDeserializer implements JsonDeserializer<Set<Identifier>> {
        @Override
        public Set<Identifier> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return GSON.fromJson(json, new TypeToken<HashSet<Identifier>>() {
            }.getType());
        }
    }
}
