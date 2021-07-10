package net.flytre.flytre_lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public void save(T config) {
        Path location = FabricLoader.getInstance().getConfigDir();
        Path path = Paths.get(location.toString(), name + ".json5");
        Writer writer;
        try {
            writer = new FileWriter(path.toFile());
            JsonElement serialized = gson.toJsonTree(config);
            serialized = commentAdder(serialized, config);
            String out = gson.toJson(serialized);
            String commented = commentApplier(out);
            writer.write(commented);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String commentApplier(String str) {
        Pattern pattern = Pattern.compile("([ \\t]*)\"(\\w*)\":\\s*\\{\\s*\"value\": (\"?([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"?),\\s*\"comment\": \"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"\\s*}");
        Matcher m = pattern.matcher(str);
        return m.replaceAll("$1//$6\\\n$1\"$2\": $3");
    }


    private JsonElement commentAdder(JsonElement serialized, T config) {

        if (!(serialized instanceof JsonObject))
            return serialized;

        JsonObject object = (JsonObject) serialized;

        commentAdderHelper(object, config.getClass());
        return object;
    }

    private void commentAdderHelper(JsonObject object, Class<?> clazz) {
        List<Field> fields = getFields(clazz);
        for (var entry : object.entrySet()) {
            FieldMatch fieldMatch = match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            if (entry.getValue() instanceof JsonObject) {
                commentAdderHelper((JsonObject) entry.getValue(), fieldMatch.field.getType());
            } else {
                String value = "";
                Description description = fieldMatch.field.getAnnotation(Description.class);
                if (description != null)
                    value += description.value();

                if (fieldMatch.field.getType().isEnum()) {
                    value += (value.length() > 0 ? " " : "") + enumToString(fieldMatch.field.getType());
                }

                if (value.length() > 0) {
                    JsonObject o = new JsonObject();
                    o.add("value", entry.getValue());
                    o.addProperty("comment", value);
                    object.add(entry.getKey(), o);
                }
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
