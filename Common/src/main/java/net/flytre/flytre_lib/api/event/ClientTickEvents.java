package net.flytre.flytre_lib.api.event;

import net.flytre.flytre_lib.impl.event.EventImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;


public final class ClientTickEvents {


    public static final Event<ClientTicker> START_CLIENT_TICK = EventImpl.create();
    public static final Event<ClientTicker> END_CLIENT_TICK = EventImpl.create();
    public static final Event<WorldTicker> START_WORLD_TICK = EventImpl.create();
    public static final Event<WorldTicker> END_WORLD_TICK = EventImpl.create();

    @FunctionalInterface
    public interface ClientTicker {
        void onTickClient(MinecraftClient client);
    }

    @FunctionalInterface
    public interface WorldTicker {
        void onWorldTick(ClientWorld world);
    }
}
