package net.flytre.flytre_lib.api.gui.text_field;

import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * A text field that only allows valid numbers and has arrows to adjust the number quickly
 */
public class NumberBox extends TranslucentTextField {

    private final boolean integers;

    public NumberBox(int x, int y, int width, int height, Text message, boolean integers, double startingValue) {
        this(x, y, width, height, message, integers, startingValue, null);
    }


    public NumberBox(int x, int y, int width, int height, Text message, boolean integers, double startingValue,  @Nullable ValueRange range) {
        super(x, y, width - 20, height, message);
        rightAligned = true;
        textPredicate = text -> {
            try {
                double val;
                if (integers)
                    val = Integer.parseInt(text);
                else
                    val = Double.parseDouble(text);

                return range == null || (val >= range.min() && val <= range.max());
            } catch (NumberFormatException e) {
                return false;
            }
        };
        text = integers ? String.valueOf((int) startingValue) : String.valueOf(startingValue);
        this.integers = integers;
    }

    public Rectangle topButton() {
        return new Rectangle(x + width + 2, y - 1, 20, height / 2);
    }

    public Rectangle botButton() {
        return new Rectangle(x + width + 2, y + 1 + height / 2, 20, height / 2);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        if (this.isVisible()) {

            if (this.drawsBackground) {
                Rectangle top = topButton();
                Rectangle bot = botButton();
                int color = this.isFocused() ? 0xCC363636 : 0xCC1a1a1a;
                RenderUtils.drawBorderedRect(top, top.contains(mouseX, mouseY) ? 0x886b6b6b : 0x446b6b6b, color, 1);
                RenderUtils.drawBorderedRect(bot, bot.contains(mouseX, mouseY) ? 0x886b6b6b : 0x446b6b6b, color, 1);

                int offset = (height / 2 - textRenderer.fontHeight) / 2 + 1;
                drawCenteredText(matrices, textRenderer, "▲", top.getCenterX(), y + offset, 0xAACCCCCC);
                drawCenteredText(matrices, textRenderer, "▼", bot.getCenterX(), y + 2 + height / 2 + offset, 0xAACCCCCC);

            }
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || super.isMouseOver(mouseX - 20, mouseY);
    }

    public void increment() {
        double curr = Double.parseDouble(text);
        double amt = Math.pow(10, String.valueOf(Math.abs((int) curr)).length() - 1);

        if (integers)
            amt = Math.max(amt, 1);

        if (!integers)
            setText(String.valueOf(amt + curr));
        else
            setText(String.valueOf((int) (amt + curr)));

    }

    public void decrement() {
        double curr = Double.parseDouble(text);
        double amt = Math.pow(10, String.valueOf(Math.abs((int) curr)).length() - 1);

        if (integers)
            amt = Math.max(amt, 1);

        if (!integers)
            setText(String.valueOf(curr - amt));
        else
            setText(String.valueOf((int) (curr - amt)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (this.isVisible() && editable) {
            if (topButton().contains(mouseX, mouseY)) {
                increment();
            } else if (botButton().contains(mouseX, mouseY)) {
                decrement();
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return (chr == 46 || 48 <= chr && chr <= 57) && super.charTyped(chr, modifiers); //numbers only
    }

    @Override
    public void setWidth(int value) {
        super.setWidth(value - 20);
    }

    public static record ValueRange(double min, double max) {

    }
}
