package net.flytre.flytre_lib.client.gui.text_field;

import net.flytre.flytre_lib.client.util.DropdownUtils;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.common.util.math.Rectangle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Remember that dropdown menus need special handling if their dropdown intersects
 * with another UI member to handle clicks correctly!
 */
public class DropdownMenu extends TranslucentTextField {

    private final List<String> options;
    private List<String> matchedOptions;
    private BiPredicate<String, String> matcher; //inputs are each option's text, followed by text in the main text field
    private int entryHeight;
    private int entryWidth;
    private OptionRenderer optionRenderer;
    private int maxRenderedEntries;


    private int scroll;
    private boolean scrolling;

    public DropdownMenu(int x, int y, int width, int height, Text message, List<String> options) {
        super(x, y, width, height, message);
        this.options = options;
        this.matchedOptions = options;
        this.matcher = (option, text) -> option.contains(text) && !(option.equals(text));
        this.entryHeight = super.height;
        this.entryWidth = super.width;
        this.optionRenderer = DropdownUtils::defaultRenderer;
        this.maxRenderedEntries = 10;
        this.scroll = 0;
        this.renderer = (renderer, matrices, text, fullText, x1, y1, color, cursor) -> {
            Style style = options.contains(fullText) ? Style.EMPTY : Style.EMPTY.withColor(Formatting.RED);
            return textRenderer.drawWithShadow(matrices, OrderedText.styledForwardsVisitedString(text, style), x1, y1, color);
        };

    }

    public void setMaxRenderedEntries(int maxRenderedEntries) {
        this.maxRenderedEntries = maxRenderedEntries;
    }

    private int maxScroll() {
        return Math.max(0, matchedOptions.size() - maxRenderedEntries);
    }


    private int dropdownYSize() {
        return Math.max(0, entryHeight * Math.min(maxRenderedEntries, matchedOptions.size()) - 2);
    }


    private Rectangle scrollbarPos() {

        float percent1 = MathHelper.clamp((float) maxRenderedEntries / matchedOptions.size(), 0f, 1f);
        int size = (int) (percent1 * dropdownYSize());


        float percent2 = MathHelper.clamp((float) scroll / matchedOptions.size(), 0f, 1f);
        int scrollY = (int) (percent2 * dropdownYSize()) + 1;

        return new Rectangle(this.x + entryWidth, this.y + height + 1 + scrollY, 5, size);
    }

    public void setOptionRenderer(OptionRenderer optionRenderer) {
        this.optionRenderer = optionRenderer;
    }

    public void setEntryHeight(int entryHeight) {
        this.entryHeight = entryHeight;
    }

    public void setEntryWidth(int entryWidth) {
        this.entryWidth = entryWidth;
    }

    public void setMatcher(BiPredicate<String, String> matcher) {
        this.matcher = matcher;
    }

    private void updatedMatchedOptions() {
        matchedOptions = options.stream().filter(i -> matcher.test(i, text)).collect(Collectors.toList());
    }

    @Override
    protected void onChanged(String newText) {
        super.onChanged(newText);
        updatedMatchedOptions();
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if (this.isActive() && drawsBackground) {
            int leftX = this.x + 4 + textXOffset;
            int topY = this.y + (this.height - 8) / 2;

            if (!matchedOptions.isEmpty()) {


                int entries = Math.min(maxRenderedEntries, matchedOptions.size());


                //background
                {
                    Rectangle bounds = new Rectangle(this.x, this.y + height + 1, entryWidth + 6, entryHeight * entries);
                    RenderUtils.drawBorderedRect(bounds, 0x446b6b6b, 0xCC3d3d3d, 1);
                    bounds = new Rectangle(this.x + entryWidth - 1, this.y + height + 2, 1, entryHeight * entries - 2);
                    RenderUtils.drawRectangle(bounds, 0xCC3d3d3d);
                }

                //entries
                {
                    for (int i = 0; i < entries; i++) {
                        String option = matchedOptions.get(i + setScroll(scroll)); //clamp scroll
                        String toRender = optionRenderer.toRender(option, text, textRenderer, renderer, entryWidth, entryHeight, textXOffset);
                        renderer.render(textRenderer, matrices, toRender, option, leftX, topY + height + 1 + entryHeight * i, 0xE0E0E0, false);
                    }
                }

                //highlight
                {
                    int selectionIndex = getSelectionIndex(mouseX, mouseY);
                    if (selectionIndex >= 0 && selectionIndex < matchedOptions.size()) {
                        Rectangle rectangle = new Rectangle(this.x, this.y + height + 1 + entryHeight * selectionIndex, entryWidth, entryHeight).shrink(1);
                        RenderUtils.drawRectangle(rectangle, 0x33000000);
                    }
                }

                //scrollbar
                {
                    Rectangle bounds = scrollbarPos();
                    RenderUtils.drawRectangle(bounds, 0xAA000000);
                }
            }

            this.hovered = isMouseOver(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        //scroll
        this.checkScrollingOnClick(mouseX, mouseY, button);
        if (scrolling)
            return true;


        //select dropdown option
        if (this.isActive() && this.editable && !matchedOptions.isEmpty()) {
            int selectionIndex = getSelectionIndex((int) mouseX, (int) mouseY);
            if (selectionIndex >= 0 && selectionIndex < Math.min(maxRenderedEntries, matchedOptions.size())) {
                String option = matchedOptions.get(selectionIndex + scroll);
                setText(option);
                return true;
            }
        }

        //default - removed weird focus handling
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //make sure the scroll amount is still valid
    @Override
    public void setText(String text) {
        super.setText(text);
        setScroll(scroll);
    }

    //get the index (no scroll included, just on the screen) of the dropdown option at the current position
    public int getSelectionIndex(int mouseX, int mouseY) {

        if (mouseX < x || mouseX > entryWidth + x)
            return -1;

        int topY = this.y + (this.height - 8) / 2;
        int cy = topY + height + 1, index = 0;

        if (mouseY < cy)
            return -1;

        while (mouseY > cy + 20) {
            index++;
            cy += entryHeight;
        }
        return index;
    }


    protected void checkScrollingOnClick(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && scrollbarPos().contains(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY))
            return true;

        Rectangle bounds = new Rectangle(this.x, this.y + height + 1, entryWidth + scrollbarPos().getWidth(), dropdownYSize());
        return isFocused() && bounds.contains(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling && button == 0) {
            this.setScroll((int) (scroll + deltaY * 0.5));
            return true;
        }

        return false;
    }


    public int setScroll(int scroll) {
        this.scroll = MathHelper.clamp(scroll, 0, maxScroll());
        return this.scroll;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScroll((int) (scroll - amount * entryHeight / 4.0D));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (isActive()) {
            switch (keyCode) {
                case 258: { //tab
                    if (0 < Math.min(maxRenderedEntries, matchedOptions.size())) {
                        String option = matchedOptions.get(scroll);
                        setText(option);
                        return true;
                    }
                }
                case 264:
                    setScroll(scroll + 1);
                    return true;
                case 265:
                    setScroll(scroll - 1);
                    return true;
            }
        }
        return false;
    }

    @Override
    public int getFullHeight() {
        return (isActive() ?  dropdownYSize() : 0) + height;
    }

    @FunctionalInterface
    public interface OptionRenderer {
        String toRender(String option, String text, TextRenderer textRenderer, TextFieldRenderer textFieldRenderer, int entryWidth, int entryHeight, int textXOffset);
    }
}
