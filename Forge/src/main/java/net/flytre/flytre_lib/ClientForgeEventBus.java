package net.flytre.flytre_lib;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.impl.config.client.ConfigListerScreen;
import net.flytre.flytre_lib.impl.config.client.GenericConfigScreen;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@OnlyIn(Dist.CLIENT)
public class ClientForgeEventBus {


    private static final Identifier FLYTRE_LIB_TAB = new Identifier("flytre_lib:textures/gui/config/tab.png");

    private static final Identifier TAB_BACKGROUND = new Identifier("flytre_lib:textures/gui/config/background.png");

    private static final float ANIMATION_TIME = 20f;

    private float libAnimationProgress = -1f;

    private boolean animating = false;

    public ClientForgeEventBus() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void screenDrawEvent(ScreenEvent.DrawScreenEvent event) {
        if(!(event.getScreen() instanceof TitleScreen))
            return;

        MatrixStack matrices = event.getPoseStack();
        int height = event.getScreen().height;
        int width = event.getScreen().width;
        float delta = event.getPartialTick();

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
                MinecraftClient.getInstance().setScreen(new ConfigListerScreen(event.getScreen()).disableAnimation());
            }
            int maxX = width - 42;
            int currX = (int) (maxX * ((ANIMATION_TIME - libAnimationProgress) / ANIMATION_TIME));
            drawTexture(matrices, currX, y, 0.0F, 0.0F, 42, 80, 42, 80);

            RenderSystem.setShaderTexture(0, TAB_BACKGROUND);
            GenericConfigScreen.tile(new Rectangle(currX - 1, height), 0, 1.0f, 255);
        }
        matrices.pop();
    }

    @SubscribeEvent
    public void screenMouseClicked(ScreenEvent.MouseClickedEvent event) {
        if(!(event.getScreen() instanceof TitleScreen))
            return;


        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        int height = event.getScreen().height;

        if (!LoaderProperties.HANDLER.getConfig().displayTitleScreenConfigButton)
            return;

        int y = Math.min(height * 2 / 3, height - 80);
        Rectangle bounds = new Rectangle(0, y, 42, 80);
        if (bounds.contains(mouseX, mouseY) && !animating) {
            libAnimationProgress = ANIMATION_TIME;
            animating = true;

            event.setCanceled(true);
        }
    }


}
