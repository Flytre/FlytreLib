package net.flytre.flytre_lib.api.base.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

/**
 * A fake world can be used to access a world when not in-game, i.e. the title screen
 * Useful for things like rendering entities, which usually need a world to be rendered
 */
public class FakeWorld extends ClientWorld {

    private static final DimensionType DIMENSION_TYPE = new DimensionType(OptionalLong.empty(),true,false,false,true,1,true,true,0,256,256,TagKey.of(Registry.BLOCK_KEY, new Identifier("null")),new Identifier("null"),15f, new DimensionType.MonsterSettings(false,false, UniformIntProvider.create(1,100),1));
    private static final RegistryEntry.Direct<DimensionType> DIMENSION_TYPE_KEY = new RegistryEntry.Direct<>(DIMENSION_TYPE);

    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), MinecraftClient.getInstance().getSession().getProfile(), MinecraftClient.getInstance().createTelemetrySender());
    private static FakeWorld INSTANCE;

    private FakeWorld() {
        super(NETWORK_HANDLER, new Properties(Difficulty.EASY, false, true), World.OVERWORLD, DIMENSION_TYPE_KEY,
                0, 0, () -> null, null, false, 0L);
    }

    public static FakeWorld getInstance() {
        if (INSTANCE == null) //Lazy load
            INSTANCE = new FakeWorld();
        return INSTANCE;
    }
}
