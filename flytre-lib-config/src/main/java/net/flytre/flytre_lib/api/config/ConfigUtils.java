package net.flytre.flytre_lib.api.config;

import net.flytre.flytre_lib.impl.config.client.GuiMaker;
import net.minecraft.client.gui.screen.Screen;


/**
 * Outward facing methods. Do not use the internal equivalents.
 */
public class ConfigUtils {
    public static Screen makeGui(Screen parent, ConfigHandler<?> handler) {
        return GuiMaker.makeGui(parent, handler);
    }
}
