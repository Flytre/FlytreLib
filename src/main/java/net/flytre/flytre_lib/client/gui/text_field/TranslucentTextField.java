package net.flytre.flytre_lib.client.gui.text_field;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.flytre.flytre_lib.common.util.math.Rectangle;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


@Environment(EnvType.CLIENT)
public class TranslucentTextField extends ClickableWidget {

    public static final ItemStack DEFAULT_ICON;

    static {
        DEFAULT_ICON = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound tag = DEFAULT_ICON.getOrCreateNbt();
        tag.putString("SkullOwner", "MHF_Question");
    }

    protected final TextRenderer textRenderer;
    protected String text; //actual text
    protected boolean editable; //whether the text content can be edited or not
    protected boolean selecting; //Whether part of the text is currently being selected (Not IS selected, BEING selected
    protected Selection selection; //selection start and end
    protected int maxLength; //max input length
    protected boolean drawsBackground; //whether to draw the background
    @Nullable
    protected Function<String, String> suggestion; //rework to take a function
    @Nullable
    protected Consumer<String> listener; //what to do on text changed
    protected Predicate<String> textPredicate; //what constitutes legal text
    protected TextFieldRenderer renderer;
    protected int xScroll = 0; //the scroll, i.e. the offset of rendered text if not all of it can fit in the box
    protected int textXOffset = 0; //the offset before text is drawn, i.e. to have an icon on the left side of the text box
    protected boolean rightAligned = false; //Not compatible with suggestions

    public TranslucentTextField(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        textRenderer = MinecraftClient.getInstance().textRenderer;
        this.text = "";
        this.maxLength = 80;
        this.drawsBackground = true;
        this.editable = true;
        this.textPredicate = Objects::nonNull;
        this.selection = new Selection(0, 0);
        this.renderer = (renderer, matrices, text, fullText, x1, y1, color, cursor) -> textRenderer.drawWithShadow(matrices, OrderedText.styledForwardsVisitedString(text, Style.EMPTY), x1, y1, color);
    }


    public void setRightAligned(boolean rightAligned) {
        this.rightAligned = rightAligned;
    }

    public void setListener(Consumer<String> listener) {
        this.listener = listener;
    }


    public void setTextXOffset(int textXOffset) {
        this.textXOffset = textXOffset;
    }

    private boolean showCursorTime() {
        return System.currentTimeMillis() % 1000 < 500;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return new TranslatableText("gui.narrate.editBox", text, this.getMessage());
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        super.appendDefaultNarrations(builder);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        if (this.textPredicate.test(text)) {
            if (text.length() > this.maxLength) {
                this.text = text.substring(0, this.maxLength);
            } else {
                this.text = text;
            }

            this.setCursorToEnd();
            this.setSelectionEnd(selection.start);
            this.onChanged(text);
        }
    }

    public TranslucentTextField withText(String text) {
        setText(text);
        return this;
    }

    public String getSelectedText() {
        return this.text.substring(selection.ordered().start, selection.ordered().end);
    }

    public void setTextPredicate(Predicate<String> textPredicate) {
        this.textPredicate = textPredicate;
    }


    public void write(String text) {
        int charactersLeft = this.maxLength - this.text.length() + selection.ordered().length();
        String string = SharedConstants.stripInvalidChars(text);
        int length = string.length();
        if (charactersLeft < length) {
            string = string.substring(0, charactersLeft);
            length = charactersLeft;
        }

        String modified = new StringBuilder(this.text).replace(selection.ordered().start, selection.ordered().end, string).toString();
        if (this.textPredicate.test(modified)) {
            this.text = modified;
            this.setSelectionStart(selection.ordered().start + length);
            this.setSelectionEnd(selection.start);
            this.onChanged(this.text);
        }
    }

    protected void onChanged(String newText) {
        if (this.listener != null) {
            this.listener.accept(newText);
        }

    }

    private void delete(int offset) {
        if (Screen.hasControlDown()) {
            this.deleteWords(offset);
        } else {
            this.deleteCharacters(offset);
        }

    }

