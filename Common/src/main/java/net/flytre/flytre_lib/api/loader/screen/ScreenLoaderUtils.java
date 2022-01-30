package net.flytre.flytre_lib.api.loader.screen;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.OptionalInt;

public class ScreenLoaderUtils {

    private static ScreenOpener SCREEN_OPENER;

    public static void openScreen(ServerPlayerEntity player, CustomScreenHandlerFactory factory) {
        assert SCREEN_OPENER != null : LoaderProperties.ASSERTION_MESSAGE;
        SCREEN_OPENER.open(player, factory);
    }

    public static void setScreenOpener(ScreenOpener screenOpener) {
        SCREEN_OPENER = screenOpener;
    }

    @FunctionalInterface
    public interface ScreenOpener {
        void open(ServerPlayerEntity player, CustomScreenHandlerFactory factory);
    }


}
