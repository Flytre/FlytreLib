package net.flytre.flytre_lib.config.client;

import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.client.list.ConfigListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * An individual screen for a config, be it the main screen or a specific object
 */
public class IndividualConfigScreen<T> extends GenericConfigScreen {

    private ConfigListWidget list;
    private final ConfigHandler<T> handler;
    private final List<ConfigListWidget.ConfigEntry> entries;


    public IndividualConfigScreen(@Nullable Screen parent, ConfigHandler<T> handler) {
        super(parent);
        this.handler = handler;
        entries = new ArrayList<>();
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
        entries.add(entry);
    }

    @Override
    protected void init() {
        list = new ConfigListWidget(client, width, height, 60, height - 60, 30);
        entries.forEach(i -> list.addConfigEntry(i));
        addSelectableChild(list);
        super.init();
    }
}
