package net.flytre.flytre_lib.config.client.list;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.client.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.mixin.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Used to edit a list of values, including adding and removing values
 */
@Environment(EnvType.CLIENT)
public class ListEditorWidget<K extends TranslucentTextField> extends ConfigStyleList<ListEditorWidget.ValueEntry<K>> {

    public ListEditorWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
    }

    public void addEntry(K textField) {
        addEntry(new ValueEntry<>(this, textField));
    }

    @Override
    public int getRowWidth() {
        return this.right - this.left;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        ValueEntry<K> entry = getEntryAtPosition2(mouseX, mouseY);
        if (entry != null && entry.textField.isActive() && entry.textField.isMouseOver(mouseX, mouseY)) {
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
            ValueEntry<K> entry = this.getEntryAtPosition2(mouseX, mouseY);

            children().forEach(i -> {
                if (i != entry)
                    i.textField.setTextFieldFocused(false);
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
    protected ValueEntry<K> getEntryAtPosition2(double x, double y) {
        for (var entry : children()) {
            if (entry.textField.isMouseOver(x, y)) {
                return entry;
            }
        }

        for (var entry : children()) {
            if (entry.canceller.isMouseOver(x, y))
                return entry;
        }
        return null;
    }


    @Environment(EnvType.CLIENT)
    public static class ValueEntry<K extends TranslucentTextField> extends ConfigStyleList.Entry<ValueEntry<K>> {


        private final K textField;
        private final TranslucentButton canceller;

        public ValueEntry(ListEditorWidget<K> list, K textField) {
            this.textField = textField;
            this.canceller = new TranslucentButton(0, 0, 20, 20, Text.of("X"), (button) -> list.removeEntry(this));
        }

        public K getTextField() {
            return textField;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Arrays.asList(textField, canceller);
        }

        @Override
        public List<? extends Element> children() {
            return Arrays.asList(textField, canceller);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            textField.x = (int) (x + entryWidth * 0.5 - textField.getWidth() / 2) - 5;
            textField.y = y;
            textField.render(matrices, mouseX, mouseY, tickDelta);



            canceller.x = textField.x + textField.getWidth() + 10;
            canceller.y = y;
            canceller.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public int getEntryHeight(int baseHeight) {
            return textField.isActive() ? Math.max(baseHeight, textField.getFullHeight() + 10) : baseHeight;
        }
    }

}
