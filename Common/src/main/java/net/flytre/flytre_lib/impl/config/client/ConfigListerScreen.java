package net.flytre.flytre_lib.impl.config.client;

import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lists all the individual configs and provides buttons to open and edit each one
 */

@ApiStatus.Internal
public
class ConfigListerScreen extends GenericConfigScreen {
    private StringValueWidget<ClickableWidget> list;


    public ConfigListerScreen(@Nullable Screen parent) {
        super(parent, null);
    }

    public static String getName(DisplayName display, String baseName) {
        if (display != null)
            return display.translationKey() ? I18n.translate(display.value()) : display.value();
        String base = baseName.replaceAll("_", " ");
        return WordUtils.capitalize(base);
    }

    public void populate() {

        List<ConfigHandler<?>> handlers = Stream.of(ConfigRegistryImpl.getClientConfigs(), ConfigRegistryImpl.getServerConfigs())
                .flatMap(Collection::stream).toList();

        for (ConfigHandler<?> handler : handlers) {
            ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, Text.translatable("flytre_lib.gui.open"), (but) -> {
                MinecraftClient.getInstance().setScreen(GuiMaker.createGui(this, but, handler));
            });
            list.addEntry(getName(handler.getAssumed().getClass().getAnnotation(DisplayName.class), handler.getName()), button);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, Text.translatable("flytre_lib.gui.client_message"), width / 2, height - 60, 0xFFFFFFFF);
    }

    @Override
    public StringValueWidget<ClickableWidget> getList() {
        return list;
    }

    @Override
    protected void init() {
        list = new StringValueWidget<>(client, width, height, 60, height - 60, 30);
        populate();
        addDrawableChild(list);
    }
}
