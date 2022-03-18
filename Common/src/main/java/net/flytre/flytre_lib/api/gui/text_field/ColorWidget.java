package net.flytre.flytre_lib.api.gui.text_field;

import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.api.base.util.Formatter;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * A text field with an advanced color picker
 */

public class ColorWidget extends TranslucentTextField {

    private final int radius = 50; //Hardcoded value
    private final int sliderWidth = radius * 3 / 2;  //Hardcoded value
    private int color = 0;
    private float brightness = 1f;
    private float alpha = 1f;

    public ColorWidget(int x, int y, int width, int height, Text message) {
        super(x + 25, y, width - 25, height, message);
    }

    public int getCenterY() {
        return y + height + radius + 5;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        int color = this.isFocused() ? 0xCC363636 : 0xCC1a1a1a;

        RenderUtils.drawBorderedRect(new Rectangle(x - 25, y - 1, 22, height + 2), this.color, color, 1);

        if (this.isActive()) {

            if (drawsBackground) {
                RenderUtils.drawBorderedRect(getFocusedBounds(), 0x446b6b6b, 0xCC3d3d3d, 1);
            }

            RenderUtils.drawColorWheel(x + 1 + radius * 3 / 2, getCenterY(), radius, brightness, alpha);

            //sliders
            {
                RenderUtils.drawRectangle(getWidthSliderBounds(), 0x446b6b6b);
                RenderUtils.drawRectangle(getWidthSliderValueBounds(), 0x88000000);
                RenderUtils.drawRectangle(getAlphaSliderBounds(), 0x446b6b6b);
                RenderUtils.drawRectangle(getAlphaSliderValueBounds(), 0x88000000);
            }
        }
    }

    public Rectangle getFocusedBounds() {
        return new Rectangle(x + 1, y + height + 1, radius * 3, radius * 2 + 45);
    }


    public Rectangle getAlphaSliderBounds() {
        int offset = radius * 3 / 2 - (sliderWidth) / 2;
        return new Rectangle(x + 1 + offset, getCenterY() + radius + 20, sliderWidth, 5);
    }

    public Rectangle getAlphaSliderValueBounds() {
        int offset = radius * 3 / 2 - (sliderWidth) / 2;
        int valueOffset = (int) (alpha * (sliderWidth - 7));
        return new Rectangle(x + 1 + offset + valueOffset, getCenterY() + radius + 20 - 1, 7, 7);
    }

    public Rectangle getWidthSliderBounds() {
        int offset = radius * 3 / 2 - (sliderWidth) / 2;
        return new Rectangle(x + 1 + offset, getCenterY() + radius + 10, sliderWidth, 5);
    }

    public Rectangle getWidthSliderValueBounds() {
        int offset = radius * 3 / 2 - (sliderWidth) / 2;
        int valueOffset = (int) (brightness * (sliderWidth - 7));
        return new Rectangle(x + 1 + offset + valueOffset, getCenterY() + radius + 10 - 1, 7, 7);
    }


    @Override
    public void setWidth(int value) {
        super.setWidth(value);
    }

    public int getColor() {
        int ret = color;
        if (text.length() < 8 && ((color >> 24 & 255) == 0))
            ret |= 0xFF000000;
        return ret;
    }

    @Override
    protected void onChanged(String newText) {
        try {
            color = Formatter.fromHexString(newText);
        } catch (NumberFormatException e) {
            TextColor t = TextColor.parse(newText);
            color = t != null ? (0xFF000000 | t.getRgb()) : 0x446b6b6b;
        }
        super.onChanged(newText);
    }

    private boolean updateBrightness(double mouseX, double mouseY, int button) {
        Rectangle slider = getWidthSliderBounds();
        Rectangle sliderModifier = slider.expandedBy(2).horizontallyExpandedBy(30);
        if (sliderModifier.contains(mouseX, mouseY)) {
            brightness = MathHelper.clamp((float) ((mouseX - slider.getLeft()) / slider.getWidth()), 0f, 1f);
            return true;
        }
        return false;
    }

    private boolean updateAlpha(double mouseX, double mouseY, int button) {
        Rectangle slider = getAlphaSliderBounds();
        Rectangle sliderModifier = slider.expandedBy(2).horizontallyExpandedBy(30);
        if (sliderModifier.contains(mouseX, mouseY)) {
            alpha = MathHelper.clamp((float) ((mouseX - slider.getLeft()) / slider.getWidth()), 0f, 1f);
            return true;
        }
        return false;
    }

    @Override
    public int getWidth() {
        return super.getWidth() + 25;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (updateBrightness(mouseX, mouseY, button))
            return true;
        if (updateAlpha(mouseX, mouseY, button))
            return true;
        {
            int centerX = x + 1 + radius * 3 / 2;
            int centerY = getCenterY();
            double dist = Math.sqrt(Math.pow((mouseX - centerX), 2) + Math.pow((mouseY - centerY), 2));
            if (dist < radius) {
                double phi = Math.atan2(mouseY - centerY, mouseX - centerX);
                int color = Color.HSBtoRGB((float) (phi / (2 * Math.PI)), (float) dist / radius, brightness);
                String hex = String.format("#%02x%02x%02x%02x", (int) (alpha * 255), color >> 16 & 255, color >> 8 & 255, color & 255);
                if (hex.startsWith("#ff") && hex.length() == 9)
                    hex = "#" + hex.substring(3);
                setText(hex);
                return true;
            }
        }


        if (getFocusedBounds().contains(mouseX, mouseY))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (updateBrightness(mouseX, mouseY, button))
            return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY))
            return true;
        return isFocused() && getFocusedBounds().contains(mouseX, mouseY);
    }


    @Override
    public int getFullHeight() {
        return height + (isActive() ? getFocusedBounds().getHeight() : 0);
    }
}
