package net.flytre.flytre_lib.impl.config.client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.flytre.flytre_lib.api.base.util.reflection.FieldMatch;
import net.flytre.flytre_lib.api.base.util.reflection.ReflectionUtils;
import net.flytre.flytre_lib.api.config.ConfigColor;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.GsonHelper;
import net.flytre.flytre_lib.api.config.annotation.Button;
import net.flytre.flytre_lib.api.config.annotation.Populator;
import net.flytre.flytre_lib.api.config.annotation.Range;
import net.flytre.flytre_lib.api.config.reference.Reference;
import net.flytre.flytre_lib.api.config.reference.block.ConfigBlock;
import net.flytre.flytre_lib.api.config.reference.entity.ConfigEntity;
import net.flytre.flytre_lib.api.config.reference.fluid.ConfigFluid;
import net.flytre.flytre_lib.api.config.reference.item.ConfigItem;
import net.flytre.flytre_lib.api.gui.TranslucentSliderWidget;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.flytre.flytre_lib.api.gui.button.TranslucentCyclingOption;
import net.flytre.flytre_lib.api.gui.text_field.*;
import net.flytre.flytre_lib.impl.config.ConfigHelper;
import net.flytre.flytre_lib.impl.config.client.list.ConfigListWidget;
import net.flytre.flytre_lib.impl.config.client.list.ListEditorScreen;
import net.flytre.flytre_lib.impl.config.client.list.MapEditorScreen;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiMaker {

    private static final Predicate<Class<?>> IS_ITEM = i -> ConfigItem.class.isAssignableFrom(i) || Item.class.isAssignableFrom(i);
    private static final Predicate<Class<?>> IS_BLOCK = i -> ConfigBlock.class.isAssignableFrom(i) || Block.class.isAssignableFrom(i);
    private static final Predicate<Class<?>> IS_ENTITY = i -> ConfigEntity.class.isAssignableFrom(i) || EntityType.class.isAssignableFrom(i);
    private static final Predicate<Class<?>> IS_FLUID = i -> ConfigFluid.class.isAssignableFrom(i) || Fluid.class.isAssignableFrom(i);
    private static final Predicate<Class<?>> IS_IDENTIFIER = i -> Reference.class.isAssignableFrom(i) || GsonHelper.REGISTRY_BASED.keySet().stream().anyMatch(clazz -> clazz.isAssignableFrom(i));


    public static <K> Screen createGui(Screen parent, @Nullable ButtonWidget reopen, ConfigHandler<K> handler) {
        if (!handler.handle())
            return null; //TODO: HANDLING, PRINT ERROR TO SCREEN
        IndividualConfigScreen<K> screen = new IndividualConfigScreen<>(parent, reopen, handler);
        JsonElement element = handler.getConfigAsJson();

        if (!(element instanceof JsonObject))
            throw new AssertionError("Non-object config cannot be parsed");

        JsonElement defaultElement = handler.getGson().toJsonTree(handler.getAssumed());
        assert defaultElement instanceof JsonObject;

        createGuiHelper(new ParentData<>(screen, handler, handler.getConfig().getClass(), (JsonObject) element, handler.getConfig(), (JsonObject) defaultElement, handler.getAssumed()));
        return screen;
    }

    private static <K> void createGuiHelper(ParentData<K> state) {

        try {
            List<Field> fields = ReflectionUtils.getFields(state.clazz);
            for (var entry : state.json.entrySet()) {
                FieldMatch fieldMatch = ReflectionUtils.match(fields, entry.getKey());

                if (fieldMatch == null)
                    continue;

                addGuiElement(state, fieldMatch, entry, state.defJson.get(entry.getKey()));
            }
        } catch (IllegalAccessException e) {
            throw new ConfigError(e);
        }
    }


    private static <K> void addGuiElement(ParentData<K> state, FieldMatch fieldMatch, Map.Entry<String, JsonElement> entry, JsonElement defaultValue) throws IllegalAccessException {

        String name = ConfigHelper.getName(state.handler, fieldMatch);
        String description = ConfigHelper.getDescription(fieldMatch);

        fieldMatch.field().setAccessible(true);


        Object value = getValue(fieldMatch, state.obj); //the value of the field

        final Class<?> fieldClass;

        {

            Class<?> temp = fieldMatch.field().getType(); //the type of the field

            if (temp == Object.class) //if the field class is just an object, set the class to the class of the value
                temp = value.getClass();
            fieldClass = temp;
        }

        if (value instanceof Number || Number.class.isAssignableFrom(fieldClass)) {
            addNumber(state, name, description, fieldMatch, fieldClass);
        } else if (IS_ITEM.test(value.getClass())) {
            addString(DropdownUtils.createItemDropdown(), state, fieldMatch, entry.getValue(), name, description);
        } else if (IS_FLUID.test(value.getClass())) {
            addString(DropdownUtils.createFluidDropdown(), state, fieldMatch, entry.getValue(), name, description);
        } else if (IS_BLOCK.test(value.getClass())) {
            addString(DropdownUtils.createBlockDropdown(), state, fieldMatch, entry.getValue(), name, description);
        } else if (IS_ENTITY.test(value.getClass())) {
            addString(AsynchronousDropdownMenu.createEntityDropdown(), state, fieldMatch, entry.getValue(), name, description);
        } else if (IS_IDENTIFIER.test(value.getClass())) {
            TranslucentTextField searchField = new TranslucentTextField(0, 0, width(), 20, Text.of(""));
            searchField.setRenderer(DropdownUtils::identifierTextFieldRenderer);
            addString(searchField, state, fieldMatch, entry.getValue(), name, description);
        } else if (value instanceof Boolean || Boolean.class.isAssignableFrom(fieldClass) || fieldClass == boolean.class) {
            addBoolean(state, fieldMatch, name, description);
        } else if (value instanceof ConfigColor || ConfigColor.class.isAssignableFrom(fieldClass)) {
            ColorWidget colorWidget = new ColorWidget(0, 0, width(), 20, Text.of(""));
            addString(colorWidget, state, fieldMatch, entry.getValue(), name, description);
        } else if (value instanceof Enum || Enum.class.isAssignableFrom(fieldClass)) {
            addEnum(state, fieldClass, fieldMatch, entry.getValue(), name, description);
        } else if (entry.getValue().isJsonArray()) {
            addList(state, fieldMatch, name, description, (JsonArray) entry.getValue());
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            addMap(state, fieldMatch, name, description);
        } else if (entry.getValue().isJsonObject()) {
            JsonObject entryValue = (JsonObject) entry.getValue();
            ClickableWidget button = new TranslucentButton(0, 0, width(), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                IndividualConfigScreen<K> innerScreen = new IndividualConfigScreen<>(state.screen, null, state.handler);
                createGuiHelper(new ParentData<>(innerScreen, state.handler, fieldClass, entryValue, getValue(fieldMatch, state.obj), (JsonObject) defaultValue, getValue(fieldMatch, state.defObj)));
                MinecraftClient.getInstance().setScreen(innerScreen);
            });
            state.screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
        } else {
            TranslucentTextField textField = new TranslucentTextField(0, 0, width(), 20, Text.of(""));
            addString(textField, state, fieldMatch, entry.getValue(), name, description);
        }
    }

    private static <K> void addNumber(ParentData<K> state, String name, String description, FieldMatch fieldMatch, Class<?> fieldClass) throws IllegalAccessException {
        Range range = fieldMatch.field().getAnnotation(Range.class);

        if (range != null)
            description += (description.length() > 0 ? " " : "") + ConfigHelper.asString(range);


        Object instanceofType = ConfigHelper.convertDouble(fieldClass, 0.1);
        boolean restrictToInt = instanceofType instanceof Integer || instanceofType instanceof Long || instanceofType instanceof BigInteger || instanceofType instanceof Short || instanceofType instanceof Byte;


        if (range != null && range.max() - range.min() < 1000) {
            double range2 = range.max() - range.min();
            ClickableWidget widget = new TranslucentSliderWidget(0, 0, width(), 20, LiteralText.EMPTY, ((Number) getValue(fieldMatch,state.obj)).doubleValue() / range2) {
                @Override
                protected void updateMessage() {
                    String format = restrictToInt ? "%f" : "%.3f";
                    this.setMessage(new TranslatableText("flytre_lib.gui.slider", String.format(format, ((Number) getValue(fieldMatch,state.obj)).doubleValue())));
                }

                @Override
                protected void applyValue() {
                    Object val = ConfigHelper.convertDouble(fieldClass, Double.parseDouble(String.format("%.3f", range2 * value + range.min())));
                    setValue(fieldMatch,state.obj,val);
                }


            };
            state.screen.addEntry(new ConfigListWidget.ConfigEntry(widget, name, description));
        } else {
            NumberBox.ValueRange valueRange = range == null ? null : new NumberBox.ValueRange(range.min(), range.max());
            NumberBox widget = new NumberBox(0, 0, Math.min(230, ConfigHelper.getWidth() - 20), 20, LiteralText.EMPTY, restrictToInt, ((Number) getValue(fieldMatch,state.obj)).doubleValue(), valueRange);
            widget.setListener(str -> setValue(fieldMatch,state.obj,ConfigHelper.convertDouble(fieldClass, Double.parseDouble(str))));
            state.screen.addEntry(new ConfigListWidget.ConfigEntry(widget, name, description));
        }

    }

    private static void addString(TranslucentTextField field, ParentData<?> state, FieldMatch fieldMatch, JsonElement jsonString, String name, String description) {
        state.screen.addEntry(new ConfigListWidget.ConfigEntry(
                formatTextField(field, jsonString, fieldMatch, state.obj, state.handler),
                name,
                description
        ));
    }

    private static <K extends TranslucentTextField> K formatTextField(K textField, JsonElement jsonString, FieldMatch match, Object object, ConfigHandler<?> handler) {
        textField.setText(jsonString.getAsString());
        TypeToken<?> token = TypeToken.get(match.field().getGenericType()); // get the type of the field
        textField.setListener(i -> {
            //Converts the string into json, then from json into the type of the field (i.e. BlockReference)
            Object val = handler.getGson().fromJson(handler.getGson().toJson(i), token.getType());
            setValue(match,object,val);
        });
        return textField;
    }

    private static void addBoolean(ParentData<?> state, FieldMatch fieldMatch, String name, String description) {
        TranslucentCyclingOption<Boolean> option = TranslucentCyclingOption.create(
                "flytre_lib.gui.value",
                (options) -> (Boolean) getValue(fieldMatch,state.obj),
                (game, opt, bool) -> setValue(fieldMatch,state.obj,bool)
        );
        ClickableWidget button = option.createButton(MinecraftClient.getInstance().options, 0, 0, width());
        state.screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
    }

    private static void addEnum(ParentData<?> state, Class<?> fieldClass, FieldMatch fieldMatch, JsonElement element, String name, String description) {
        Enum<?>[] constants = (Enum<?>[]) fieldClass.getEnumConstants();
        if (constants.length <= 6) {
            TranslucentCyclingOption<?> option = TranslucentCyclingOption.create(
                    "flytre_lib.gui.value",
                    constants,
                    enumVal -> Text.of(ConfigHelper.getEnumName(enumVal, true)),
                    options -> (Enum<?>) getValue(fieldMatch,state.obj),
                    (game, opt, val) -> setValue(fieldMatch,state.obj,val)
            );
            ClickableWidget button = option.createButton(MinecraftClient.getInstance().options, 0, 0, width());
            state.screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
        } else {
            DropdownMenu menu = DropdownUtils.createGenericDropdown(0, 0, Arrays.stream(constants).map(i -> ConfigHelper.getEnumName(i, false)).collect(Collectors.toList()));
            addString(menu, state, fieldMatch, element, name, description);
        }
    }

    private static <K> void addList(ParentData<K> state, FieldMatch fieldMatch, String name, String description, JsonArray value) {
        Type type = fieldMatch.field().getGenericType();
        Type valueType;
        if (!(type instanceof ParameterizedType)) {
            valueType = type;
        } else {
            valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }

        Consumer<List<Object>> consumer = list -> {
            Object val = state.handler.getGson().fromJson(state.handler.getGson().toJson(list), type);
            setValue(fieldMatch,state.obj,val);
        };
        Supplier<TranslucentTextField> adder = adder(TypeToken.get(valueType).getRawType());

        ClickableWidget button = new TranslucentButton(0, 0, width(), 20, new TranslatableText("flytre_lib.gui.edit"), (but) -> {
            List<String> parsed;
            try {
                parsed = state.handler.getGson().fromJson(value, new TypeToken<List<String>>() {}.getType());
            } catch (JsonParseException e) {
                but.setMessage(Text.of("Error: Edit Config Json"));
                return;
            }

            List<TranslucentTextField> initial = parsed
                    .stream()
                    .sorted()
                    .map(i -> adder.get().withText(i))
                    .collect(Collectors.toList());
            MinecraftClient.getInstance().setScreen(new ListEditorScreen<>(state.screen, but, consumer, initial, adder, fieldMatch.field().getAnnotation(Button.class)));
        });
        state.screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));

    }


    private static int width() {
        return Math.min(250, ConfigHelper.getWidth());
    }


    private static void addMap(ParentData<?> state, FieldMatch fieldMatch, String name, String description) {
        ClickableWidget button = new TranslucentButton(0, 0, width(), 20, new TranslatableText("flytre_lib.gui.edit"), (but) -> {
            //Generates the map every time its opened rather than just once for custom button tweaking
            MinecraftClient.getInstance().setScreen(mapScreenMaker(state, fieldMatch, (Map<?, ?>) getValue(fieldMatch,state.obj), but));
        });
        state.screen.addEntry(new ConfigListWidget.ConfigEntry(button, name, description));
    }

    private static <T, E, K> MapEditorScreen mapScreenMaker(ParentData<K> state, FieldMatch fieldMatch, Map<T, E> rawValues, ButtonWidget reopen) {

        //Default values are irrelevant for maps because they're used to predict list types and reset values, and have nothing to do with map values

        MapEditorScreen mapEditor = new MapEditorScreen(state.screen, fieldMatch.field().getAnnotation(Button.class), reopen);

        //Populator
        populator:
        {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            ClientWorld world = MinecraftClient.getInstance().world;
            if (player == null || world == null)
                break populator;
            Populator annotation = fieldMatch.field().getAnnotation(Populator.class);
            if (annotation == null)
                break populator;
            try {
                BiFunction<ClientWorld, ClientPlayerEntity, Map<?, ?>> function = annotation.value().getConstructor().newInstance();
                Map<? extends T, ? extends E> values = (Map<? extends T, ? extends E>) function.apply(world, player);//Ignore THIS fishy cast, I trust the user to be sensible
                for (T key : values.keySet()) {
                    if (!annotation.replace())
                        rawValues.putIfAbsent(key, values.get(key));
                    else
                        rawValues.put(key, values.get(key));
                }
            } catch (ClassCastException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Map<String, ClickableWidget> values = new HashMap<>();
        Map<String, ObjectWrapper<?>> wrappedValues = new HashMap<>();
        for (var mapEntry : rawValues.entrySet()) {
            String key = state.handler.getGson().fromJson(state.handler.getGson().toJson(mapEntry.getKey()), String.class);
            JsonElement element = state.handler.getGson().toJsonTree(mapEntry.getValue());
            if (element instanceof JsonObject) {
                ClickableWidget button = new TranslucentButton(0, 0, width(), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                    IndividualConfigScreen<K> simulator = new IndividualConfigScreen<>(mapEditor, but, state.handler);
                    createGuiHelper(new ParentData<>(simulator, state.handler, mapEntry.getValue().getClass(), (JsonObject) element, mapEntry.getValue(), (JsonObject) element, mapEntry.getValue()));
                    MinecraftClient.getInstance().setScreen(simulator);
                });
                values.put(key, button);
            } else {
                IndividualConfigScreen<K> simulator = new IndividualConfigScreen<>(mapEditor, reopen, state.handler);
                ObjectWrapper<?> wrapper = create(fieldMatch, mapEntry.getValue());
                wrappedValues.put(key, wrapper);
                JsonElement elementInner = state.handler.getGson().toJsonTree(wrapper);
                createGuiHelper(new ParentData<>(simulator, state.handler, wrapper.getClass(), (JsonObject) elementInner, wrapper, (JsonObject) elementInner, wrapper));
                ClickableWidget widget = simulator.getEntries().get(0).getValue();
                values.put(key, widget);

            }
        }
        if (!wrappedValues.isEmpty())
            mapEditor.setWrappedElements(wrappedValues, fieldMatch, state.handler, state.obj);
        mapEditor.setValues(values);

        return mapEditor;
    }

    /**
     * Wrap map values
     */
    private static ObjectWrapper<?> create(FieldMatch match, Object wrappedValue) {
        Type generic = ((ParameterizedType) match.field().getGenericType()).getActualTypeArguments()[1];
        Class<?> clazz = TypeToken.get(generic).getRawType();
        return new ObjectWrapper<>(clazz.cast(wrappedValue));
    }


    private static Supplier<TranslucentTextField> adder(Class<?> clazz) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        if (IS_ITEM.test(clazz))
            return () -> DropdownUtils.createItemDropdown(0, 0);
        if (IS_FLUID.test(clazz))
            return () -> DropdownUtils.createFluidDropdown(0, 0);
        if (IS_BLOCK.test(clazz))
            return () -> DropdownUtils.createBlockDropdown(0, 0);
        if (IS_ENTITY.test(clazz))
            return () -> DropdownUtils.createEntityDropdown(0, 0);
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
                return new NumberBox(0, 0, Math.min(230, width - 20), 20, LiteralText.EMPTY, integers, 0, null);
            };
        if (Enum.class.isAssignableFrom(clazz)) {
            Enum<?>[] constants = (Enum<?>[]) clazz.getEnumConstants();
            return () -> DropdownUtils.createGenericDropdown(Arrays.stream(constants).map(Enum::name).collect(Collectors.toList()));

        }
        return () -> new TranslucentTextField(0, 0, width(), 20, new TranslatableText("null"));
    }

    public static Runnable getRunnable(@Nullable Button button) {
        try {
            return button == null ? null : button.function().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setValue(FieldMatch field,Object objectWithTheField, Object value) {
        try {
            field.field().set(objectWithTheField, value);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new ConfigError(e);
        }
    }

    public static Object getValue(FieldMatch field, Object objectWithTheField) {
        try {
            return field.field().get(objectWithTheField);
        } catch (IllegalAccessException e) {
            throw new ConfigError(e);
        }
    }

    public static class ParentData<K> {
        public final IndividualConfigScreen<K> screen; //The screen to add things to
        public final ConfigHandler<K> handler; //The config handler
        public final JsonObject json; //The actual json for the config
        public final Class<?> clazz; //The class of the current object
        public final Object obj; //The actual config object from the current config


        private final Object defObj; //The default object from the default config
        private final JsonObject defJson; //The default json from the default config

        public ParentData(IndividualConfigScreen<K> screen, ConfigHandler<K> handler, Class<?> clazz, JsonObject json, Object obj, JsonObject defJson, Object defObj) {
            this.screen = screen;
            this.handler = handler;
            this.json = json;
            this.clazz = clazz;
            this.obj = obj;


            this.defObj = defObj;
            this.defJson = defJson;
        }

        @Override
        public String toString() {
            return "ParentData{" +
                    "screen=" + screen +
                    ", handler=" + handler +
                    ", json=" + json +
                    ", clazz=" + clazz +
                    ", obj=" + obj +
                    ", defObj=" + defObj +
                    ", defJson=" + defJson +
                    '}';
        }
    }


}