package net.flytre.flytre_lib.impl.config.client;

import net.flytre.flytre_lib.api.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * An individual screen for a config, be it the main screen or a specific object
 */

@ApiStatus.Internal
class IndividualConfigScreen<T> extends GenericConfigScreen {

    private final ConfigHandler<T> handler;
    private final List<ConfigListWidget.ConfigEntry> entries;
    private ConfigListWidget list;


    public IndividualConfigScreen(@Nullable Screen parent, @Nullable ButtonWidget reopen, ConfigHandler<T> handler) {
        super(parent, reopen);
        this.handler = handler;
        entries = new ArrayList<>();
    }

    public ConfigListWidget getList() {
        return list;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        handler.save(handler.getConfig());

        //Simulate reload since values have been changed
        if (handler.getConfig() instanceof ConfigEventAcceptor)
            ((ConfigEventAcceptor) handler.getConfig()).onReload();

        super.onClose();
    }

    public void addEntry(ConfigListWidget.ConfigEntry entry) {
        entries.add(entry);
    }


    public List<ConfigListWidget.ConfigEntry> getEntries() {
        return entries;
    }

    @Override
    protected void init() {
        list = new ConfigListWidget(client, width, height, 60, height - 60, 30);
        entries.forEach(i -> list.addConfigEntry(i));
        addDrawableChild(list);
        super.init();

        TranslucentButton fileOpener = new TranslucentButton(width / 2 - width / 10, height - 30, width / 5, 20, Text.translatable("flytre_lib.gui.open_file"), (button) -> {
            Path location = LoaderProperties.getModConfigDirectory();
            Path path = Paths.get(location.toString(), handler.getName() + ".json5");
            Util.getOperatingSystem().open(path.toFile().toURI());
        });
        TranslucentButton helpMac = new TranslucentButton(width / 2 - width / 5 - width / 5, height - 30, width / 5, 20, Text.translatable("flytre_lib.gui.help_mac"), (button) -> {
            try {
                Util.getOperatingSystem().open(new URI("https://support.apple.com/guide/mac-help/choose-an-app-to-open-a-file-on-mac-mh35597/mac"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        TranslucentButton helpWindows = new TranslucentButton(width / 2 + width / 5, height - 30, width / 5, 20, Text.translatable("flytre_lib.gui.help_windows"), (button) -> {
            try {
                Util.getOperatingSystem().open(new URI("https://support.microsoft.com/en-us/windows/change-default-programs-in-windows-10-e5d82cad-17d1-c53b-3505-f10a32e1894d"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        addDrawableChild(fileOpener);
        addDrawableChild(helpMac);
        addDrawableChild(helpWindows);
    }


}
