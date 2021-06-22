package net.flytre.flytre_lib.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GsonHelper {

    public static final Identifier.Serializer IDENTIFIER_SERIALIZER = new Identifier.Serializer();


    public static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    public static final Gson GSON;

    static {
        GSON_BUILDER.setPrettyPrinting();
        GSON_BUILDER.registerTypeAdapter(Identifier.class, IDENTIFIER_SERIALIZER);
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<Identifier>>() {
        }.getType(), new SetDeserializer<>(i -> i));


        GSON_BUILDER.registerTypeAdapter(EntityType.class, new IdentifierBasedSerializer<EntityType<?>>(Registry.ENTITY_TYPE::get, Registry.ENTITY_TYPE::getId));
        GSON_BUILDER.registerTypeAdapter(Fluid.class, new IdentifierBasedSerializer<>(Registry.FLUID::get, Registry.FLUID::getId));
        GSON_BUILDER.registerTypeAdapter(StatusEffect.class, new IdentifierBasedSerializer<>(Registry.STATUS_EFFECT::get, Registry.STATUS_EFFECT::getId));
        GSON_BUILDER.registerTypeAdapter(Block.class, new IdentifierBasedSerializer<>(Registry.BLOCK::get, Registry.BLOCK::getId));
        GSON_BUILDER.registerTypeAdapter(Enchantment.class, new IdentifierBasedSerializer<>(Registry.ENCHANTMENT::get, Registry.ENCHANTMENT::getId));
        GSON_BUILDER.registerTypeAdapter(Item.class, new IdentifierBasedSerializer<>(Registry.ITEM::get, Registry.ITEM::getId));
        GSON_BUILDER.registerTypeAdapter(EntityAttribute.class, new IdentifierBasedSerializer<>(Registry.ATTRIBUTE::get, Registry.ATTRIBUTE::getId));

        GSON = GSON_BUILDER.create();
    }

    public static class IdentifierBasedSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

        private final Function<Identifier, T> fromId;
        private final Function<T, Identifier> toId;

        private IdentifierBasedSerializer(Function<Identifier, T> fromId, Function<T, Identifier> toId) {
            this.fromId = fromId;
            this.toId = toId;
            GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<T>>() {
            }.getType(), new SetDeserializer<>(fromId));
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return fromId.apply(GSON.fromJson(json, Identifier.class));
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            Identifier id = toId.apply(src);
            return IDENTIFIER_SERIALIZER.serialize(id, id.getClass(), context);
        }
    }

    public static class SetDeserializer<T> implements JsonDeserializer<Set<T>> {

        private final Function<Identifier, T> fromId;

        private SetDeserializer(Function<Identifier, T> fromId) {
            this.fromId = fromId;
        }

        @Override
        public Set<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<Identifier> ids = GSON.fromJson(json, new TypeToken<HashSet<Identifier>>() {
            }.getType());
            return ids.stream().map(fromId).collect(Collectors.toSet());
        }
    }
}
