package net.flytre.flytre_lib.config.client;

import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.flytre.flytre_lib.config.DisplayName;
import net.flytre.flytre_lib.config.client.list.StringValueWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lists all the individual configs and provides buttons to open and edit each one
 */
public class ConfigListerScreen extends GenericConfigScreen {
    private StringValueWidget<ClickableWidget> list;


    public ConfigListerScreen(@Nullable Screen parent) {
        super(parent);
    }

    public static String getName(DisplayName display, String baseName) {
        if (display != null)
            return display.translationKey() ? I18n.translate(display.value()) : display.value();
        String base = baseName.replaceAll("_", " ");
        return WordUtils.capitalize(base);
    }

    public void populate() {

        List<ConfigHandler<?>> handlers = Stream.of(ConfigRegistry.getClientConfigs(), ConfigRegistry.getServerConfigs())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (ConfigHandler<?> handler : handlers) {
            ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                MinecraftClient.getInstance().setScreen(GuiMaker.makeGui(this, handler));
            });
            list.addEntry(getName(handler.getAssumed().getClass().getAnnotation(DisplayName.class), handler.getName()), button);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, new TranslatableText("flytre_lib.gui.client_message"), width / 2, height - 60, 0xFFFFFFFF);
    }

    @Override
    protected void init() {
        list = new StringValueWidget<>(client, width, height, 60, height - 60, 30);
        populate();
        addDrawableChild(list);
    }
}
