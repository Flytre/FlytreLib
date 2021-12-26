package net.flytre.flytre_lib.impl.config;

import com.google.gson.JsonElement;
import net.flytre.flytre_lib.api.base.util.JsonNbtConverter;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ConfigS2CPacket implements Packet<ClientPlayPacketListener> {


    private final ConfigHandler<?> handler;
    private final NbtCompound nbt;

    public ConfigS2CPacket(ConfigHandler<?> handler) {
        this.handler = handler;
        this.nbt = (NbtCompound) JsonNbtConverter.toNbt(handler.getConfigAsJson());
    }

    public ConfigS2CPacket(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Optional<ConfigHandler<?>> optional = ConfigRegistryImpl.getServerConfigs().stream().filter(i -> i.getConfigId().equals(id)).findFirst();
        if (optional.isEmpty())
            throw new AssertionError("Config " + id + " not registered on client.");
        this.handler = optional.get();
        this.nbt = buf.readNbt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(handler.getConfigId());
        buf.writeNbt(nbt);
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        JsonElement json = JsonNbtConverter.toJson(nbt);
        handler.setConfig(json);
    }
}
