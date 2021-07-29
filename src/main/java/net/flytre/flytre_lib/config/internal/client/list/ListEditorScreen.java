package net.flytre.flytre_lib.config.internal.client.list;

import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.client.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.config.Button;
import net.flytre.flytre_lib.config.internal.client.GenericConfigScreen;
import net.flytre.flytre_lib.config.internal.client.GuiMaker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListEditorScreen extends GenericConfigScreen {

    private final Button buttonAnnotation;
    private final ButtonWidget reopen; //Basically references the button that created this screen, to recreate this screen with updated values


    private final Consumer<List<String>> saver;
    private final List<TranslucentTextField> initial;
    private final Supplier<TranslucentTextField> adder;
    private ListEditorWidget<TranslucentTextField> list;

    public ListEditorScreen(@Nullable Screen parent, Consumer<List<String>> saver, List<TranslucentTextField> initial, Supplier<TranslucentTextField> adder, @Nullable Button buttonAnnotation, @Nullable ButtonWidget reopen) {
        super(parent);
        this.saver = saver;
        this.initial = initial;
        this.adder = adder;
        this.buttonAnnotation = buttonAnnotation;
        this.reopen = reopen;
    }

    public void onClose() {
        super.onClose();
        saver.accept(list.children().stream().map(ListEditorWidget.ValueEntry::getTextField).map(TranslucentTextField::getText).filter(i -> i.length() != 0).collect(Collectors.toList()));
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

        TranslucentButton entryAdder = new TranslucentButton(width / 2 - width / 10 - offset, height - 30, width / 5, 20, new TranslatableText("flytre_lib.gui.add"), (button) -> {
            list.addEntry(adder.get());
        });
        addDrawableChild(entryAdder);
        TranslucentButton done = new TranslucentButton(width / 2 - width / 10 + offset, height - 30, width / 5, 20, new TranslatableText("flytre_lib.gui.done"), (button) -> {
            onClose();
        });
        addDrawableChild(done);



        if (customButtonFunc != null) {
            @Nullable TranslucentButton customButton = new TranslucentButton(width / 2 - width / 10, height - 30, width / 5, 20, new TranslatableText(buttonAnnotation.translationKey()), (x) -> {
                onClose();
                customButtonFunc.run();
                if (reopen != null)
                    reopen.onPress();
            });
            addDrawableChild(customButton);
        }

        super.init();
    }
}
