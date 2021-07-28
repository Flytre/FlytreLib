package net.flytre.flytre_lib.config.client;

import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.client.list.ConfigListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;


/**
 * An individual screen for a config, be it the main screen or a specific object
 */
public class IndividualConfigScreen<T> extends GenericConfigScreen {

    private final ConfigListWidget list;
    private final ConfigHandler<T> handler;


    public IndividualConfigScreen(@Nullable Screen parent, ConfigHandler<T> handler) {
        super(parent);
        this.handler = handler;
        Window window = MinecraftClient.getInstance().getWindow();
        this.list =  new ConfigListWidget(MinecraftClient.getInstance(), window.getScaledWidth(), window.getScaledHeight(), 60, window.getScaledHeight() - 60, 30);
    }

    public ConfigListWidget getList() {
        return list;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.list.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        handler.save(handler.getConfig());
        super.onClose();
    }

    public void addEntry(ConfigListWidget.ConfigEntry entry) {
        this.list.addConfigEntry(entry);
    }

    @Override
    protected void init() {
        addSelectableChild(list);
        super.init();
    }
}
