package net.flytre.flytre_lib.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.flytre.flytre_lib.config.reference.*;
import net.flytre.flytre_lib.config.reference.block.BlockReference;
import net.flytre.flytre_lib.config.reference.block.BlockTagReference;
import net.flytre.flytre_lib.config.reference.block.ConfigBlock;
import net.flytre.flytre_lib.config.reference.entity.ConfigEntity;
import net.flytre.flytre_lib.config.reference.entity.EntityReference;
import net.flytre.flytre_lib.config.reference.entity.EntityTagReference;
import net.flytre.flytre_lib.config.reference.fluid.ConfigFluid;
import net.flytre.flytre_lib.config.reference.fluid.FluidReference;
import net.flytre.flytre_lib.config.reference.fluid.FluidTagReference;
import net.flytre.flytre_lib.config.reference.item.ConfigItem;
import net.flytre.flytre_lib.config.reference.item.ItemReference;
import net.flytre.flytre_lib.config.reference.item.ItemTagReference;
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
import java.util.*;
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
        }.getType(), new IdentifierBasedSetSerializer<>(Function.identity(), Function.identity()));


        GSON_BUILDER.registerTypeAdapter(Set.class, (JsonSerializer<Set<?>>) (src, typeOfSrc, context) -> {
            JsonArray array = new JsonArray();
            src.stream().map(context::serialize).sorted(Comparator.comparing(i -> i.isJsonPrimitive() ? i.getAsString() : "")).forEach(array::add);
            return array;
        });

        GSON_BUILDER.registerTypeAdapter(ConfigColor.class, new ConfigColor.ColorSerializer());

        registerRegistryBasedClass(EntityType.class, GSON_BUILDER, Registry.ENTITY_TYPE);
        registerRegistryBasedClass(Fluid.class, GSON_BUILDER, Registry.FLUID);
        registerRegistryBasedClass(StatusEffect.class, GSON_BUILDER, Registry.STATUS_EFFECT);
        registerRegistryBasedClass(Block.class, GSON_BUILDER, Registry.BLOCK);
        registerRegistryBasedClass(Enchantment.class, GSON_BUILDER, Registry.ENCHANTMENT);
        registerRegistryBasedClass(Item.class, GSON_BUILDER, Registry.ITEM);
        registerRegistryBasedClass(EntityAttribute.class, GSON_BUILDER, Registry.ATTRIBUTE);
        registerRegistryBasedClass(SoundEvent.class, GSON_BUILDER, Registry.SOUND_EVENT);
        registerRegistryBasedClass(VillagerProfession.class, GSON_BUILDER, Registry.VILLAGER_PROFESSION);


        registerReference(EntityReference.class, GSON_BUILDER, EntityReference::new);
        registerReference(FluidReference.class, GSON_BUILDER, FluidReference::new);
        registerReference(StatusEffectReference.class, GSON_BUILDER, StatusEffectReference::new);
        registerReference(BiomeReference.class, GSON_BUILDER, BiomeReference::new);
        registerReference(DimensionReference.class, GSON_BUILDER, DimensionReference::new);
        registerReference(BlockReference.class, GSON_BUILDER, BlockReference::new);
        registerReference(EnchantmentReference.class, GSON_BUILDER, EnchantmentReference::new);
        registerReference(ItemReference.class, GSON_BUILDER, ItemReference::new);
        registerReference(AttributeReference.class, GSON_BUILDER, AttributeReference::new);
        registerReference(SoundEventReference.class, GSON_BUILDER, SoundEventReference::new);
        registerReference(VillagerProfessionReference.class, GSON_BUILDER, VillagerProfessionReference::new);
        registerReference(AdvancementReference.class, GSON_BUILDER, AdvancementReference::new);

        registerTag(BlockTagReference.class, GSON_BUILDER, BlockTagReference::new);
        registerTag(EntityTagReference.class, GSON_BUILDER, EntityTagReference::new);
        registerTag(FluidTagReference.class, GSON_BUILDER, FluidTagReference::new);
        registerTag(ItemTagReference.class, GSON_BUILDER, ItemTagReference::new);

        GSON_BUILDER.registerTypeAdapter(ConfigBlock.class, new ConfigXDeserializer<>(BlockTagReference.class, BlockReference.class));
        GSON_BUILDER.registerTypeAdapter(ConfigEntity.class, new ConfigXDeserializer<>(EntityTagReference.class, EntityReference.class));
        GSON_BUILDER.registerTypeAdapter(ConfigFluid.class, new ConfigXDeserializer<>(FluidTagReference.class, FluidReference.class));
        GSON_BUILDER.registerTypeAdapter(ConfigItem.class, new ConfigXDeserializer<>(ItemTagReference.class, ItemReference.class));


        GSON = GSON_BUILDER.create();
    }

    /**
     * Used to add Gson support for a class based around a registry, i.e. Block or EntityType
     */
    public static void registerRegistryBasedClass(Class<?> clazz, GsonBuilder builder, Registry<?> registry) {
        builder.registerTypeAdapter(clazz, new IdentifierBasedSerializer<>(registry));
        builder.registerTypeAdapter(TypeToken.getParameterized(Set.class, clazz).getType(), new IdentifierBasedSetSerializer<>(registry));
    }


    /**
     * Used to add Gson support for a reference class, i.e. BlockReference or EntityReference
     */
    public static <T> void registerReference(Class<?> clazz, GsonBuilder builder, Function<Identifier, Reference<T>> toReference) {
        builder.registerTypeAdapter(clazz, new ReferenceSerializer<>(toReference));
        builder.registerTypeAdapter(TypeToken.getParameterized(Set.class, clazz).getType()
                , new IdentifierBasedSetSerializer<>(toReference, Reference::getIdentifier));
    }


    /**
     * Used to add Gson support for a tag reference class, i.e. BlockTagReference or EntityTagReference. #s are not present in the
     * input to the toReference parameter.
     */
    public static <T> void registerTag(Class<?> clazz, GsonBuilder builder, Function<Identifier, Reference<T>> toReference) {

        Function<JsonPrimitive, Reference<T>> reference = json -> {
            String str = json.getAsString();
            if (!str.startsWith("#"))
                throw new JsonParseException("Tried to parse " + str + " as a tag but does not start with #");
            return toReference.apply(new Identifier(str.substring(1)));
        };

        Function<Reference<T>, JsonPrimitive> element = ref -> new JsonPrimitive("#" + ref.getIdentifier().toString());

        builder.registerTypeAdapter(clazz, (JsonSerializer<Reference<T>>) (src, typeOfSrc, context) -> element.apply(src));
        builder.registerTypeAdapter(clazz, (JsonDeserializer<Reference<T>>) (json, typeOfT, context) -> reference.apply(json.getAsJsonPrimitive()));
        builder.registerTypeAdapter(TypeToken.getParameterized(Set.class, clazz).getType(), new SetSerializer<>(reference, element));
    }


    /**
     * Used to serialize and deserialize references
     */
    public static class ReferenceSerializer<T extends Reference<?>> implements JsonDeserializer<T>, JsonSerializer<T> {


        private final Function<Identifier, T> toReference;

        public ReferenceSerializer(Function<Identifier, T> toReference) {
            this.toReference = toReference;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return toReference.apply(GSON.fromJson(json, Identifier.class));
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return GSON.toJsonTree(src.getIdentifier());
        }
    }


    /**
     * Used to serialize and deserialize identifier-convertible objects
     */
    public static class IdentifierBasedSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

        protected final Function<Identifier, T> fromId;
        protected final Function<T, Identifier> toId;


        public IdentifierBasedSerializer(Registry<T> registry) {
            this.fromId = i -> registry.getOrEmpty(i).orElse(null);
            this.toId = registry::getId;
        }

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


    /**
     * Used to serialize and deserialize a set of objects that can be converted to JsonPrimitives
     */
    public static class SetSerializer<T> implements JsonSerializer<Set<T>>, JsonDeserializer<Set<T>> {

        private final Function<JsonPrimitive, T> fromJson;
        private final Function<T, JsonPrimitive> toJson;


        public SetSerializer(Function<JsonPrimitive, T> fromJson, Function<T, JsonPrimitive> toJson) {
            this.toJson = toJson;
            this.fromJson = fromJson;
        }

        @Override
        public JsonElement serialize(Set<T> set, Type type, JsonSerializationContext jsonSerializationContext) {
            return jsonSerializationContext.serialize(set.stream().map(toJson).sorted(Comparator.comparing(JsonElement::getAsString)).collect(Collectors.toList()));
        }

        @Override
        public Set<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<JsonPrimitive> primitives = GSON.fromJson(json, new TypeToken<HashSet<JsonPrimitive>>() {
            }.getType());
            return primitives.stream().map(fromJson).filter(Objects::nonNull).collect(Collectors.toSet());
        }
    }


    /**
     * Used to serialize and deserialize a set of objects that can be converted to Identifiers
     */
    public static class IdentifierBasedSetSerializer<T> extends SetSerializer<T> {

        public IdentifierBasedSetSerializer(Registry<T> registry) {
            this(i -> registry.getOrEmpty(i).orElse(null), registry::getId);
            Function<Identifier, T> fromId = i -> registry.getOrEmpty(i).orElse(null);
            Function<T, Identifier> toId = registry::getId;
        }

        public IdentifierBasedSetSerializer(Function<Identifier, T> fromId, Function<T, Identifier> toId) {
            super(i -> fromId.apply(new Identifier(i.getAsString())), i -> new JsonPrimitive(toId.apply(i).toString()));
        }
    }


    /**
     * Remember to enable complex map key serialization if used (GSON_BUILDER.enableComplexMapKeySerialization())
     * Used to serialize and deserialize a map where the keys are identifier-convertible, i.e. blocks or entity types (see Crazy Creepers config for use)
     */
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


    /**
     * Used to deserialize sets of the ConfigX interfaces, where values could be either a reference or a tag reference
     */
    public static class ConfigXDeserializer<T> implements JsonDeserializer<T> {
        private final Class<? extends T> tag;
        private final Class<? extends T> reference;

        public ConfigXDeserializer(Class<? extends T> tag, Class<? extends T> reference) {
            this.tag = tag;
            this.reference = reference;
        }


        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsString().startsWith("#"))
                return context.deserialize(json, tag);
            else
                return context.deserialize(json, reference);

        }
    }
}
