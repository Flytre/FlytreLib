package net.flytre.flytre_lib.loader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Used to create BlockEntityType builders, which requires an access widener.
 */

public interface BlockEntityFactory<T extends BlockEntity> {

    static <T extends BlockEntity> BlockEntityType.Builder<T> createBuilder(BlockEntityFactory<? extends T> factory, Block... blocks) {
        return BlockEntityType.Builder.create(factory::create, blocks);
    }

    T create(BlockPos pos, BlockState state);
}
