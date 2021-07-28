package net.flytre.flytre_lib.config.client;

import net.flytre.flytre_lib.client.gui.TranslucentButton;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.flytre.flytre_lib.config.client.list.StringValueWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

/**
 * Lists all the individual configs
 */
public class ConfigListerScreen extends GenericConfigScreen {
    private StringValueWidget<ClickableWidget> list;


    public ConfigListerScreen(@Nullable Screen parent) {
        super(parent);
    }

    public void populate() {
        for (ConfigHandler<?> handler : ConfigRegistry.getClientConfigs()) {
            ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                MinecraftClient.getInstance().setScreen(GuiMaker.makeGui(this, handler));
            });
            list.addEntry(handler.getName(), button);
        }

        for (ConfigHandler<?> handler : ConfigRegistry.getServerConfigs()) {
            Screen screen = GuiMaker.makeGui(this, handler);
            ClickableWidget button = new TranslucentButton(0, 0, Math.min(250, width), 20, new TranslatableText("flytre_lib.gui.open"), (but) -> {
                MinecraftClient.getInstance().setScreen(screen);
            });
            list.addEntry(handler.getName(), button);
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
