package net.flytre.flytre_lib.api.base.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

/**
 * A fake world can be used to access a world when not in-game, i.e. the title screen
 * Useful for things like rendering entities, which usually need a world to be rendered
 */
public class FakeWorld extends ClientWorld {

    private static final DimensionType DIMENSION_TYPE = DimensionType.create(OptionalLong.empty(), true, false, false, true, 1, true, true, true, true, false, 0, 256, 256, new Identifier("null"), new Identifier("null"), 15f);
    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), MinecraftClient.getInstance().getSession().getProfile(), MinecraftClient.getInstance().createTelemetrySender());
    private static FakeWorld INSTANCE;

    private FakeWorld() {
        super(NETWORK_HANDLER, new Properties(Difficulty.EASY, false, true), World.OVERWORLD, DIMENSION_TYPE,
                0, 0, () -> null, null, false, 0L);
    }

    public static FakeWorld getInstance() {
        if (INSTANCE == null) //Lazy load
            INSTANCE = new FakeWorld();
        return INSTANCE;
    }
}
