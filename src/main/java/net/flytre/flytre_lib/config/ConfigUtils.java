package net.flytre.flytre_lib.config;

import net.flytre.flytre_lib.config.internal.client.GuiMaker;
import net.minecraft.client.gui.screen.Screen;


/**
 * Outward facing methods. Do not use the internal equivalents.
 */
public class ConfigUtils {


    public static Screen makeGui(Screen parent, ConfigHandler<?> handler) {
        return GuiMaker.makeGui(parent, handler);
    }
}
