package net.flytre.flytre_lib.loader;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkHooks;

final class ScreenLoaderUtilsImpl implements ScreenLoaderUtils.ScreenLoaderUtilsDelegate {

    private ScreenLoaderUtilsImpl() {

    }

    public static void init() {
        ScreenLoaderUtils.setDelegate(new ScreenLoaderUtilsImpl());
    }

    @Override
    public void open(ServerPlayerEntity player, CustomScreenHandlerFactory factory) {
        NetworkHooks.openGui(player, factory, factory::sendPacket);
    }
}
