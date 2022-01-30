package net.flytre.flytre_lib.api.loader.screen;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface CustomScreenHandlerFactory extends NamedScreenHandlerFactory {

    void sendPacket(PacketByteBuf buf);
}
