package net.flytre.flytre_lib.impl.config.client;

import net.flytre.flytre_lib.api.config.annotation.Button;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.flytre.flytre_lib.api.gui.button.TranslucentCyclingButtonWidget;
import net.flytre.flytre_lib.api.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.mixin.config.SliderWidgetAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@ApiStatus.Internal
class ListEditorScreen<T extends ClickableWidget> extends GenericConfigScreen {

    private final Button buttonAnnotation;

    private final Consumer<List<Object>> saver;
    private final List<T> initial;
    private final Supplier<T> adder;
    private ListEditorWidget<T> list;

    public ListEditorScreen(@Nullable Screen parent, @Nullable ButtonWidget reopen, Consumer<List<Object>> saver, List<T> initial, Supplier<T> adder, @Nullable Button buttonAnnotation) {
        super(parent, reopen);
        this.saver = saver;
        this.initial = initial;
        this.adder = adder;
        this.buttonAnnotation = buttonAnnotation;
    }

    public void onClose() {
        super.onClose();
        saver.accept(list.children()
                .stream()
                .map(ListEditorWidget.ValueEntry::getClickable)
                .map(this::toValue)
                .filter(i -> !(i instanceof String) || ((String) i).length() != 0)
                .collect(Collectors.toList()));
    }

    @Override
    public ConfigStyleList<?> getList() {
        return list;
    }

    private Object toValue(ClickableWidget clickable) {
        if (clickable instanceof TranslucentTextField)
            return ((TranslucentTextField) clickable).getText();
        if (clickable instanceof SliderWidget)
            return ((SliderWidgetAccessor) clickable).getValue();
        if (clickable instanceof TranslucentCyclingButtonWidget)
            return ((TranslucentCyclingButtonWidget<?>) clickable).getValue();
        throw new ConfigError("Unknown type: " + clickable.getType());
    }

    @Override
    protected void init() {
        list = new ListEditorWidget<>(client, width, height, 60, height - 60, 30);
        initial.forEach(i -> list.addEntry(i));
        if (list.children().isEmpty())
            list.addEntry(adder.get());
        addDrawableChild(list);

        Runnable customButtonFunc = GuiMaker.getRunnable(buttonAnnotation);

        int offset = width / 8 + (customButtonFunc == null ? 0 : width / 5);

        TranslucentButton entryAdder = new TranslucentButton(width / 2 - width / 10 - offset, height - 30, width / 5, 20, Text.translatable("flytre_lib.gui.add"), (button) -> {
            list.addEntry(adder.get());
        });

        addDrawableChild(entryAdder);

        TranslucentButton done = new TranslucentButton(width / 2 - width / 10 + offset, height - 30, width / 5, 20, Text.translatable("flytre_lib.gui.done"), (button) -> {
            onClose();
        });
        addDrawableChild(done);


        if (customButtonFunc != null) {
            @Nullable TranslucentButton customButton = new TranslucentButton(width / 2 - width / 10, height - 30, width / 5, 20, Text.translatable(buttonAnnotation.translationKey()), (x) -> {
                onClose();
                customButtonFunc.run();
                reopenAction();
            });
            addDrawableChild(customButton);
        }

        super.init();
    }
}
