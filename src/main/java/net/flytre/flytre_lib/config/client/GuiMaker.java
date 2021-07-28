package net.flytre.flytre_lib.config.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.client.gui.TranslucentSliderWidget;
import net.flytre.flytre_lib.client.gui.text_field.ColorWidget;
import net.flytre.flytre_lib.client.gui.text_field.DropdownMenu;
import net.flytre.flytre_lib.client.gui.text_field.NumberBox;
import net.flytre.flytre_lib.client.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.client.option.TranslucentCyclingOption;
import net.flytre.flytre_lib.client.util.DropdownUtils;
import net.flytre.flytre_lib.common.util.reflection.FieldMatch;
import net.flytre.flytre_lib.common.util.reflection.ReflectionUtils;
import net.flytre.flytre_lib.config.*;
import net.flytre.flytre_lib.config.client.list.ConfigListWidget;
import net.flytre.flytre_lib.config.client.list.ListEditorScreen;
import net.flytre.flytre_lib.config.client.list.MapEditorScreen;
import net.flytre.flytre_lib.config.reference.Reference;
import net.flytre.flytre_lib.config.reference.block.ConfigBlock;
import net.flytre.flytre_lib.config.reference.entity.ConfigEntity;
import net.flytre.flytre_lib.config.reference.fluid.ConfigFluid;
import net.flytre.flytre_lib.config.reference.item.ConfigItem;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiMaker {

    public static final Predicate<Class<?>> IS_ITEM = i -> ConfigItem.class.isAssignableFrom(i) || Item.class.isAssignableFrom(i);
    public static final Predicate<Class<?>> IS_BLOCK = i -> ConfigBlock.class.isAssignableFrom(i) || Block.class.isAssignableFrom(i);
    public static final Predicate<Class<?>> IS_ENTITY = i -> ConfigEntity.class.isAssignableFrom(i) || EntityType.class.isAssignableFrom(i);
    public static final Predicate<Class<?>> IS_IDENTIFIER = i -> Reference.class.isAssignableFrom(i) || Fluid.class.isAssignableFrom(i)
            || StatusEffect.class.isAssignableFrom(i) || Enchantment.class.isAssignableFrom(i) || EntityAttribute.class.isAssignableFrom(i)
            || SoundEvent.class.isAssignableFrom(i) || VillagerProfession.class.isAssignableFrom(i) || Identifier.class.isAssignableFrom(i);
    public static final Predicate<Class<?>> IS_FLUID = i -> ConfigFluid.class.isAssignableFrom(i) || Fluid.class.isAssignableFrom(i);


    public static <K> Screen makeGui(Screen parent, ConfigHandler<K> handler) {
        if (!handler.handle())
            return null; //TODO: HANDLING, PRINT ERROR TO SCREEN
        IndividualConfigScreen<K> result = new IndividualConfigScreen<>(parent, handler);
        JsonElement element = handler.getConfigAsJson();

        if (!(element instanceof JsonObject))
            throw new AssertionError("Non-object config cannot be parsed");

        makeGuiHelper(result, handler, (JsonObject) element, handler.getConfig().getClass(), handler.getConfig());
        return result;
    }


    public static <K> void makeGuiHelper(IndividualConfigScreen<K> screen, ConfigHandler<K> handler, JsonObject object, Class<?> currentClass, Object currentObject) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        try {
            List<Field> fields = ReflectionUtils.getFields(currentClass);
            for (var entry : object.entrySet()) {
                FieldMatch fieldMatch = ReflectionUtils.match(fields, entry.getKey());

                if (fieldMatch == null)
                    continue;

                Description descriptionAnnotation = fieldMatch.field().getAnnotation(Description.class);
                String description = descriptionAnnotation == null ? "" : descriptionAnnotation.value();
                String name = getName(fieldMatch);
                fieldMatch.field().setAccessible(true);
                Object value = fieldMatch.field().get(currentObject);

                Class<?> fieldClass = fieldMatch.field().getType();

                if (value instanceof Number || Number.class.isAssignableFrom(fieldClass)) { //TODO: max/min value based n short/byte/etc.
                    numberHandler(screen, fieldClass, name, description, fieldMatch, currentObject);
                } else if (IS_ITEM.test(value.getClass())) {
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(DropdownUtils.createItemDropdown(0, 0), entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (IS_FLUID.test(value.getClass())) {
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(DropdownUtils.createFluidDropdown(0, 0), entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (IS_BLOCK.test(value.getClass())) {
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(DropdownUtils.createBlockDropdown(0, 0), entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (IS_ENTITY.test(fieldClass)) {
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(DropdownUtils.createEntityDropdown(0, 0), entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (IS_IDENTIFIER.test(fieldClass)) {
                    TranslucentTextField searchField = new TranslucentTextField(0, 0, Math.min(250, width), 20, new TranslatableText("null"));
                    searchField.setRenderer(DropdownUtils::identifierTextFieldRenderer);

                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(searchField, entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (Boolean.class.isAssignableFrom(fieldClass) || fieldClass == boolean.class) {
                    TranslucentCyclingOption<Boolean> option = TranslucentCyclingOption.create(
                            "flytre_lib.gui.value",
                            (options) -> {
                                try {
                                    return (Boolean) fieldMatch.field().get(currentObject);
                                } catch (IllegalAccessException ignored) {
                                }
                                return false;
                            },
                            (game, opt, bool) -> {
                                try {
                                    fieldMatch.field().set(currentObject, bool);
                                } catch (IllegalAccessException ignored) {
                                }
                            }
                    );
                    ClickableWidget button = option.createButton(MinecraftClient.getInstance().options, 0, 0, Math.min(250, width));
                    screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
                } else if (ConfigColor.class.isAssignableFrom(fieldClass)) {
                    ColorWidget colorWidget = new ColorWidget(0, 0, Math.min(250, width), 20, new TranslatableText("null"));
                    colorWidget.setText(entry.getValue().getAsString());
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(colorWidget, entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (Enum.class.isAssignableFrom(fieldClass)) {
                    enumHandler(screen, handler, fieldClass, fieldMatch, currentObject, entry.getValue(), width, name, description);
                } else if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                    TranslucentTextField searchField = new TranslucentTextField(0, 0, Math.min(250, width), 20, new TranslatableText("null"));
                    screen.addEntry(new ConfigListWidget.ConfigEntry(
                            handleTextField(searchField, entry.getValue(), fieldMatch, currentObject, handler),
                            name,
                            description
                    ));
                } else if (entry.getValue().isJsonArray()) {
                    Type type = fieldMatch.field().getGenericType();
                    Type valueType;
                    if (!(type instanceof ParameterizedType)) {
                        valueType = type;
                    } else {
                        valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    }

                    Consumer<List<String>> consumer = list -> {
                        try {
                            fieldMatch.field().setAccessible(true);
                            fieldMatch.field().set(currentObject, handler.getGson().fromJson(handler.getGson().toJson(list), type));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    };
                    Supplier<TranslucentTextField> adder = adder(TypeToken.get(valueType).getRawType());

                    ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.edit"), (but) -> {
                        List<String> parsed;
                        try {
                            parsed = handler.getGson().fromJson(handler.getGson().toJson(fieldMatch.field().get(currentObject)), new TypeToken<List<String>>() {
                            }.getType());
                        } catch (Exception e) {
                            but.setMessage(Text.of("Error: Edit Config Json"));
                            return;
                        }
                        List<TranslucentTextField> initial = parsed
                                .stream()
                                .sorted()
                                .map(i -> adder.get().withText(i))
                                .collect(Collectors.toList());
                        MinecraftClient.getInstance().setScreen(new ListEditorScreen(screen, consumer, initial, adder));
                    });
                    screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
                } else if (Map.class.isAssignableFrom(fieldClass)) {
                    MapEditorScreen mapEditor = new MapEditorScreen(screen);
                    Map<?, ?> rawValues = (Map<?, ?>) fieldMatch.field().get(currentObject);
                    Map<String, ClickableWidget> values = new HashMap<>();
                    Map<String, ObjectWrapper<?>> wrappedValues = new HashMap<>();
                    for (var mapEntry : rawValues.entrySet()) {
                        String key = handler.getGson().fromJson(handler.getGson().toJson(mapEntry.getKey()), String.class);
                        JsonElement element = handler.getGson().toJsonTree(mapEntry.getValue());
                        IndividualConfigScreen<K> simulator = new IndividualConfigScreen<>(mapEditor, handler);
                        if (element instanceof JsonObject) {
                            GuiMaker.makeGuiHelper(simulator, handler, (JsonObject) element, mapEntry.getValue().getClass(), mapEntry.getValue());
                            ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                                MinecraftClient.getInstance().setScreen(simulator);
                            });
                            values.put(key, button);
                        } else {
                            ObjectWrapper<?> wrapper = create(fieldMatch, mapEntry.getValue());
                            wrappedValues.put(key, wrapper);
                            JsonElement elementInner = handler.getGson().toJsonTree(wrapper);
                            GuiMaker.makeGuiHelper(simulator, handler, (JsonObject) elementInner, wrapper.getClass(), wrapper);
                            ClickableWidget widget = simulator.getList().children().get(0).getValue();
                            values.put(key, widget);

                        }
                    }
                    if (!wrappedValues.isEmpty())
                        mapEditor.setWrappedElements(wrappedValues, fieldMatch, handler, currentObject);
                    mapEditor.setValues(values);

                    ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.edit"), (but) -> {
                        MinecraftClient.getInstance().setScreen(mapEditor);
                    });
                    screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));

                } else if (entry.getValue().isJsonObject()) {
                    JsonObject inner = (JsonObject) entry.getValue();
                    IndividualConfigScreen<K> innerScreen = new IndividualConfigScreen<>(screen, handler);
                    fieldMatch.field().setAccessible(true);
                    makeGuiHelper(innerScreen, handler, inner, fieldClass, fieldMatch.field().get(currentObject));
                    ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                        MinecraftClient.getInstance().setScreen(innerScreen);
                    });
                    screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void enumHandler(IndividualConfigScreen<?> screen, ConfigHandler<?> handler, Class<?> fieldClass, FieldMatch fieldMatch, Object currentObject, JsonElement element, int width, String name, String description) {
        Enum<?>[] objs = (Enum<?>[]) fieldClass.getEnumConstants();
        if (objs.length <= 6) {
            TranslucentCyclingOption<?> option = TranslucentCyclingOption.create(
                    "flytre_lib.gui.value",
                    objs,
                    enumVal -> {
                        try {
                            return Text.of(ConfigHandler.getEnumName(enumVal));
                        } catch (NoSuchFieldException e) {
                            return Text.of("ERROR");
                        }
                    },
                    options -> {
                        try {
                            return (Enum<?>) fieldMatch.field().get(currentObject);
                        } catch (IllegalAccessException e) {
                            return objs[0];
                        }
                    },
                    (game, opt, val) -> {
                        try {
                            fieldMatch.field().set(currentObject, val);
                        } catch (IllegalAccessException ignored) {
                        }
                    }

            );
            ClickableWidget button = option.createButton(MinecraftClient.getInstance().options, 0, 0, Math.min(250, width));
            screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
        } else {
            DropdownMenu menu = DropdownUtils.createGenericDropdown(0, 0, Arrays.stream(objs).map(i -> {
                try {
                    return ConfigHandler.getEnumName(i);
                } catch (NoSuchFieldException e) {
                    return "";
                }
            }).collect(Collectors.toList()));
            screen.addEntry(new ConfigListWidget.ConfigEntry(
                    handleTextField(menu, element, fieldMatch, currentObject, handler),
                    name,
                    description
            ));
        }
    }

    private static TranslucentTextField handleTextField(TranslucentTextField raw, JsonElement element, FieldMatch match, Object currentObject, ConfigHandler<?> handler) {
        raw = raw.withText(element.getAsString());
        Type type = match.field().getGenericType();
        TypeToken<?> token = TypeToken.get(type);
        raw.setListener(i -> {
            try {
                match.field().set(currentObject, handler.getGson().fromJson(handler.getGson().toJson(i), token.getType()));
            } catch (Exception ignored) {
            }
        });
        return raw;
    }

    private static String getName(FieldMatch fieldMatch) {
        DisplayName display = fieldMatch.field().getAnnotation(DisplayName.class);
        if (display != null)
            return display.value();
        String base = fieldMatch.serializedName() != null ? fieldMatch.serializedName() : fieldMatch.field().getName();
        base = base.replaceAll("_", " ");
        return WordUtils.capitalize(base);
    }

    private static void numberHandler(IndividualConfigScreen<?> screen, Class<?> fieldClass, String name, String description, FieldMatch fieldMatch, Object currentObject) throws IllegalAccessException {
        fieldMatch.field().setAccessible(true);
        Range range = fieldMatch.field().getAnnotation(Range.class);
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();

        if (range != null)
            description += (description.length() > 0 ? " " : "") + ConfigHandler.asString(range);


        if (range != null && range.max() - range.min() < 1000) {
            double rangeLiteral = range.max() - range.min();
            if (rangeLiteral < 1000) {
                ClickableWidget widget = new TranslucentSliderWidget(0, 0, Math.min(250, width), 20, LiteralText.EMPTY, fieldMatch.field().getDouble(currentObject) / rangeLiteral) {
                    @Override
                    protected void updateMessage() {
                        try {
                            this.setMessage(new TranslatableText("flytre_lib.gui.slider", fieldMatch.field().getDouble(currentObject)));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void applyValue() {
                        try {
                            fieldMatch.field().set(currentObject, convertNumberToType(fieldClass, rangeLiteral * value + range.min()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }


                };
                screen.addEntry(new ConfigListWidget.ConfigEntry(widget, name, description));
            }
        } else {
            Object sampleType = convertNumberToType(fieldClass, 0.1);
            boolean integers = sampleType instanceof Integer || sampleType instanceof Long || sampleType instanceof BigInteger || sampleType instanceof Short || sampleType instanceof Byte;
            NumberBox widget = new NumberBox(0, 0, Math.min(230, width - 20), 20, LiteralText.EMPTY, integers, range, fieldMatch.field().getDouble(currentObject));
            widget.setListener(str -> {
                try {
                    fieldMatch.field().set(currentObject, convertNumberToType(fieldClass, Double.parseDouble(str)));
                } catch (Exception ignored) {
                }
            });
            screen.addEntry(new ConfigListWidget.ConfigEntry(widget, name, description));
        }
    }

    private static Object convertNumberToType(Class<?> fieldClass, double value) {
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

        throw new IllegalArgumentException("Unknown class:" + fieldClass.getClass());
    }

    private static Supplier<TranslucentTextField> adder(Class<?> clazz) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        if (IS_ITEM.test(clazz))
            return () -> DropdownUtils.createItemDropdown(0, 0);
        if (IS_FLUID.test(clazz))
            return () -> DropdownUtils.createFluidDropdown(0, 0);
        if (IS_BLOCK.test(clazz))
            return () -> DropdownUtils.createItemDropdown(0, 0);
        if (IS_ENTITY.test(clazz))
            return () -> DropdownUtils.createItemDropdown(0, 0);
        if (IS_IDENTIFIER.test(clazz)) {
            return () -> {
                TranslucentTextField searchField = new TranslucentTextField(0, 0, Math.min(250, width), 20, new TranslatableText("null"));
                searchField.setRenderer(DropdownUtils::identifierTextFieldRenderer);
                return searchField;
            };
        }
        if (Number.class.isAssignableFrom(clazz))
            return () -> {
                boolean integers = Long.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz) || BigInteger.class.isAssignableFrom(clazz);
                return new NumberBox(0, 0, Math.min(230, width - 20), 20, LiteralText.EMPTY, integers, null, 0);
            };
        return () -> new TranslucentTextField(0, 0, Math.min(250, width), 20, new TranslatableText("null"));
    }

    /**
     * Wrap map values
     */
    public static ObjectWrapper<?> create(FieldMatch match, Object wrappedValue) {
        Type generic = ((ParameterizedType) match.field().getGenericType()).getActualTypeArguments()[1];
        Class<?> clazz = TypeToken.get(generic).getRawType();
        return new ObjectWrapper<>(clazz.cast(wrappedValue));
    }

    public static class ObjectWrapper<K> {
        public K value;

        public ObjectWrapper(K value) {
            this.value = value;
        }
    }
}
