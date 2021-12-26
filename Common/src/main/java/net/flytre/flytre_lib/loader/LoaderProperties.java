package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.init.FlytreLibConfig;

import java.nio.file.Path;
import java.util.function.Function;

public final class LoaderProperties {
    public static ConfigHandler<FlytreLibConfig> HANDLER = null;
    private static boolean DEV_ENVIRONMENT = false;
    private static Function<String, String> MOD_ID_TO_NAME = null;
    private static Path MOD_CONFIG_DIRECTORY;

    public static void setModConfigDirectory(Path modConfigDirectory) {
        MOD_CONFIG_DIRECTORY = modConfigDirectory;
    }


    public static Path getModConfigDirectory() {
        return MOD_CONFIG_DIRECTORY;
    }

    public static String getModName(String modId) {
        if (MOD_ID_TO_NAME == null)
            throw new AssertionError("");
        else
            return MOD_ID_TO_NAME.apply(modId);
    }

    public static void setModIdToName(Function<String, String> modIdToName) {
        MOD_ID_TO_NAME = modIdToName;
    }

    public static boolean isDevEnvironment() {
        return DEV_ENVIRONMENT;
    }

    public static void setDevEnvironment(boolean isDevEnvironment) {
        DEV_ENVIRONMENT = isDevEnvironment;
    }
}
