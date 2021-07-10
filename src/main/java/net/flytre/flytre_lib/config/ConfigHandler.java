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
        Path path = Paths.get(location.toString(), name + ".json");
        Writer writer;
        try {
            writer = new FileWriter(path.toFile());
            JsonElement serialized = gson.toJsonTree(config);
            serialized = configValueHelper(serialized, config);
            gson.toJson(serialized, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonElement configValueHelper(JsonElement serialized, T config) {

        if (!(serialized instanceof JsonObject))
            return serialized;

        JsonObject object = (JsonObject) serialized;

        List<String> comment = new ArrayList<>();
        comment.add("Config Value Helper:");
        comment.add("Use this to help you figure out what values each key can have");

        configValueHelperRecur(comment, object, config.getClass(), "");

        if (comment.size() > 2)
            object.add("__comment", gson.toJsonTree(comment));
        return object;
    }

    private void configValueHelperRecur(List<String> comment, JsonObject object, Class<?> clazz, String prefix) {
        List<Field> fields = getFields(clazz);
        for (var entry : object.entrySet()) {
            FieldMatch fieldMatch = match(fields, entry.getKey());

            if (fieldMatch == null)
                continue;

            if (entry.getValue() instanceof JsonObject) {
                String prefix2 = prefix.equals("") ? fieldMatch.displayName : prefix + "." + fieldMatch.displayName;
                configValueHelperRecur(comment, (JsonObject) entry.getValue(), fieldMatch.field.getType(), prefix2);
            } else {
                String start = prefix + (prefix.length() > 0 ? "." : "") + fieldMatch.displayName + ": ";

                String end = "";
                Description description = fieldMatch.field.getAnnotation(Description.class);
                if (description != null)
                    end += description.value();

                if (fieldMatch.field.getType().isEnum()) {
                    comment.add(start + end + " " + Arrays.toString(fieldMatch.field.getType().getEnumConstants()));
                } else if (end.length() > 0) {
                    comment.add(start + end);
                }
            }
        }
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
            if (file.getName().equals(name + ".json")) {
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
