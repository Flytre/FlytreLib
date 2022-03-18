package net.flytre.flytre_lib.loader;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;

/**
 * Interface for block entities with screen handlers that need custom packet data sent to them to implement.
 */
public interface CustomScreenHandlerFactory extends NamedScreenHandlerFactory {

    void sendPacket(PacketByteBuf buf);
}
