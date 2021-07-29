package net.flytre.flytre_lib.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

/**
 * Load configs on server start; Automatic OP when in dev environment
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$reloadConfigs(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        ConfigRegistry.reloadServerConfigs(null);
    }

    @Inject(method = "getPermissionLevel", at = @At("HEAD"), cancellable = true)
    public void flytre_lib$always_op(GameProfile profile, CallbackInfoReturnable<Integer> cir) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            cir.setReturnValue(4);
    }
}
