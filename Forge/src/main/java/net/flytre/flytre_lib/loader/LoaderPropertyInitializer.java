package net.flytre.flytre_lib.loader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class LoaderPropertyInitializer {


    private LoaderPropertyInitializer() {
    }

    public static void init(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        final ArgumentAcceptingOptionSpec<Path> gameDir = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING)).defaultsTo(Path.of("."));
        parser.allowsUnrecognizedOptions();
        final OptionSet optionSet = parser.parse(args);
        var configDir = Paths.get(optionSet.valueOf(gameDir).toString(), FMLPaths.CONFIGDIR.relative().toString());


        LoaderPropertiesImpl.init(configDir);
        LoaderAgnosticRegistryImpl.init();
        ScreenLoaderUtilsImpl.init();
        ItemTabCreatorImpl.init();
        RenderLayerRegistryImpl.init();
    }

}
