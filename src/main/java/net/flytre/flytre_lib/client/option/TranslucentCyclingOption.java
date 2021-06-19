package net.flytre.flytre_lib.client.option;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.client.gui.TranslucentCyclingButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TranslucentCyclingOption<T> extends Option {


    private final TranslucentCyclingOption.Setter<T> setter;
    private final Function<GameOptions, T> getter;
    private final Supplier<TranslucentCyclingButtonWidget.Builder<T>> buttonBuilderFactory;
    private Function<MinecraftClient, TranslucentCyclingButtonWidget.TooltipFactory<T>> tooltips = (client) -> (value) -> ImmutableList.of();

    private TranslucentCyclingOption(String key, Function<GameOptions, T> getter, TranslucentCyclingOption.Setter<T> setter, Supplier<TranslucentCyclingButtonWidget.Builder<T>> buttonBuilderFactory) {
        super(key);
        this.getter = getter;
        this.setter = setter;
        this.buttonBuilderFactory = buttonBuilderFactory;
    }

    public static <T> TranslucentCyclingOption<T> create(String key, List<T> values, Function<T, Text> valueToText, Function<GameOptions, T> getter, TranslucentCyclingOption.Setter<T> setter) {
        return new TranslucentCyclingOption<>(key, getter, setter, () -> TranslucentCyclingButtonWidget.builder(valueToText).values(values));
    }

    public static <T> TranslucentCyclingOption<T> create(String key, Supplier<List<T>> valuesSupplier, Function<T, Text> valueToText, Function<GameOptions, T> getter, TranslucentCyclingOption.Setter<T> setter) {
        return new TranslucentCyclingOption<>(key, getter, setter, () -> TranslucentCyclingButtonWidget.builder(valueToText).values(valuesSupplier.get()));
    }

    public static <T> TranslucentCyclingOption<T> create(String key, List<T> defaults, List<T> alternatives, BooleanSupplier alternativeToggle, Function<T, Text> valueToText, Function<GameOptions, T> getter, TranslucentCyclingOption.Setter<T> setter) {
        return new TranslucentCyclingOption<>(key, getter, setter, () -> TranslucentCyclingButtonWidget.builder(valueToText).values(alternativeToggle, defaults, alternatives));
    }

    public static <T> TranslucentCyclingOption<T> create(String key, T[] values, Function<T, Text> valueToText, Function<GameOptions, T> getter, TranslucentCyclingOption.Setter<T> setter) {
        return new TranslucentCyclingOption<>(key, getter, setter, () -> TranslucentCyclingButtonWidget.builder(valueToText).values(values));
    }

    public static TranslucentCyclingOption<Boolean> create(String key, Text on, Text off, Function<GameOptions, Boolean> getter, TranslucentCyclingOption.Setter<Boolean> setter) {
        return new TranslucentCyclingOption<>(key, getter, setter, () -> TranslucentCyclingButtonWidget.onOffBuilder(on, off));
    }

    public static TranslucentCyclingOption<Boolean> create(String key, Function<GameOptions, Boolean> getter, TranslucentCyclingOption.Setter<Boolean> setter) {
        return new TranslucentCyclingOption<Boolean>(key, getter, setter, TranslucentCyclingButtonWidget::onOffBuilder);
    }

    public static TranslucentCyclingOption<Boolean> create(String key, Text tooltip, Function<GameOptions, Boolean> getter, TranslucentCyclingOption.Setter<Boolean> setter) {
        return create(key, getter, setter).tooltip((client) -> {
            List<OrderedText> list = client.textRenderer.wrapLines(tooltip, 200);
            return (value) -> list;
        });\
    }

    public TranslucentCyclingOption<T> tooltip(Function<MinecraftClient, TranslucentCyclingButtonWidget.TooltipFactory<T>> tooltips) {
        this.tooltips = tooltips;
        return this;
    }

    public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
        TranslucentCyclingButtonWidget.TooltipFactory<T> tooltipFactory = this.tooltips.apply(MinecraftClient.getInstance());
        return this.buttonBuilderFactory.get().tooltip(tooltipFactory).initially(this.getter.apply(options)).build(x, y, width, 20, this.getDisplayPrefix(), (button, value) -> {
            this.setter.accept(options, this, value);
            options.write();
        });
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface Setter<T> {
        void accept(GameOptions gameOptions, Option option, T value);
    }
}