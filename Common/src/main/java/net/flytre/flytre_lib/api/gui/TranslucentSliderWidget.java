package net.flytre.flytre_lib.api.gui;

import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class TranslucentSliderWidget extends SliderWidget {

    public TranslucentSliderWidget(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text, value);
        this.updateMessage();
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            MinecraftClient client = MinecraftClient.getInstance();
            this.renderBackground(matrixStack, client, mouseX, mouseY);
            int color = 0x446b6b6b;
            if (!active) {
                color = 0x440a0a0a;
            } else if (isHovered()) {
                color = 0x886b6b6b;
            }

            RenderUtils.drawRect(x, y, x + width, y + height, color);
            drawCenteredText(matrixStack, client.textRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, 0xffffff);
        }
    }

    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        int left = this.x + (int) (this.value * (double) (this.width - 8));
        int top = this.y;
        RenderUtils.drawRect(left, top, left + 8, top + 20, 0xA06b6b6b);
    }
}
