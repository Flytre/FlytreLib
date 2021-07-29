package net.flytre.flytre_lib.mixin;


import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Reload client configs on resource reload
 */
@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {

    @Inject(method = "reload", at = @At("RETURN"))
    public void flytre_lib$reloadClientConfig(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        ConfigRegistry.reloadClientConfigs();
    }
}
