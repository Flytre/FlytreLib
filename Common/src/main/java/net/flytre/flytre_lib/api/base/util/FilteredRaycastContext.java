package net.flytre.flytre_lib.api.base.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.Set;

public class FilteredRaycastContext extends RaycastContext {

    private final Set<Block> filteredBlocks;

    public FilteredRaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, Entity entity, Set<Block> filteredBlocks) {
        super(start, end, shapeType, fluidHandling, entity);
        this.filteredBlocks = filteredBlocks;
    }

    @Override
    public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {

        if (filteredBlocks.contains(state.getBlock())) {
            return VoxelShapes.empty();
        }

        return super.getBlockShape(state, world, pos);
    }
}
