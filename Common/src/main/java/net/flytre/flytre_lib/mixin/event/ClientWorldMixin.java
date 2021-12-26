package net.flytre.flytre_lib.mixin.event;

import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "tickEntities", at = @At("RETURN"))
    private void flytre_lib$endWorldTick(CallbackInfo ci) {
        Profiler profiler = client.getProfiler();
        profiler.push("flytreLibEndWorldTick");
        ClientTickEvents.END_WORLD_TICK.getListeners().forEach(i -> {
            profiler.push(i.getClass().getName());
            i.onWorldTick((ClientWorld) (Object) this);
            profiler.pop();
        });
        profiler.pop();
    }

    @Inject(method = "tickEntities", at = @At("HEAD"))
    private void flytre_lib$startWorldTick(CallbackInfo ci) {
        Profiler profiler = client.getProfiler();
        profiler.push("flytreLibStartWorldTick");
        ClientTickEvents.START_WORLD_TICK.getListeners().forEach(i -> {
            profiler.push(i.getClass().getName());
            i.onWorldTick((ClientWorld) (Object) this);
            profiler.pop();
        });
        profiler.pop();
    }
}
