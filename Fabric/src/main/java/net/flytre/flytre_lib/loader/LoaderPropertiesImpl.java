package net.flytre.flytre_lib.loader;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.nio.file.Path;

final class LoaderPropertiesImpl implements LoaderProperties.Delegate {


    private LoaderPropertiesImpl() {

    }

    public static void init() {
        LoaderProperties.setDelegate(new LoaderPropertiesImpl());
    }


    @Override
    public Path getModConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getModName(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(ModContainer::getMetadata).map(ModMetadata::getName).orElse(modId);
    }

    @Override
    public boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
