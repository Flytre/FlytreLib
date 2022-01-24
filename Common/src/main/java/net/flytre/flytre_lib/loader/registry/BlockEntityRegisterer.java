package net.flytre.flytre_lib.loader.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public interface BlockEntityRegisterer {


    static <T extends BlockEntity> BlockEntityType.Builder<T> createBuilder(BlockEntityFactory<? extends T> factory, Block... blocks) {
        return BlockEntityType.Builder.create(factory::create, blocks);
    }

    <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id);

    interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
