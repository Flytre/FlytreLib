package net.flytre.flytre_lib.config.internal.client.list;

import com.google.gson.reflect.TypeToken;
import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.common.util.reflection.FieldMatch;
import net.flytre.flytre_lib.config.Button;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.internal.client.GenericConfigScreen;
import net.flytre.flytre_lib.config.internal.client.GuiMaker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class MapEditorScreen extends GenericConfigScreen {

    private final Button buttonAnnotation;
    private final ButtonWidget reopen; //Basically references the button that created this screen, to recreate this screen with updated values

    private Map<String, ClickableWidget> values;
    private StringValueWidget<ClickableWidget> list;
    private Runnable save;

    /**
     * if the values are objects and already saved, no need to save them again. However if entries are not auto saved, save em this way
     */
    public MapEditorScreen(@Nullable Screen parent, @Nullable Button buttonAnnotation, @Nullable ButtonWidget reopen) {
        super(parent);
        this.buttonAnnotation = buttonAnnotation;
        this.reopen = reopen;
    }


    /**
     * Basically, if a map contains JsonElements rather than objects (which are incompatible with makeGuiHelper()), the map's values are wrapped in objects and processed that way,
     * Then, on save, the ObjectWrappers' values are saved to the original config map
     */
    public void setWrappedElements(Map<String, GuiMaker.ObjectWrapper<?>> wrappedElements, FieldMatch rawValueField, ConfigHandler<?> handler, Object currentObject) {
        save = () -> {
            Map<String, Object> processed = new HashMap<>();
            wrappedElements.forEach((key, value) -> processed.put(key, value.value));
            ParameterizedType parameterizedType = (ParameterizedType) rawValueField.field().getGenericType();
            TypeToken<?> token = TypeToken.get(parameterizedType);
            try {
                rawValueField.field().setAccessible(true);
                rawValueField.field().set(currentObject, handler.getGson().fromJson(handler.getGson().toJson(processed), token.getType()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
    }

    public void setValues(Map<String, ClickableWidget> values) {
        this.values = values;
    }

    public void onClose() {
        super.onClose();


        if (save != null) {
            //save wrapped elements
            save.run();
        }
    }

    @Override
    protected void init() {
        list = new StringValueWidget<>(client, width, height, 60, height - 60, 30);

        values.forEach((key, value) -> list.addEntry(key, value));
        addDrawableChild(list);


        Runnable customButtonFunc = GuiMaker.getRunnable(buttonAnnotation);

        int doneOffset = customButtonFunc == null ? 0 : width / 8;

        TranslucentButton done = new TranslucentButton(width / 2 - width / 10 + doneOffset, height - 30, width / 5, 20, new TranslatableText("flytre_lib.gui.done"), (button) -> {
            onClose();
        });
        addDrawableChild(done);

        if (customButtonFunc != null) {
            @Nullable TranslucentButton customButton = new TranslucentButton(width / 2 - width / 10 - width / 8, height - 30, width / 5, 20, new TranslatableText(buttonAnnotation.translationKey()), (x) -> {
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
