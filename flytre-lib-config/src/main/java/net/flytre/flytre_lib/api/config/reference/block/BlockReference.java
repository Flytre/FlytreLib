package net.flytre.flytre_lib.api.config.reference.block;

import net.flytre.flytre_lib.api.config.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class BlockReference extends Reference<Block> implements ConfigBlock {

    public BlockReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public BlockReference(@NotNull Block value) {
        super(value, Registry.BLOCK);
    }


    public BlockReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable Block getValue(World world) {
        return getValue(Registry.BLOCK_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<Block>> references) {
        return references.contains(this);
    }
}
