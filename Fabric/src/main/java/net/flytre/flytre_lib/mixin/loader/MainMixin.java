package net.flytre.flytre_lib.mixin.loader;


import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 1)
public abstract class MainMixin {


    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$setLoaderProperties(String[] args, CallbackInfo ci) {
        LoaderProperties.setDevEnvironment(FabricLoader.getInstance().isDevelopmentEnvironment());
        LoaderProperties.setModIdToName((id) -> FabricLoader.getInstance().getModContainer(id).map(ModContainer::getMetadata).map(ModMetadata::getName).orElse(id));
        LoaderProperties.setModConfigDirectory(FabricLoader.getInstance().getConfigDir());
    }
}
