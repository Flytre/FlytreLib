package net.flytre.flytre_lib.config.client.list;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.client.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.mixin.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;


/**
 * Maps a string to some sort of value
 */
@Environment(EnvType.CLIENT)
public class StringValueWidget<K extends ClickableWidget> extends ConfigStyleList<StringValueWidget.PairEntry<K>> {

    public StringValueWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
    }

    public void addEntry(String key, K textField) {
        addEntry(new PairEntry<>(key, textField));
    }


    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        PairEntry<K> entry = getEntryAtPosition2(mouseX, mouseY);
        if (entry != null && entry.value instanceof TranslucentTextField && ((TranslucentTextField) entry.value).isActive() && entry.value.isMouseOver(mouseX, mouseY)) {
            if (entry.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            PairEntry<K> entry = this.getEntryAtPosition2(mouseX, mouseY);

            children().forEach(i -> {
                if (i != entry && i.value.isFocused())
                    i.value.changeFocus(false);
            });

            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return ((EntryListWidgetAccessor) this).getScrolling();
        }
    }

    @Override
    protected PairEntry<K> getEntryAtPosition2(double x, double y) {
        for (var entry : children()) {
            if (entry.value.isMouseOver(x, y)) {
                return entry;
            }
        }
        return null;
    }


    @Environment(EnvType.CLIENT)
    public static class PairEntry<K extends ClickableWidget> extends ConfigStyleList.Entry<PairEntry<K>> {


        protected final String key;
        protected final K value;
        protected int textHeight;

        public PairEntry(String key, K value) {
            this.key = key;
            this.value = value;
        }

        public K getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Collections.singletonList(value);
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(value);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int editorWidth = MathHelper.clamp(250, entryWidth / 5, 3 * entryWidth / 8);
            int rightMargin = Math.min(150, entryWidth / 10);


            int leftMargin = Math.min(150, entryWidth / 10);
            //name
            int textX = x + leftMargin;
            int textWidth = entryWidth - editorWidth - rightMargin - leftMargin - 20;

            textHeight = RenderUtils.getWrappedHeight(key, textWidth, 3);


            textHeight = RenderUtils.drawWrappedString(matrices, key, textX, y + entryHeight / 2 - textHeight / 2, textWidth, 3, 0xFFAAAAAA);


            int valueHeight = value instanceof TranslucentTextField ? ((TranslucentTextField) value).getFullHeight() : value.getHeight();
            value.setWidth(editorWidth);
            value.x = x + entryWidth - editorWidth - rightMargin;
            value.y = y + entryHeight / 2 - valueHeight / 2;
            value.render(matrices, mouseX, mouseY, tickDelta);
        }


        @Override
        public int getEntryHeight(int baseHeight) {
            int value = Math.max(baseHeight, textHeight + 10);
            if (this.value instanceof TranslucentTextField && ((TranslucentTextField) this.value).isActive()) {
                value = Math.max(value, ((TranslucentTextField) this.value).getFullHeight() + 10);
            }
            return value;
        }
    }

}
