package net.flytre.flytre_lib.mixin.loader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.flytre.flytre_lib.api.event.CommandRegistrationEvent;
import net.flytre.flytre_lib.impl.config.ReloadConfigCommand;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.main.Main;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(value = Main.class, priority = 1)
public class MainMixin {

    @Shadow
    @Nullable
    private static <T> T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
        throw new AssertionError();
    }

    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$setLoaderProperties(String[] args, CallbackInfo ci) {

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        final ArgumentAcceptingOptionSpec<Path> gameDir = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING)).defaultsTo(Path.of("."));
        parser.allowsUnrecognizedOptions();
        final OptionSet optionSet = parser.parse(args);
        var configDir = Paths.get(optionSet.valueOf(gameDir).toString(), FMLPaths.CONFIGDIR.relative().toString());

        LoaderProperties.setModConfigDirectory(configDir);
        LoaderProperties.setDevEnvironment(false);
        LoaderProperties.setModIdToName(id -> ModList.get().getModContainerById(id)
                .map(modContainer -> modContainer.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(id)));

        CommandRegistrationEvent.EVENT.register((dispatcher, dedicated) -> ReloadConfigCommand.register(dispatcher));

    }
}
