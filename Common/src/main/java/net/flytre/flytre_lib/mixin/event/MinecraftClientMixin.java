package net.flytre.flytre_lib.mixin.event;

import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {


    @Shadow
    private Profiler profiler;

    @Inject(at = @At("HEAD"), method = "tick")
    private void flytre_lib$onStartTick(CallbackInfo info) {
        profiler.push("flytreLibStartClientTick");
        ClientTickEvents.START_CLIENT_TICK.getListeners().forEach(i -> {
            profiler.push(i.getClass().getName());
            i.onTickClient((MinecraftClient) (Object) this);
            profiler.pop();
        });
        profiler.pop();
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void flytre_lib$onEndTick(CallbackInfo info) {
        profiler.push("flytreLibEndClientTick");
        ClientTickEvents.END_CLIENT_TICK.getListeners().forEach(i -> {
            profiler.push(i.getClass().getName());
            i.onTickClient((MinecraftClient) (Object) this);
            profiler.pop();
        });
        profiler.pop();    }
}
