package net.flytre.flytre_lib.mixin.base;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServerWatchdog.class)
public class DedicatedServerWatchdogMixin {

    @Mutable
    @Shadow @Final private long maxTickTime;

    @Inject(method="<init>", at = @At("TAIL"))
    public void flytre_lib$infiniteDebuggingTime(MinecraftDedicatedServer server, CallbackInfo ci) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            maxTickTime = Long.MAX_VALUE / 2;
    }
}
