package net.flytre.flytre_lib.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

public class FakeWorld extends ClientWorld {

    private static final DimensionType DIMENSION_TYPE = DimensionType.create(OptionalLong.empty(), true, false, false, true, 1, true, true, true, true, false, 0, 256, 256, null, new Identifier("null"), new Identifier("null"), 15f);
    private static FakeWorld instance;

    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MinecraftClient.getInstance(),null, new ClientConnection(NetworkSide.CLIENTBOUND), MinecraftClient.getInstance().getSession().getProfile());

    private FakeWorld() {
        super(NETWORK_HANDLER, new Properties(Difficulty.EASY, false, true), null, DIMENSION_TYPE,
                0, () -> null, null, false, 0L);
    }

    public static FakeWorld getInstance() {
        if (instance == null) instance = new FakeWorld();
        return instance;
    }
}
