package net.flytre.flytre_lib.mixin.config;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.impl.config.client.ConfigListerScreen;
import net.flytre.flytre_lib.impl.config.client.GenericConfigScreen;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Lib icon and title screen animation to open the config from the title screen
 */
@Mixin(TitleScreen.class)
abstract class TitleScreenMixin extends Screen {


    @Unique
    private static final Identifier FLYTRE_LIB_TAB = new Identifier("flytre_lib:textures/gui/config/tab.png");
    @Unique
    private static final Identifier TAB_BACKGROUND = new Identifier("flytre_lib:textures/gui/config/background.png");
    @Unique
    private static final float ANIMATION_TIME = 20f;
    @Unique
    private float libAnimationProgress = -1f;
    @Unique
    private boolean animating = false;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void flytre_lib$renderTab(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        if (!LoaderProperties.HANDLER.getConfig().displayTitleScreenConfigButton)
            return;

        matrices.push();
        matrices.translate(0, 0, 100);
        int y = Math.min(height * 2 / 3, height - 80);
        RenderSystem.setShaderTexture(0, FLYTRE_LIB_TAB);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.enableBlend();

        if (!animating)
            drawTexture(matrices, 0, y, 0.0F, 0.0F, 42, 80, 42, 80);

        if (animating) {
            libAnimationProgress -= delta;
            if (libAnimationProgress < 0) {
                animating = false;
                MinecraftClient.getInstance().setScreen(new ConfigListerScreen(this).disableAnimation());
            }
            int maxX = width - 42;
            int currX = (int) (maxX * ((ANIMATION_TIME - libAnimationProgress) / ANIMATION_TIME));
            drawTexture(matrices, currX, y, 0.0F, 0.0F, 42, 80, 42, 80);

            RenderSystem.setShaderTexture(0, TAB_BACKGROUND);
            GenericConfigScreen.tile(new Rectangle(currX - 1, height), 0, 1.0f, 255);
        }
        matrices.pop();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
    public void flytre_lib$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        if (!LoaderProperties.HANDLER.getConfig().displayTitleScreenConfigButton)
            return;

        int y = Math.min(height * 2 / 3, height - 80);
        Rectangle bounds = new Rectangle(0, y, 42, 80);
        if (bounds.contains(mouseX, mouseY) && !animating) {
            libAnimationProgress = ANIMATION_TIME;
            animating = true;
            cir.setReturnValue(true);
        }
    }
}
