package net.flytre.flytre_lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigHandler<T> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final T assumed;
    private final String name;
    private T config;

    public ConfigHandler(T assumed, String name) {
        this.assumed = assumed;
        this.name = name;

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
            Path path = Paths.get(location.toString(), name + ".json");
            Writer writer;
            try {
                writer = new FileWriter(path.toFile());
                GSON.toJson(assumed, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = assumed;
        } else {
            try (Reader reader = new FileReader(configFile)) {
                this.config = GSON.fromJson(reader, (Type) assumed.getClass());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public T getConfig() {
        return config;
    }
}
