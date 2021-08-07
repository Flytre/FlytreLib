package net.flytre.flytre_lib.api.config.reference;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;


public class AdvancementReference extends Reference<Advancement> {

    public AdvancementReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public AdvancementReference(@NotNull Advancement value) {
        super(value.getId(), value);
    }


    public AdvancementReference(String namespace, String path) {
        super(namespace, path);
    }

    /**
     * The client advancement manager does not know all advancements, only ones visible to the client (i.e. gotten recipes and advancements)
     * Only to be used if the world is remote.
     */
    @Override
    protected @Nullable Advancement getValue(@Nullable RegistryKey<? extends Registry<? extends Advancement>> key, World world) {

        if (!world.isClient) {
            ServerAdvancementLoader loader = Objects.requireNonNull(world.getServer()).getAdvancementLoader();
            return loader.get(identifier);
        }
        return null;
    }

    public boolean hasAdvancement(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world, boolean valueIfNull) {
        Advancement advancement = getValue(null, world);
        if (advancement == null)
            return valueIfNull;
        PlayerAdvancementTracker manager = player.getAdvancementTracker();
        return manager.getProgress(advancement).isDone();
    }

    public Advancement getValue(World world) {
        return getValue(null, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<Advancement>> references) {
        return references.contains(this);
    }
}
