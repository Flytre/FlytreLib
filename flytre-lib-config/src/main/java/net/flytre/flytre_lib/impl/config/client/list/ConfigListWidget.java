package net.flytre.flytre_lib.impl.config.client.list;

import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
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
            int editorWidth = MathHelper.clamp(250, entryWidth / 5, 3 * entryWidth / 8);
            int rightMargin = Math.min(150, entryWidth / 10);
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            int leftMargin = Math.min(150, entryWidth / 10);
            int textX = x + leftMargin;
            int textWidth = entryWidth - editorWidth - rightMargin - leftMargin - 20;

            //description
            Rectangle bounds = new Rectangle(textX, y+ entryHeight / 2 - textHeight / 2, Math.min(textWidth, renderer.getWidth(key)), textHeight);
            if (bounds.contains(mouseX, mouseY)) {
                textWidth = Math.min(textWidth, x + entryWidth - mouseX - 2);
                int height = RenderUtils.getWrappedHeight(description, textWidth, 3);
                RenderUtils.drawBorderedRect(new Rectangle(mouseX, mouseY, Math.min(textWidth, renderer.getWidth(description)), height).expandedBy(2), 0xAA3b3b3b, 0xCC363636, 1);
                RenderUtils.drawWrappedString(matrices, description, mouseX, mouseY, textWidth, 3, 0xFFCCCCCC);
            }
        }
    }
}
