package net.flytre.flytre_lib.api.config.reference;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VillagerProfessionReference extends Reference<VillagerProfession> {

    public VillagerProfessionReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public VillagerProfessionReference(@NotNull VillagerProfession value) {
        super(value, Registry.VILLAGER_PROFESSION);
    }


    public VillagerProfessionReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable VillagerProfession getValue(World world) {
        return getValue(Registry.VILLAGER_PROFESSION_KEY, world);
    }


}
