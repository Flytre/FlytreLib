package net.flytre.flytre_lib.api.base.util;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Large scale block operations
 */
public final class BlockManipulator {

    private BlockManipulator() {

    }


    /**
     * Spawn a structure at a position
     */
    public static void place(ServerWorld world, StructureTemplate structure, BlockPos pos) {
        place(world, structure, pos, 1.0f, BlockMirror.NONE, BlockRotation.NONE, false);
    }

    /**
     * Spawn a structure at a position
     */
    public static void place(ServerWorld world, StructureTemplate structure, BlockPos pos, float integrity, BlockMirror mirror, BlockRotation rotation, boolean ignoreEntities) {


        StructurePlacementData structurePlacementData = (new StructurePlacementData()).setMirror(mirror).setRotation(rotation).setIgnoreEntities(ignoreEntities);
        if (integrity < 1.0F) {
            structurePlacementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(integrity, 0.0F, 1.0F))).setRandom(Random.create());
        }

        structure.place(world, pos, pos, structurePlacementData, Random.create(), Block.NOTIFY_LISTENERS);
    }


    /**
     * Bounds are inclusive
     */
    public static Collection<BlockPos> getBlocksInRegion(BlockPos one, BlockPos two) {
        BlockPos min = new BlockPos(Math.min(one.getX(), two.getX()), Math.min(one.getY(), two.getY()), Math.min(one.getZ(), two.getZ()));
        BlockPos max = new BlockPos(Math.max(one.getX(), two.getX()), Math.max(one.getY(), two.getY()), Math.max(one.getZ(), two.getZ()));
        Set<BlockPos> result = new HashSet<>();
        for (int i = min.getX(); i <= max.getX(); i++) {
            for (int j = min.getY(); j <= max.getY(); j++) {
                for (int k = min.getZ(); k <= max.getZ(); k++) {
                    result.add(new BlockPos(i, j, k));
                }
            }
        }
        return result;
    }

    /**
     * Get the blocks in a region and do some operation on them
     */
    public static void forBlockInRegion(BlockPos one, BlockPos two, Consumer<BlockPos> actor) {
        getBlocksInRegion(one, two).forEach(actor);
    }

}
