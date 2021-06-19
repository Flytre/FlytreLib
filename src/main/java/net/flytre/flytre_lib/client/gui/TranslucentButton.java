package net.flytre.flytre_lib.client.gui;

import net.flytre.flytre_lib.client.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Translucent button from Biome Locator
 */
public class TranslucentButton extends ButtonWidget {
    public TranslucentButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            MinecraftClient client = MinecraftClient.getInstance();
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


}
