package net.flytre.flytre_lib.api.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.api.base.util.DevUtils;
import net.flytre.flytre_lib.api.base.util.reflection.FieldMatch;
import net.flytre.flytre_lib.api.base.util.reflection.ReflectionUtils;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.Range;
import net.flytre.flytre_lib.impl.config.ConfigHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A config handler is what handles all the input-output of a config.
 * Namely, Turning the config into a formatted file and back again
 *
 * @param <T> The Config class
 */
public final class ConfigHandler<T> {
    private final Gson gson;
    private final T assumed;
    private final String name;
    private final String translationPrefix;
    private T config;

    public ConfigHandler(T assumed, String name) {
        this(assumed, name, (String) null);
    }

    public ConfigHandler(T assumed, String name, Gson gson) {
        this(assumed, name, null, gson);
    }

    public ConfigHandler(T assumed, String name, String translationPrefix) {
        this(assumed, name, translationPrefix, GsonHelper.GSON);
    }

    public ConfigHandler(T assumed, String name, String translationPrefix, Gson gson) {
        this.gson = gson;
        this.assumed = assumed;
        this.name = name;
        this.translationPrefix = translationPrefix;
    }


    public String getTranslationPrefix() {
        return translationPrefix;
    }


    public String getName() {
        return name;
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

        str = "//Hit F3 + M to edit configs client side in game\n" + str;
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
        List<Field> fields = ReflectionUtils.getFields(clazz);
        for (var entry : object.entrySet()) {
            FieldMatch fieldMatch = ReflectionUtils.match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            String comment = "";
            Description description = fieldMatch.field().getAnnotation(Description.class);
            if (description != null)
                comment += description.value();

            if (fieldMatch.field().getType().isEnum()) {
                comment += (comment.length() > 0 ? " " : "") + ConfigHelper.enumAsStringArray(fieldMatch.field().getType());
            }


            Range range = fieldMatch.field().getAnnotation(Range.class);
            if (range != null)
                comment += (comment.length() > 0 ? " " : "") + ConfigHelper.asString(range);

            if (entry.getValue() instanceof JsonObject) {
                commentFormattedGsonHelper((JsonObject) entry.getValue(), fieldMatch.field().getType());
            }

            if (comment.length() > 0) {
                JsonObject o = new JsonObject();
                o.add("value", entry.getValue());
                o.addProperty("comment", comment);
                object.add(entry.getKey(), o);
            }

        }
    }

    private void validate(JsonObject serialized, T config) throws IllegalAccessException {
        validate(serialized, config.getClass(), config, new ArrayList<>());
    }

    private void validate(JsonObject json, Class<?> clazz, Object obj, List<String> path) throws IllegalAccessException {
        List<Field> fields = ReflectionUtils.getFields(clazz);
        for (var entry : json.entrySet()) {
            FieldMatch fieldMatch = ReflectionUtils.match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            fieldMatch.field().setAccessible(true);
            Object value = fieldMatch.field().get(obj);

            if (value == null) {
                throw new ValidationException("Null value found for field " + fieldMatch.field().getName() + ". Likely caused by an invalid value for said field");
            }


            Range range = fieldMatch.field().getAnnotation(Range.class);
            if (range != null) {
                if (range.max() < range.min())
                    throw new ConfigAnnotationException("Invalid @Range annotation for field " + fieldMatch.field().getName() + ": Max value must be less or equal to the min value");
                if (!(value instanceof Number)) {
                    throw new ConfigAnnotationException("@Range annotation unsupported for field " + fieldMatch.field().getName());
                } else {
                    Number number = (Number) value;
                    if (range.min() > number.doubleValue() || range.max() < number.doubleValue()) {
                        List<String> path2 = new ArrayList<>(path);
                        path2.add(fieldMatch.field().getName());
                        throw new ValidationException("Value " + value + " for field " + String.join(".", path2) + " is not in range " + ConfigHelper.asString(range));
                    }
                }

            }

            if (entry.getValue() instanceof JsonObject) {
                fieldMatch.field().setAccessible(true);
                validate((JsonObject) entry.getValue(), fieldMatch.field().getType(), fieldMatch.field().get(obj),
                        Stream.concat(path.stream(), List.of(fieldMatch.field().getName()).stream()).distinct().collect(Collectors.toUnmodifiableList()));
            }
        }
    }

    private void appendError(File file, Exception e) throws IOException {
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
     * Return false if an error was found, or true if none was
     */
    public boolean handle() {
        boolean error = false;
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
                    DevUtils.LOGGER.error("Unable to load config " + name + ".json5 : " + e.getMessage() + ". Loading default config instead.");
                    appendError(configFile, e);
                    error = true;
                }
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (this.config instanceof ConfigEventAcceptor)
            ((ConfigEventAcceptor) this.config).onReload();

        return !error;
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


    public Gson getGson() {
        return gson;
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
