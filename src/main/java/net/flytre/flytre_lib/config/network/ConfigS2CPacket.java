package net.flytre.flytre_lib.config.network;

import com.google.gson.JsonElement;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.flytre_lib.common.util.JsonNbtConverter;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Optional;


/**
 * Remember, you can't send data to the client if the config handler is only visible on the server or the game will have no idea
 * how to deserialize the nbt into a config object.
 * <p>
 * <p>
 * This packet is sent to the client, syncing the server config into the client's server config
 */
public class ConfigS2CPacket {

    public static final Identifier PACKET_ID = new Identifier("flytre_lib", "config_sync");

    private final ConfigHandler<?> handler;

    public ConfigS2CPacket(ConfigHandler<?> handler) {
        this.handler = handler;
    }

    public static void apply(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Optional<ConfigHandler<?>> optional = ConfigRegistry.getServerConfigs().stream().filter(i -> i.getConfigId().equals(id)).findFirst();
        if (optional.isEmpty())
            throw new AssertionError("Config " + id + " not registered on client.");
        ConfigHandler<?> handler = optional.get();
        JsonElement json = JsonNbtConverter.toJson(buf.readNbt());
        handler.setConfig(json);
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(handler.getConfigId());
        packet.writeNbt((NbtCompound) JsonNbtConverter.toNbt(handler.getConfigAsJson()));
        return packet;
    }


    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, (client, handler, buf, sender) -> apply(buf));
    }
}
