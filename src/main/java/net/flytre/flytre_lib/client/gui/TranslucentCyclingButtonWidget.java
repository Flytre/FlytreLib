package net.flytre.flytre_lib.client.gui;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.client.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class TranslucentCyclingButtonWidget<T> extends PressableWidget implements OrderableTooltip {
    static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
    private static final List<Boolean> BOOLEAN_VALUES;

    static {
        BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    }

    private final Text optionText;
    private final Values<T> values;
    private final Function<T, Text> valueToText;
    private final Function<TranslucentCyclingButtonWidget<T>, MutableText> narrationMessageFactory;
    private final UpdateCallback<T> callback;
    private final TooltipFactory<T> tooltipFactory;
    private final boolean optionTextOmitted;
    private int index;
    private T value;
    private @Nullable BiFunction<T, Boolean, Integer> valueHoveredToColor;

    TranslucentCyclingButtonWidget(int x, int y, int width, int height, Text message, Text optionText, int index, T value, Values<T> values, Function<T, Text> valueToText, Function<TranslucentCyclingButtonWidget<T>, MutableText> narrationMessageFactory, TranslucentCyclingButtonWidget.UpdateCallback<T> callback, TranslucentCyclingButtonWidget.TooltipFactory<T> tooltipFactory, boolean optionTextOmitted) {
        super(x, y, width, height, message);
        this.optionText = optionText;
        this.index = index;
        this.value = value;
        this.values = values;
        this.valueToText = valueToText;
        this.narrationMessageFactory = narrationMessageFactory;
        this.callback = callback;
        this.tooltipFactory = tooltipFactory;
        this.optionTextOmitted = optionTextOmitted;
        this.valueHoveredToColor = (val, hovered) -> hovered ? 0x886b6b6b : 0x446b6b6b;
    }

    /**
     * Creates a new builder for a cycling button widget.
     */
    public static <T> TranslucentCyclingButtonWidget.Builder<T> builder(Function<T, Text> valueToText) {
        return new TranslucentCyclingButtonWidget.Builder<>(valueToText);
    }

    /**
     * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
     * and {@linkplain Boolean#FALSE} values. It displays
     * {@code on} for {@code true} and {@code off} for {@code false}.
     * Its current initial value is {@code true}.
     */
    public static TranslucentCyclingButtonWidget.Builder<Boolean> onOffBuilder(Text on, Text off) {
        return new TranslucentCyclingButtonWidget.Builder<Boolean>(value -> value ? on : off).values(BOOLEAN_VALUES);
    }

    /**
     * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
     * and {@linkplain Boolean#FALSE} values. It displays
     * {@link net.minecraft.client.gui.screen.ScreenTexts#ON} for {@code true} and
     * {@link net.minecraft.client.gui.screen.ScreenTexts#OFF} for {@code false}.
     * Its current initial value is {@code true}.
     */
    public static TranslucentCyclingButtonWidget.Builder<Boolean> onOffBuilder() {
        return (new TranslucentCyclingButtonWidget.Builder<Boolean>((value) -> value ? ScreenTexts.ON : ScreenTexts.OFF)).values(BOOLEAN_VALUES);
    }

    /**
     * Creates a builder for a cycling button widget that only has {@linkplain Boolean#TRUE}
     * and {@linkplain Boolean#FALSE} values. It displays
     * {@link net.minecraft.client.gui.screen.ScreenTexts#ON} for {@code true} and
     * {@link net.minecraft.client.gui.screen.ScreenTexts#OFF} for {@code false}.
     * Its current initial value is set to {@code initialValue}.
     */
    public static TranslucentCyclingButtonWidget.Builder<Boolean> onOffBuilder(boolean initialValue) {
        return onOffBuilder().initially(initialValue);
    }

    public void setValueHoveredToColor(@Nullable BiFunction<T, Boolean, Integer> valueHoveredToColor) {
        this.valueHoveredToColor = valueHoveredToColor;
    }

    public void onPress() {
        if (Screen.hasShiftDown()) {
            this.cycle(-1);
        } else {
            this.cycle(1);
        }

    }

    private void cycle(int amount) {
        List<T> list = this.values.getCurrent();
        this.index = MathHelper.floorMod(this.index + amount, list.size());
        T object = list.get(this.index);
        this.internalSetValue(object);
        this.callback.onValueChange(this, object);
    }

    private T getValue(int offset) {
        List<T> list = this.values.getCurrent();
        return list.get(MathHelper.floorMod(this.index + offset, list.size()));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 0.0D) {
            this.cycle(-1);
        } else if (amount < 0.0D) {
            this.cycle(1);
        }

        return true;
    }

    private void internalSetValue(T value) {
        Text text = this.composeText(value);
        this.setMessage(text);
        this.value = value;
    }

    private Text composeText(T value) {
        return this.optionTextOmitted ? this.valueToText.apply(value) : this.composeGenericOptionText(value);
    }

    private MutableText composeGenericOptionText(T value) {
        return ScreenTexts.composeGenericOptionText(this.optionText, this.valueToText.apply(value));
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        List<T> list = this.values.getCurrent();
        int i = list.indexOf(value);
        if (i != -1) {
            this.index = i;
        }

        this.internalSetValue(value);
    }

    protected MutableText getNarrationMessage() {
        return this.narrationMessageFactory.apply(this);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        if (this.active) {
            T object = this.getValue(1);
            Text text = this.composeText(object);
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.focused", text));
            } else {
                builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.hovered", text));
            }
        }

    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            MinecraftClient client = MinecraftClient.getInstance();
            int color = active ? valueHoveredToColor.apply(value, isHovered()) : 0x440a0a0a;
            RenderUtils.drawRect(x, y, x + width, y + height, color);
            drawCenteredText(matrixStack, client.textRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, 0xffffff);
        }
    }

    /**
     * {@return a generic narration message for this button}
     *
     * <p>If the button omits the option text in rendering, such as showing only
     * "Value", this narration message will still read out the option like
     * "Option: Value".
     */
    public MutableText getGenericNarrationMessage() {
        return getNarrationMessage(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage());
    }

    public List<OrderedText> getOrderedTooltip() {
        return this.tooltipFactory.apply(this.value);
    }

    @Environment(EnvType.CLIENT)
    private interface Values<T> {
        static <T> TranslucentCyclingButtonWidget.Values<T> of(List<T> values) {
            final List<T> list = ImmutableList.copyOf(values);
            return new TranslucentCyclingButtonWidget.Values<>() {
                public List<T> getCurrent() {
                    return list;
                }

                public List<T> getDefaults() {
                    return list;
                }
            };
        }

        static <T> TranslucentCyclingButtonWidget.Values<T> of(final BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            final List<T> list = ImmutableList.copyOf(defaults);
            final List<T> list2 = ImmutableList.copyOf(alternatives);
            return new TranslucentCyclingButtonWidget.Values<>() {
                public List<T> getCurrent() {
                    return alternativeToggle.getAsBoolean() ? list2 : list;
                }

                public List<T> getDefaults() {
                    return list;
                }
            };
        }

        List<T> getCurrent();

        List<T> getDefaults();
    }

    @Environment(EnvType.CLIENT)
    public interface UpdateCallback<T> {
        void onValueChange(TranslucentCyclingButtonWidget<T> button, T value);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface TooltipFactory<T> extends Function<T, List<OrderedText>> {
    }

    /**
     * A builder to easily create cycling button widgets.
     * <p>
     * Each builder must have at least one of its {@code values} methods called
     * with at least one default (non-alternative) value in the list before
     * building.
     *
     * @see TranslucentCyclingButtonWidget#builder(Function)
     */
    @Environment(EnvType.CLIENT)
    public static class Builder<T> {
        private final Function<T, Text> valueToText;
        private int initialIndex;
        @Nullable
        private T value;
        private TranslucentCyclingButtonWidget.TooltipFactory<T> tooltipFactory = (value) -> ImmutableList.of();
        private Function<TranslucentCyclingButtonWidget<T>, MutableText> narrationMessageFactory = TranslucentCyclingButtonWidget::getGenericNarrationMessage;
        private TranslucentCyclingButtonWidget.Values<T> values = TranslucentCyclingButtonWidget.Values.of(ImmutableList.of());
        private boolean optionTextOmitted;

        /**
         * Creates a builder.
         *
         * @see TranslucentCyclingButtonWidget#builder(Function)
         */
        public Builder(Function<T, Text> valueToText) {
            this.valueToText = valueToText;
        }

        /**
         * Sets the option values for this button.
         */
        public TranslucentCyclingButtonWidget.Builder<T> values(List<T> values) {
            this.values = TranslucentCyclingButtonWidget.Values.of(values);
            return this;
        }

        /**
         * Sets the option values for this button.
         */
        @SafeVarargs
        public final TranslucentCyclingButtonWidget.Builder<T> values(T... values) {
            return this.values(ImmutableList.copyOf(values));
        }

        /**
         * Sets the option values for this button.
         *
         * <p>When the user presses the ALT key, the {@code alternatives} values
         * will be iterated; otherwise the {@code defaults} values will be iterated
         * when clicking the built button.
         */
        public TranslucentCyclingButtonWidget.Builder<T> values(List<T> defaults, List<T> alternatives) {
            this.values = TranslucentCyclingButtonWidget.Values.of(TranslucentCyclingButtonWidget.HAS_ALT_DOWN, defaults, alternatives);
            return this;
        }

        /**
         * Sets the option values for this button.
         *
         * <p>When {@code alternativeToggle} {@linkplain BooleanSupplier#getAsBoolean()
         * getAsBoolean} returns {@code true}, the {@code alternatives} values
         * will be iterated; otherwise the {@code defaults} values will be iterated
         * when clicking the built button.
         */
        public TranslucentCyclingButtonWidget.Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            this.values = TranslucentCyclingButtonWidget.Values.of(alternativeToggle, defaults, alternatives);
            return this;
        }

        /**
         * Sets the tooltip factory that provides tooltips for any of the values.
         *
         * <p>If this is not called, the values simply won't have tooltips.
         */
        public TranslucentCyclingButtonWidget.Builder<T> tooltip(TranslucentCyclingButtonWidget.TooltipFactory<T> tooltipFactory) {
            this.tooltipFactory = tooltipFactory;
            return this;
        }

        /**
         * Sets the initial value of this button widget.
         *
         * <p>This is not effective if {@code value} is not in the default
         * values (i.e. excluding alternative values).
         *
         * <p>If this is not called, the initial value defaults to the first
         * value in the values list supplied.
         */
        public TranslucentCyclingButtonWidget.Builder<T> initially(T value) {
            this.value = value;
            int i = this.values.getDefaults().indexOf(value);
            if (i != -1) {
                this.initialIndex = i;
            }

            return this;
        }

        /**
         * Overrides the narration message of the button to build.
         *
         * <p>If this is not called, the button will use
         * {@link TranslucentCyclingButtonWidget#getGenericNarrationMessage()} for narration
         * messages.
         */
        public TranslucentCyclingButtonWidget.Builder<T> narration(Function<TranslucentCyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
            this.narrationMessageFactory = narrationMessageFactory;
            return this;
        }

        /**
         * Makes the built button omit the option and only display the current value
         * for its text, such as showing "Jump Mode" than "Mode: Jump Mode".
         */
        public TranslucentCyclingButtonWidget.Builder<T> omitKeyText() {
            this.optionTextOmitted = true;
            return this;
        }

        public TranslucentCyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
            return this.build(x, y, width, height, optionText, (button, value) -> {
            });
        }

        /**
         * Builds a cycling button widget.
         *
         * @throws IllegalStateException if no {@code values} call is made, or the
         *                               {@code values} has no default values available
         */
        public TranslucentCyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, TranslucentCyclingButtonWidget.UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            } else {
                T object = this.value != null ? this.value : list.get(this.initialIndex);
                Text text = (Text) this.valueToText.apply(object);
                Text text2 = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);
                return new TranslucentCyclingButtonWidget<>(x, y, width, height, text2, optionText, this.initialIndex, object, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
            }
        }
    }
}

