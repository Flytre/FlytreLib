package net.flytre.flytre_lib.api.base.util;

import net.flytre.flytre_lib.mixin.base.MinecraftServerAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

public class FakeServerWorld extends ServerWorld {

    public static FakeServerWorld create(MinecraftServer server) {
        Executor executor = ((MinecraftServerAccessor)server).getWorkerExecutor();
        LevelStorage.Session session = ((MinecraftServerAccessor)server).getSession();
        UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(server.getSaveProperties(), server.getSaveProperties().getMainWorldProperties());
        GeneratorOptions generatorOptions = server.getSaveProperties().getGeneratorOptions();
        Registry<DimensionOptions> registry = generatorOptions.getDimensions();
        DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
        WorldGenerationProgressListener worldGenerationProgressListener = new WorldGenerationProgressListener() {
            @Override
            public void start(ChunkPos spawnPos) {

            }

            @Override
            public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {

            }

            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }
        };

        return new FakeServerWorld(server,executor,session,unmodifiableLevelProperties,dimensionOptions,worldGenerationProgressListener);

    }

    private FakeServerWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener) {
        super(server, workerExecutor, session, properties, World.OVERWORLD, dimensionOptions, worldGenerationProgressListener, false, 0, Collections.emptyList(), false);
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
    }

    @Override
    public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
    }

    @Override
    protected void tickBlockEntities() {
    }

    @Override
    public void save(@Nullable ProgressListener progressListener, boolean flush, boolean savingDisabled) {
    }

    @Override
    public void loadEntities(Stream<Entity> entities) {
    }

    @Override
    public boolean tryLoadEntity(Entity entity) {
        return true;
    }

    @Override
    public void addEntities(Stream<Entity> entities) {
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
    }
}
