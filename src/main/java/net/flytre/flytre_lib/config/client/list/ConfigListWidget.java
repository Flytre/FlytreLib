package net.flytre.flytre_lib.config.client.list;

import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.common.util.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
/**
 * Maps a string (name) + optional description to some sort of value
 */
public class ConfigListWidget extends StringValueWidget<ClickableWidget> {


    public ConfigListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
    }


    public void addConfigEntry(ConfigEntry entry) {
        addEntry(entry);
    }

    @Override
    public int getRowWidth() {
        return this.right - this.left;
    }

    @Override
    protected int getScrollbarPositionX() {
        int rightMargin = Math.max(10, getRowWidth() / 5);
        return width - rightMargin + 20;
    }

    public static class ConfigEntry extends StringValueWidget.PairEntry<ClickableWidget> {


        private final String description;

        public ConfigEntry(ClickableWidget value, String name, String description) {
            super(name, value);
            this.description = description;
        }

        public ClickableWidget getValue() {
            return value;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
            //super math
            int editorWidth = MathHelper.clamp(250, entryWidth / 4, entryWidth * 3 / 4);
            int rightMargin = Math.max(10, entryWidth / 5);
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            int textX = x + entryWidth / 5;
            int textWidth = entryWidth - editorWidth - rightMargin - entryWidth / 5 - 20;

            //description
            Rectangle bounds = new Rectangle(textX, y, Math.min(textWidth, renderer.getWidth(key)), textHeight);
            if (bounds.contains(mouseX, mouseY)) {
                textWidth = Math.min(textWidth, x + entryWidth - mouseX - 2);
                int height = RenderUtils.getWrappedHeight(description, textWidth, 3);
                RenderUtils.drawBorderedRect(new Rectangle(mouseX, mouseY, Math.min(textWidth, renderer.getWidth(description)), height).grow(2), 0xAA3b3b3b, 0xCC363636, 1);
                RenderUtils.drawWrappedString(matrices, description, mouseX, mouseY, textWidth, 3, 0xFFCCCCCC);
            }
        }
    }
}
