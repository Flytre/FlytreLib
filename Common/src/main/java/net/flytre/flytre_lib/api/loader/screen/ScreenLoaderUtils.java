package net.flytre.flytre_lib.api.loader.screen;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ScreenLoaderUtils {

    private static ScreenOpener DELEGATE;

    private ScreenLoaderUtils() {

    }

    public static void openScreen(ServerPlayerEntity player, CustomScreenHandlerFactory factory) {
        assert DELEGATE != null : LoaderProperties.ASSERTION_MESSAGE;
        DELEGATE.open(player, factory);
    }

    public static void setDelegate(ScreenOpener delegate) {
        ScreenLoaderUtils.DELEGATE = delegate;
    }


    public interface ScreenOpener {
        void open(ServerPlayerEntity player, CustomScreenHandlerFactory factory);
    }


}
