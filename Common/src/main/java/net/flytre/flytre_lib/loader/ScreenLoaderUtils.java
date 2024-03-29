package net.flytre.flytre_lib.loader;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Used to open a screen agnostic-ally
 */
public final class ScreenLoaderUtils {

    private static ScreenLoaderUtilsDelegate DELEGATE;

    private ScreenLoaderUtils() {
        throw new AssertionError();
    }

    public static void openScreen(ServerPlayerEntity player, CustomScreenHandlerFactory factory) {
        DELEGATE.open(player, factory);
    }

    static void setDelegate(ScreenLoaderUtilsDelegate delegate) {
        ScreenLoaderUtils.DELEGATE = delegate;
    }


    interface ScreenLoaderUtilsDelegate {
        void open(ServerPlayerEntity player, CustomScreenHandlerFactory factory);
    }


}
