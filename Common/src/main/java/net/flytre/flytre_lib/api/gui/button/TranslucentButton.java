package net.flytre.flytre_lib.api.gui.button;

import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Creates a translucent button
 */
public class TranslucentButton extends ButtonWidget {
    public TranslucentButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            MinecraftClient client = MinecraftClient.getInstance();
            int color = RenderUtils.getModernUiColor(active, isHovered());

            RenderUtils.drawRect(x, y, x + width, y + height, color);
            drawCenteredText(matrixStack, client.textRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, 0xffffff);
        }
    }


}
