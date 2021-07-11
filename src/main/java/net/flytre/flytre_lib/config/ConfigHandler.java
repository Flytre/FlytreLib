package net.flytre.flytre_lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A config handler is what handles all the input-output of a config.
 * Namely, Turning the config into a formatted file and back again
 *
 * @param <T> The Config class
 */
public class ConfigHandler<T> {
    private final Gson gson;
    private final T assumed;
    private final String name;
    private T config;

    public ConfigHandler(T assumed, String name) {
        this(assumed, name, new GsonBuilder().setPrettyPrinting().create());
    }

    public ConfigHandler(T assumed, String name, Gson gson) {
        this.assumed = assumed;
        this.name = name;
        this.gson = gson;
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
                this.config = gson.fromJson(reader, (Type) assumed.getClass());
            } catch (IOException e) {
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

    private record FieldMatch(Field field, String displayName) {
    }
}
