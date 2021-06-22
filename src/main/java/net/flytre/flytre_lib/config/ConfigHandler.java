package net.flytre.flytre_lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if(this.config instanceof ConfigEventAcceptor)
            ((ConfigEventAcceptor) this.config).onReload();

    }

    public T getAssumed() {
        return assumed;
    }

    public T getConfig() {
        return config;
    }
}
