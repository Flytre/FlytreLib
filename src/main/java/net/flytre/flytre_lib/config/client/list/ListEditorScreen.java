package net.flytre.flytre_lib.config.client.list;

import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.client.gui.text_field.TranslucentTextField;
import net.flytre.flytre_lib.config.client.GenericConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListEditorScreen extends GenericConfigScreen {

    private final Consumer<List<String>> saver;
    private final List<TranslucentTextField> initial;
    private final Supplier<TranslucentTextField> adder;
    private ListEditorWidget<TranslucentTextField> list;
    private TranslucentButton entryAdder;
    private TranslucentButton done;

    public ListEditorScreen(@Nullable Screen parent, Consumer<List<String>> saver, List<TranslucentTextField> initial, Supplier<TranslucentTextField> adder) {
        super(parent);
        this.saver = saver;
        this.initial = initial;
        this.adder = adder;
    }

    public void onClose() {
        super.onClose();
        saver.accept(list.children().stream().map(ListEditorWidget.ValueEntry::getTextField).map(TranslucentTextField::getText).filter(i -> i.length() != 0).collect(Collectors.toList()));
    }


    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.list.render(matrices, mouseX, mouseY, delta);
        entryAdder.render(matrices, mouseX, mouseY, delta);
        done.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        list = new ListEditorWidget<>(client, width, height, 60, height - 60, 30);
        initial.forEach(i -> list.addEntry(i));
        if(list.children().isEmpty())
            list.addEntry(adder.get());
        addSelectableChild(list);
        entryAdder = new TranslucentButton(width / 2 - width / 10 - width / 8, height - 30, width / 5, 20, new TranslatableText("flytre_lib.gui.add"), (button) -> {
            list.addEntry(adder.get());
        });
        addDrawableChild(entryAdder);
        done = new TranslucentButton(width / 2 - width / 10 + width / 8, height - 30, width / 5, 20, new TranslatableText("flytre_lib.gui.done"), (button) -> {
            onClose();
        });
        addDrawableChild(done);

        super.init();
    }
}
