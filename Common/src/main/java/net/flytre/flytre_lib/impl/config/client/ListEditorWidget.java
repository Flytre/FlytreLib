package net.flytre.flytre_lib.impl.config.client;

import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.flytre.flytre_lib.api.gui.text_field.NumberBox;
import net.flytre.flytre_lib.api.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.mixin.config.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

/**
 * Used to edit a list of values, including adding and removing values
 */

@ApiStatus.Internal
class ListEditorWidget<K extends ClickableWidget> extends ConfigStyleList<ListEditorWidget.ValueEntry<K>> {

    public ListEditorWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
    }

    public void addEntry(K clickable) {
        addEntry(new ValueEntry<>(this, clickable));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        ValueEntry<K> entry = getEntryAtPosition2(mouseX, mouseY);
        if (entry != null && isActive(entry.clickable) && entry.clickable.isMouseOver(mouseX, mouseY)) {
            if (entry.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    private boolean isActive(K clickable) {
        return clickable instanceof TranslucentTextField ? ((TranslucentTextField) clickable).isActive() : clickable.isFocused();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        ValueEntry<K> entry = this.getEntryAtPosition2(mouseX, mouseY);

        children().forEach(i -> {
            if (i != entry && i.clickable instanceof TranslucentTextField)
                ((TranslucentTextField) i.clickable).setTextFieldFocused(false);
        });

        if (entry != null) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(entry);
                this.setDragging(true);
                return true;
            }
        }

        return ((EntryListWidgetAccessor) this).getScrolling();
    }

    @Override
    protected ValueEntry<K> getEntryAtPosition2(double x, double y) {
        for (var entry : children()) {
            if (entry.clickable.isMouseOver(x, y)) {
                return entry;
            }
        }

        for (var entry : children()) {
            if (entry.canceller.isMouseOver(x, y))
                return entry;
        }
        return null;
    }


    public static class ValueEntry<K extends ClickableWidget> extends Entry<ValueEntry<K>> {


        private final K clickable;
        private final TranslucentButton canceller;

        public ValueEntry(ListEditorWidget<K> list, K clickable) {
            this.clickable = clickable;
            this.canceller = new TranslucentButton(0, 0, 20, 20, Text.of("X"), (button) -> list.removeEntry(this));
        }

        public K getClickable() {
            return clickable;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Arrays.asList(clickable, canceller);
        }

        @Override
        public List<? extends Element> children() {
            return Arrays.asList(clickable, canceller);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            clickable.x = (int) (x + entryWidth * 0.5 - clickable.getWidth() / 2) - 5;

            clickable.y = y;


            canceller.x = clickable.x + clickable.getWidth() + 10;
            canceller.y = y;

            if (clickable instanceof NumberBox)
                clickable.x -= 20;

            clickable.render(matrices, mouseX, mouseY, tickDelta);
            canceller.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public int getEntryHeight(int baseHeight) {
            if (clickable instanceof TranslucentTextField) {
                TranslucentTextField textField = (TranslucentTextField) clickable;
                return textField.isActive() ? Math.max(baseHeight, textField.getFullHeight() + 10) : baseHeight;
            }
            return clickable.getHeight();
        }
    }

}