    private void deleteWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (selection.end != selection.start) {
                this.write(""); //delete selection
            } else {
                this.deleteCharacters(this.getWordSkipPosition(wordOffset) - selection.start);
            }
        }
    }

    public void deleteCharacters(int characterOffset) {
        if (!this.text.isEmpty()) {
            if (selection.end != selection.start) {
                this.write("");
            } else {
                int cursorPos = this.getCursorPosWithOffset(characterOffset); //move cursor by offset and return position
                int leftBound = Math.min(cursorPos, selection.start);
                int rightBound = Math.max(cursorPos, selection.start); //basically the bounds, deletes everything from the offset to original cursor position (represented by start selection)
                if (leftBound != rightBound) {
                    String modifier = new StringBuilder(this.text).delete(leftBound, rightBound).toString();
                    if (this.textPredicate.test(modifier)) {
                        this.text = modifier;
                        this.setCursor(leftBound);
                    }
                }
            }
        }
    }

    public int getWordSkipPosition(int wordOffset) { //Get the position of the word x words offset from the cursor
        TextFieldWidget widget = new TextFieldWidget(textRenderer, x, y, width, height, new LiteralText(""));
        widget.setText(getText());
        widget.setSelectionStart(selection.start);
        return widget.getWordSkipPosition(wordOffset);
    }

    public void moveCursor(int offset) {
        this.setCursor(this.getCursorPosWithOffset(offset));
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(this.text, selection.start, offset);
    }


    public void setSelectionStart(int cursor) {
        this.selection = selection.withStart(MathHelper.clamp(cursor, 0, this.text.length()));
    }

    public void setSelectionEnd(int index) {

        int length = this.text.length();
        this.selection = selection.withEnd(MathHelper.clamp(index, 0, length));

        if (this.textRenderer != null) {

            if (this.xScroll > length)
                this.xScroll = length;

            int width = this.getInnerWidth();
            String string = this.textRenderer.trimToWidth(this.text.substring(this.xScroll), width - textXOffset);
            int length2 = string.length() + this.xScroll;
            if (selection.end == this.xScroll) {
                this.xScroll -= this.textRenderer.trimToWidth(this.text, width - textXOffset, true).length();
            }

            if (selection.end > length2) {
                this.xScroll += selection.end - length2;
            } else if (selection.end <= this.xScroll) {
                this.xScroll -= this.xScroll - selection.end;
            }

            this.xScroll = MathHelper.clamp(this.xScroll, 0, length);
        }

    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isActive()) {
            return false;
        } else {
            this.selecting = Screen.hasShiftDown();
            if (Screen.isSelectAll(keyCode)) { // command + A
                this.setCursorToEnd();
                this.setSelectionEnd(0);
                return true;
            } else if (Screen.isCopy(keyCode)) { //command + C
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                return true;
            } else if (Screen.isPaste(keyCode)) { //command + V
                if (this.editable) {
                    this.write(MinecraftClient.getInstance().keyboard.getClipboard());
                }

                return true;
            } else if (Screen.isCut(keyCode)) { //command + X
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                if (this.editable) {
                    this.write("");
                }

                return true;
            } else {
                switch (keyCode) {
                    case 259: //backspace
                        if (this.editable) {
                            this.selecting = false;
                            this.delete(-1);
                            this.selecting = Screen.hasShiftDown();
                        }
                        return true;
                    case 261: //delete
                        if (this.editable) {
                            this.selecting = false;
                            this.delete(1);
                            this.selecting = Screen.hasShiftDown();
                        }

                        return true;
                    case 262: //right
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 263: //left
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 268: //home
                        this.setCursorToStart();
                        return true;
                    case 269: //end
                        this.setCursorToEnd();
                        return true;
                    default:
                        return false;
                }
            }
        }
    }

    public boolean isActive() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isActive()) {
            return false;
        } else if (SharedConstants.isValidChar(chr)) {
            if (this.editable) {
                this.write(Character.toString(chr));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) {
            return false;
        } else {
            boolean inBounds = mouseX >= (double) this.x && mouseX < (double) (this.x + this.width) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
            this.setTextFieldFocused(inBounds);

            if (this.isFocused() && inBounds && button == 0) {
                int i = MathHelper.floor(mouseX) - this.x - textXOffset;
                if (this.drawsBackground) {
                    i -= 4; //border thickness
                }

                String string = this.textRenderer.trimToWidth(this.text.substring(this.xScroll), this.getInnerWidth());
                this.setCursor(this.textRenderer.trimToWidth(string, i).length() + this.xScroll);
                return true;
            } else {
                return false;
            }
        }
    }

    public void setTextFieldFocused(boolean focused) {
        this.setFocused(focused);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isVisible()) {
            int color;
            if (this.drawsBackground) {
                color = this.isFocused() ? 0xCC363636 : 0xCC1a1a1a;
                RenderUtils.drawBorderedRect(new Rectangle(x - 1, y - 1, width + 2, height + 2), 0x446b6b6b, color, 1);
            }

            color = this.editable ? 0xE0E0E0 : 0x707070;
            int selectionStartLocation = selection.start - xScroll;
            int selectionEndLocation = selection.end - xScroll;
            String string = this.textRenderer.trimToWidth(this.text.substring(this.xScroll), this.getInnerWidth()-14);
            final boolean selectionStartLocationInBounds = selectionStartLocation >= 0 && selectionStartLocation <= string.length();
            final boolean cursorBlinkIn = this.isFocused() && showCursorTime() && selectionStartLocationInBounds;
            int leftX = (this.drawsBackground ? this.x + 4 : this.x) + textXOffset;
            int topY = this.drawsBackground ? this.y + (this.height - 8) / 2 : this.y;
            int o;
            if (selectionEndLocation > string.length()) {
                selectionEndLocation = string.length();
            }

            {
                String string2 = selectionStartLocationInBounds ? string.substring(0, selectionStartLocation) : string;
                if (!rightAligned) {
                    o = renderer.render(textRenderer, matrices, string2, text, leftX, topY, color, false);
                } else {
                    int rightX = getInnerWidth() + leftX;
                    int width = textRenderer.getWidth(string);
                    o = renderer.render(textRenderer, matrices, string2, text, rightX - width, topY, color, false);
                }
            }

            boolean bl3 = selection.start < this.text.length() || this.text.length() >= this.getMaxLength();
            int p = o;
            if (!selectionStartLocationInBounds) {
                p = selectionStartLocation > 0 ? leftX + this.width : leftX;
            } else if (bl3) {
                p = o - 1;
                --o;
            }

            if (!string.isEmpty() && selectionStartLocationInBounds && selectionStartLocation < string.length()) {
                renderer.render(textRenderer, matrices, string.substring(selectionStartLocation), text, o, topY, color, true);
            }

            if (!bl3 && this.suggestion != null) {
                @Nullable String suggested = this.suggestion.apply(string);
                if (suggested != null)
                    this.textRenderer.drawWithShadow(matrices, suggested, (float) (p - 1), (float) topY, -8355712);
            }

            int y1;
            int x2;
            int y3;
            if (cursorBlinkIn) {
                if (bl3) {
                    y1 = topY - 1;
                    x2 = p + 1;
                    y3 = topY + 1;
                    Objects.requireNonNull(this.textRenderer);
                    DrawableHelper.fill(matrices, p, y1, x2, y3 + 9, -3092272);
                } else {
                    this.textRenderer.drawWithShadow(matrices, "_", (float) p, (float) topY, color);
                }
            }

            if (selectionEndLocation != selectionStartLocation) {
                int q = leftX + this.textRenderer.getWidth(string.substring(0, selectionEndLocation));
                y1 = topY - 1;
                x2 = q - 1;
                y3 = topY + 1;
                Objects.requireNonNull(this.textRenderer);
                this.drawSelectionHighlight(Rectangle.ofBounds(p, y1, x2, y3 + 9));
            }

        }
    }

    private void drawSelectionHighlight(Rectangle rectangle) {
        if (rectangle.getRight() > this.x + this.width) {
            rectangle = Rectangle.ofBounds(rectangle.getLeft(), rectangle.getTop(), this.x + this.width, rectangle.getBottom());
        }

        if (rectangle.getLeft() > this.x + this.width) {
            rectangle = Rectangle.ofBounds(this.x + this.width, rectangle.getTop(), rectangle.getRight(), rectangle.getBottom());
        }
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        RenderUtils.drawRectangle(rectangle, 0x9900bfff);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
            this.onChanged(this.text);
        }

    }

    public int getCursor() {
        return selection.start;
    }

    public void setCursor(int cursor) {
        this.setSelectionStart(cursor);
        if (!this.selecting) {
            this.setSelectionEnd(selection.start);
        }

        this.onChanged(this.text);
    }

    public void setDrawsBackground(boolean drawsBackground) {
        this.drawsBackground = drawsBackground;
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return this.visible && this.editable && super.changeFocus(lookForwards);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= (double) this.x && mouseX < (double) (this.x + this.width) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getInnerWidth() {
        return (this.drawsBackground ? this.width - 8 : this.width) - 6;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCharacterX(int index) {
        return index > this.text.length() ? this.x : this.x + this.textRenderer.getWidth(this.text.substring(0, index));
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setRenderer(TextFieldRenderer renderer) {
        this.renderer = renderer;
    }

    public void setSuggestion(Function<String, String> suggestion) {
        this.suggestion = suggestion;
    }

    @FunctionalInterface
    public interface TextFieldRenderer {

        int render(TextRenderer renderer, MatrixStack matrices, String text, String fullText, float x, float y, int color, boolean cursor);

    }

    private static record Selection(int start, int end) {

        public Selection {
        }

        public int length() {
            return end - start;
        }

        public Selection withEnd(int newEnd) {
            return new Selection(this.start, newEnd);
        }


        public Selection withStart(int newStart) {
            return new Selection(newStart, this.end);
        }

        public Selection ordered() {
            if (start > end) {
                return new Selection(end, start);
            }
            return this;
        }

    }

    // Way to retrieve height from dropdown menus / text fields adjusted by the dropdown etc.
    public int getFullHeight() {
        return height;
    }
}
