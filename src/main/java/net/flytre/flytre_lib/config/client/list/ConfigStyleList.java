package net.flytre.flytre_lib.config.client.list;

import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.common.util.math.Rectangle;
import net.flytre.flytre_lib.mixin.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public abstract class ConfigStyleList<E extends ConfigStyleList.Entry<E>> extends ElementListWidget<E> {

    public ConfigStyleList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        ((EntryListWidgetAccessor) this).setHoveredEntry(this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition2(mouseX, mouseY) : null);
        int k = this.getRowLeft();
        int l = this.top + 4 - (int) this.getScrollAmount();

        this.renderList(matrices, k, l, mouseX, mouseY, delta);

        int o = this.getMaxScroll();
        if (o > 0) {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int p = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
            p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
            int q = (int) this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
            if (q < this.top) {
                q = this.top;
            }

            Rectangle bounds = Rectangle.ofBounds(i - 1, top - 1, j + 1, bottom + 1);
            RenderUtils.drawHollowRect(bounds, 0xCC3d3d3d, 1);

            bounds = Rectangle.ofBounds(i, q, j, q + p);
            RenderUtils.drawRectangle(bounds, 0xAA333333);
        }

        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }


    @Override
    public void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int entryCount = this.getEntryCount();

        int top;
        int bot = this.top + 4 - (int) this.getScrollAmount() + this.headerHeight;

        for (int entryIterator = 0; entryIterator < entryCount; ++entryIterator) {
            top = bot;
            bot = top + children().get(entryIterator).getEntryHeight(itemHeight);
            if (bot >= this.top && top <= this.bottom) {
                int height = children().get(entryIterator).getEntryHeight(itemHeight) - 4;
                var entry = this.getEntry(entryIterator);
                int width = this.getRowWidth();
                int r = this.getRowLeft();
                entry.render(matrices, entryIterator, top, r, width, height, mouseX, mouseY, Objects.equals(getHoveredEntry(), entry), delta);
            }
        }
    }


    @Override
    protected int getMaxPosition() {
        return headerHeight + children().stream().map(i -> i.getEntryHeight(itemHeight)).reduce(0, Integer::sum);
    }


    protected abstract ConfigStyleList.Entry<E> getEntryAtPosition2(double x, double y);


    public abstract static class Entry<E extends ConfigStyleList.Entry<E>> extends ElementListWidget.Entry<E> implements ParentElement {


        public abstract int getEntryHeight(int baseHeight);
    }
}
