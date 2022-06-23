package net.flytre.flytre_lib.mixin.base;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {

    @Accessor("workerExecutor")
    Executor getWorkerExecutor();

    @Accessor("session")
    LevelStorage.Session getSession();
}
