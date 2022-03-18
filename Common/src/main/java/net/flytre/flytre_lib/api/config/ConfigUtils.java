package net.flytre.flytre_lib.api.config;

import net.flytre.flytre_lib.impl.config.client.GuiMaker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;


/**
 * Outward facing configuration utility methods. Do not use the internal equivalents.
 */
public final class ConfigUtils {
    private ConfigUtils() {
    }

    /**
     * @param parent  The screen that opens this GUI
     * @param reopen  The button that opens this GUI
     * @param handler The config handler of the GUI
     * @return the config screen
     */
    public static Screen createGui(Screen parent, @Nullable ButtonWidget reopen, ConfigHandler<?> handler) {
        return GuiMaker.createGui(parent, reopen, handler);
    }
}
