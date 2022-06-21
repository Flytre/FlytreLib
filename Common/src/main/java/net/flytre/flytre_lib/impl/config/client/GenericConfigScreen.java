package net.flytre.flytre_lib.impl.config.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Every config screen inherits from this, automatically jumps to the parent screen when closed and has the fun
 * background and animation
 */

@ApiStatus.Internal
public abstract class GenericConfigScreen extends Screen {

    public static final Identifier BACKGROUND = new Identifier("flytre_lib:textures/gui/config/background.png");
    protected @Nullable
    final Screen parent;
    protected final @Nullable ButtonWidget reopen; //Basically references the button that created this screen, to recreate this screen with updated values
    private float animationTime;


    public GenericConfigScreen(@Nullable Screen parent, @Nullable ButtonWidget reopen) {
        super(Text.translatable("flytre_lib.gui.config_screen"));
        this.parent = parent;
        this.reopen = reopen;
    }

    public static void tile(Rectangle bounds, int vOffset, float saturation, int green) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(bounds.getLeft(), bounds.getBottom(), 0.0D).texture(0.0F, (float) bounds.getHeight() / 32.0F + (float) vOffset).color((int) (92 * saturation), green, (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(bounds.getRight(), bounds.getBottom(), 0.0D).texture((float) bounds.getWidth() / 32.0F, (float) bounds.getHeight() / 32.0F + (float) vOffset).color((int) (92 * saturation), green, (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(bounds.getRight(), bounds.getTop(), 0.0D).texture((float) bounds.getWidth() / 32.0F, (float) vOffset).color((int) (92 * saturation), (int) (92 * saturation), (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(bounds.getLeft(), bounds.getTop(), 0.0D).texture(0.0F, (float) vOffset).color((int) (92 * saturation), (int) (92 * saturation), (int) (92 * saturation), 255).next();
        tessellator.draw();
    }

    public void reopenAction() {
        if (reopen != null)
            reopen.onPress();

        assert client != null;
        if (client.currentScreen != this) {
            if (client.currentScreen instanceof GenericConfigScreen) {
                GenericConfigScreen scr = (GenericConfigScreen) client.currentScreen;
                scr.disableAnimation();
                scr.getList().setScrollAmount(getList().getScrollAmount());
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        onClose();
    }

    public void onClose() {
        assert this.client != null;
        this.client.setScreen(parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.animationTime += delta * 0.5f; //speed mult
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public GenericConfigScreen disableAnimation() {
        this.animationTime = 100;
        return this;
    }

    @Override
    public void renderBackgroundTexture(int vOffset) {


        float rawTime = this.animationTime / 0.5f;
        float saturation = rawTime * 0.05F;

        if (saturation > 1.0F)
            saturation = 1.0F;

        saturation *= saturation;


        int green = MathHelper.clamp((int) ((92 + (255 - 92) * Math.min(1, animationTime / 15)) * saturation), 0, 255);


        tile(new Rectangle(width, height), vOffset, saturation, green);
    }

    public abstract ConfigStyleList<?> getList();
}
