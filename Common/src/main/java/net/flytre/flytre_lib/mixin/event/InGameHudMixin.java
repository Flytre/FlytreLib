package net.flytre.flytre_lib.mixin.event;


import net.flytre.flytre_lib.api.event.HudRenderEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("SpellCheckingInspection")
@Mixin(InGameHud.class)
class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
            slice = @Slice(
                    from =
                    @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")))
    public void flytre_lib$render(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
        HudRenderEvent.EVENT.getListeners().forEach(i -> i.onRender(matrixStack, tickDelta));
    }
}
