package net.flytre.flytre_lib.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.FlytreLib;
import net.flytre.flytre_lib.common.util.Formatter;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A config handler is what handles all the input-output of a config.
 * Namely, Turning the config into a formatted file and back again
 *
 * @param <T> The FlytreLibConfig class
 */
public class ConfigHandler<T> {
    private final Gson gson;
    private final T assumed;
    private final String name;
    private T config;

    public ConfigHandler(T assumed, String name) {
        this(assumed, name, GsonHelper.GSON);
    }

    public ConfigHandler(T assumed, String name, Gson gson) {
        this.assumed = assumed;
        this.name = name;
        this.gson = gson;
    }

    private static String asString(Range range) {
        return "[min: " + Formatter.formatNumber(range.min()) + ", max: " + Formatter.formatNumber(range.max()) + "]";
    }

    /**
     * Save a config to the config file location
     */
    public void save(T config) {
        Path location = FabricLoader.getInstance().getConfigDir();
        Path path = Paths.get(location.toString(), name + ".json5");
        Writer writer;
        try {
            writer = new FileWriter(path.toFile());
            JsonElement parsed = commentFormattedGson(gson.toJsonTree(config), config);
            writer.write(StringEscapeUtils.unescapeJava(formattedGsonToJson5(gson.toJson(parsed))));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formattedGsonToJson5(String str) {
        Pattern commentPattern = Pattern.compile("([ \\t]*)\"(\\w*)\":\\s*\\{\\s*\"value\": ((.|\\s)+?),\\s*\"comment\": \"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"\\s*}");
        str = commentPattern.matcher(str).replaceAll("$1//$5\\\n$1\"$2\": $3");

        Pattern multiline = Pattern.compile("^(\\s*)//(.*?)\\\\n(.*)$", Pattern.MULTILINE);
        while (str.contains("\\n"))
            str = multiline.matcher(str).replaceAll("$1//$2\n$1//$3");

        return str;
    }

    private JsonElement commentFormattedGson(JsonElement serialized, T config) {

        if (!(serialized instanceof JsonObject))
            return serialized;

        JsonObject object = (JsonObject) serialized;

        commentFormattedGsonHelper(object, config.getClass());
        return object;
    }

    private void commentFormattedGsonHelper(JsonObject object, Class<?> clazz) {
        List<Field> fields = getFields(clazz);
        for (var entry : object.entrySet()) {
            FieldMatch fieldMatch = match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            String comment = "";
            Description description = fieldMatch.field.getAnnotation(Description.class);
            if (description != null)
                comment += description.value();

            if (fieldMatch.field.getType().isEnum()) {
                comment += (comment.length() > 0 ? " " : "") + enumToString(fieldMatch.field.getType());
            }


            Range range = fieldMatch.field.getAnnotation(Range.class);
            if (range != null)
                comment += (comment.length() > 0 ? " " : "") + asString(range);

            if (entry.getValue() instanceof JsonObject) {
                commentFormattedGsonHelper((JsonObject) entry.getValue(), fieldMatch.field.getType());
            }

            if (comment.length() > 0) {
                JsonObject o = new JsonObject();
                o.add("value", entry.getValue());
                o.addProperty("comment", comment);
                object.add(entry.getKey(), o);
            }

        }
    }

    public void validate(JsonObject serialized, T config) throws IllegalAccessException {
        validate(serialized, config.getClass(), config, new ArrayList<>());
    }

    public void validate(JsonObject json, Class<?> clazz, Object obj, List<String> path) throws IllegalAccessException {
        List<Field> fields = getFields(clazz);
        for (var entry : json.entrySet()) {
            FieldMatch fieldMatch = match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            Range range = fieldMatch.field.getAnnotation(Range.class);
            if (range != null) {
                Object value = fieldMatch.field.get(obj);
                if (range.max() < range.min())
                    throw new ConfigAnnotationException("Invalid @Range annotation for field " + fieldMatch.field.getName() + ": Max value must be less or equal to the min value");
                if (!(value instanceof Number)) {
                    throw new ConfigAnnotationException("@Range annotation unsuppoted for field " + fieldMatch.field.getName());
                } else {
                    Number number = (Number) value;
                    if (range.min() > number.doubleValue() || range.max() < number.doubleValue()) {
                        List<String> path2 = new ArrayList<>(path);
                        path2.add(fieldMatch.field.getName());
                        throw new ValidationException("Value " + value + " for field " + String.join(".", path2) + " is not in range " + asString(range));
                    }
                }

            }

            if (entry.getValue() instanceof JsonObject) {
                fieldMatch.field.setAccessible(true);
                validate((JsonObject) entry.getValue(), fieldMatch.field.getType(), fieldMatch.field.get(obj),
                        Stream.concat(path.stream(), List.of(fieldMatch.field.getName()).stream()).distinct().collect(Collectors.toUnmodifiableList()));
            }
        }
    }

    private String enumToString(Class<?> type) {
        Enum<?>[] objs = (Enum<?>[]) type.getEnumConstants();
        List<String> result = new ArrayList<>();
        for (Enum<?> val : objs) {
            try {
                SerializedName serializedName = val.getClass().getField(val.name()).getAnnotation(SerializedName.class);
                if (serializedName == null)
                    result.add(val.name());
                else
                    result.add(serializedName.value());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        return result.toString();
    }

    private @Nullable FieldMatch match(List<Field> fields, String serializedName) {
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

    private List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();

        do {
            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return result;
    }


    private void appendErrorToConfig(File file, Exception e) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String str = FileUtils.readFileToString(file, Charsets.UTF_8);


        String errorMessage = e.getMessage();

        if (e instanceof NumberFormatException) {
            errorMessage = "java.lang.NumberFormatException: " + errorMessage;
        }

        errorMessage = errorMessage.replace("java.lang.NumberFormatException", "Expected a number but found a different data type");
        errorMessage = errorMessage.replace("java.lang.IllegalStateException", "Wrong type of value passed");

        if (e instanceof InvalidIdentifierException) {
            errorMessage += ". The namespace is everything before the colon, while the path is everything after";
        }

        errorMessage = errorMessage + ". Default config was loaded.";


        String logLine = "//[" + format.format(date) + "]" + " ERROR: " + errorMessage;
        str = logLine + "\n" + str;

        Path path = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), name + ".json5");
        FileWriter writer = new FileWriter(path.toFile());
        writer.write(str);
        writer.close();
    }

    /**
     * Load the config, or if none is found save the default to a file and load that
     */
    public void handle() {
        Path location = FabricLoader.getInstance().getConfigDir();
        File config = location.toFile();
        File configFile = null;
        for (File file : config.listFiles()) {
            if (file.getName().equals(name + ".json5")) {
                configFile = file;
                break;
            }
        }

        if (configFile == null) {
            save(assumed);
            this.config = assumed;
        } else {
            try (Reader reader = new FileReader(configFile)) {

                try {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    this.config = gson.fromJson(json, (Type) assumed.getClass());
                    validate(json, this.config);
                } catch (JsonParseException | NumberFormatException | InvalidIdentifierException | ValidationException e) {
                    this.config = assumed;
                    FlytreLib.LOGGER.error("Unable to load config " + name + ".json5 : " + e.getMessage() + ". Loading default config instead.");
                    appendErrorToConfig(configFile, e);
                }
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (this.config instanceof ConfigEventAcceptor)
            ((ConfigEventAcceptor) this.config).onReload();

    }

    public T getAssumed() {
        return assumed;
    }

    public T getConfig() {
        return config;
    }

    public void setConfig(JsonElement element) {
        this.config = gson.fromJson(element, (Type) assumed.getClass());
    }

    /**
     * If handle hasn't been called once yet, that's on you
     */
    public JsonElement getConfigAsJson() {
        return gson.toJsonTree(config == null ? assumed : config);
    }

    public Identifier getConfigId() {
        return new Identifier("flytre_lib", name);
    }

    public T fromJson(JsonElement element) {
        return gson.fromJson(element, (Type) assumed.getClass());
    }

    private record FieldMatch(Field field, String displayName) {
    }


    public static class ConfigAnnotationException extends RuntimeException {

        public ConfigAnnotationException(String msg) {
            super(msg);
        }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) {
            super(msg);
        }
    }
}
