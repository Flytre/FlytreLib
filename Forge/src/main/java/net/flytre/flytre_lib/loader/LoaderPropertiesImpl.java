package net.flytre.flytre_lib.loader;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

final class LoaderPropertiesImpl implements LoaderProperties.Delegate {

    private final Path configDir;

    private LoaderPropertiesImpl(Path configDir) {
        this.configDir = configDir;
    }

    public static void init(Path configDir) {
        LoaderProperties.setDelegate(new LoaderPropertiesImpl(configDir));
    }


    @Override
    public Path getModConfigDirectory() {
        return configDir;
    }

    @Override
    public String getModName(String id) {
        return ModList.get().getModContainerById(id)
                .map(modContainer -> modContainer.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(id));

    }

    @Override
    public boolean isDevEnvironment() {
        return !FMLEnvironment.production;
    }
}
