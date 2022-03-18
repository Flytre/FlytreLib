package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.loader.LoaderProperties;
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
class DedicatedServerWatchdogMixin {

    @Mutable
    @Shadow
    @Final
    private long maxTickTime;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void flytre_lib$infiniteDebuggingTime(MinecraftDedicatedServer server, CallbackInfo ci) {
        if (LoaderProperties.isDevEnvironment())
            maxTickTime = Long.MAX_VALUE / 2;
    }
}
