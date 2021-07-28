package net.flytre.flytre_lib.config.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GenericConfigScreen extends Screen {

    public static final Identifier BACKGROUND = new Identifier("flytre_lib:textures/gui/config/background.png");
    protected @Nullable
    final Screen parent;
    private float animationTime;

    public GenericConfigScreen(@Nullable Screen parent) {
        super(new TranslatableText("flytre_lib.gui.config_screen"));
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        this.client.setScreen(parent);
    }


    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.animationTime += delta * 0.5f; //speed mult
        this.renderBackground(matrices);
    }

    public void renderBackgroundTexture(int vOffset) {


        float rawTime = this.animationTime / 0.5f;
        float saturation = rawTime * 0.05F;

        if (saturation > 1.0F)
            saturation = 1.0F;

        saturation *= saturation;


        int green = (int) ((92 + (255 - 92) * Math.min(1, animationTime / 15)) * saturation);


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float) this.height / 32.0F + (float) vOffset).color((int) (92 * saturation), green, (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float) this.width / 32.0F, (float) this.height / 32.0F + (float) vOffset).color((int) (92 * saturation), green, (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float) this.width / 32.0F, (float) vOffset).color((int) (92 * saturation), (int) (92 * saturation), (int) (92 * saturation), 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float) vOffset).color((int) (92 * saturation), (int) (92 * saturation), (int) (92 * saturation), 255).next();
        tessellator.draw();
    }
}
