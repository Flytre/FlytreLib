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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        }.getType(), new SetSerializer<>(Function.identity(),Function.identity()));


        GSON_BUILDER.registerTypeAdapter(EntityType.class, new IdentifierBasedSerializer<EntityType<?>>(Registry.ENTITY_TYPE::get, Registry.ENTITY_TYPE::getId));
        GSON_BUILDER.registerTypeAdapter(Fluid.class, new IdentifierBasedSerializer<>(Registry.FLUID::get, Registry.FLUID::getId));
        GSON_BUILDER.registerTypeAdapter(StatusEffect.class, new IdentifierBasedSerializer<>(Registry.STATUS_EFFECT::get, Registry.STATUS_EFFECT::getId));
        GSON_BUILDER.registerTypeAdapter(Block.class, new IdentifierBasedSerializer<>(Registry.BLOCK::get, Registry.BLOCK::getId));
        GSON_BUILDER.registerTypeAdapter(Enchantment.class, new IdentifierBasedSerializer<>(Registry.ENCHANTMENT::get, Registry.ENCHANTMENT::getId));
        GSON_BUILDER.registerTypeAdapter(Item.class, new IdentifierBasedSerializer<>(Registry.ITEM::get, Registry.ITEM::getId));
        GSON_BUILDER.registerTypeAdapter(EntityAttribute.class, new IdentifierBasedSerializer<>(Registry.ATTRIBUTE::get, Registry.ATTRIBUTE::getId));
        GSON_BUILDER.registerTypeAdapter(SoundEvent.class, new IdentifierBasedSerializer<>(Registry.SOUND_EVENT::get, Registry.SOUND_EVENT::getId));
        GSON_BUILDER.registerTypeAdapter(VillagerProfession.class, new IdentifierBasedSerializer<>(Registry.VILLAGER_PROFESSION::get, Registry.VILLAGER_PROFESSION::getId));

        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<EntityType<?>>>(){}.getType(),new SetSerializer<>(Registry.ENTITY_TYPE::get, Registry.ENTITY_TYPE::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<Fluid>>(){}.getType(),new SetSerializer<>(Registry.FLUID::get, Registry.FLUID::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<StatusEffect>>(){}.getType(),new SetSerializer<>(Registry.STATUS_EFFECT::get, Registry.STATUS_EFFECT::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<Block>>(){}.getType(),new SetSerializer<>(Registry.BLOCK::get, Registry.BLOCK::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<Enchantment>>(){}.getType(),new SetSerializer<>(Registry.ENCHANTMENT::get, Registry.ENCHANTMENT::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<Item>>(){}.getType(),new SetSerializer<>(Registry.ITEM::get, Registry.ITEM::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<EntityAttribute>>(){}.getType(),new SetSerializer<>(Registry.ATTRIBUTE::get, Registry.ATTRIBUTE::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<SoundEvent>>(){}.getType(),new SetSerializer<>(Registry.SOUND_EVENT::get, Registry.SOUND_EVENT::getId));
        GSON_BUILDER.registerTypeAdapter(new TypeToken<Set<VillagerProfession>>(){}.getType(),new SetSerializer<>(Registry.VILLAGER_PROFESSION::get, Registry.VILLAGER_PROFESSION::getId));

        GSON = GSON_BUILDER.create();
    }

    public static class IdentifierBasedSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

        private final Function<Identifier, T> fromId;
        private final Function<T, Identifier> toId;

        public IdentifierBasedSerializer(Function<Identifier, T> fromId, Function<T, Identifier> toId) {
            this.fromId = fromId;
            this.toId = toId;
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

    public static class SetSerializer<T> implements JsonSerializer<Set<T>>, JsonDeserializer<Set<T>> {

        private final Function<Identifier, T> fromId;
        private final Function<T, Identifier> toId;

        public SetSerializer(Function<Identifier, T> fromId, Function<T, Identifier> toId) {
            this.toId = toId;
            this.fromId = fromId;
        }

        @Override
        public JsonElement serialize(Set<T> identifiers, Type type, JsonSerializationContext jsonSerializationContext) {
            return jsonSerializationContext.serialize(identifiers.stream().map(toId).sorted().collect(Collectors.toList()));
        }

        @Override
        public Set<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<Identifier> ids = GSON.fromJson(json, new TypeToken<HashSet<Identifier>>() {
            }.getType());
            return ids.stream().map(fromId).collect(Collectors.toSet());
        }
    }

    public static class MapDeserializer<K, V> implements JsonDeserializer<Map<K, V>> {

        private final Function<Identifier, K> keyFromId;

        public MapDeserializer(Function<Identifier, K> fromId) {
            this.keyFromId = fromId;
        }

        @Override
        public Map<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<Identifier, V> identifierVMap = GSON.fromJson(json, new TypeToken<HashMap<Identifier, V>>() {
            }.getType());

            Map<K, V> result = new HashMap<>();
            for (var entry : identifierVMap.entrySet()) {
                result.put(keyFromId.apply(entry.getKey()), entry.getValue());
            }
            return result;
        }
    }
}
