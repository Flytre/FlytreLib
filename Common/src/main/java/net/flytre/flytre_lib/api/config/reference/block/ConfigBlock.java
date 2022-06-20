package net.flytre.flytre_lib.api.config.reference.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


sealed public interface ConfigBlock permits BlockReference, BlockTagReference {


    /**
     * Not to be used unless very rarely
     */
    static Set<Block> values(Set<ConfigBlock> blocks, World world) {
        Set<Block> result = new HashSet<>();
        for (ConfigBlock block : blocks) {
            if (block instanceof BlockReference) {
                result.add(((BlockReference) block).getValue(world));
            } else {
                Set<Block> list = ((BlockTagReference) block).getValue(world);
                if (list != null)
                    result.addAll(list);
            }
        }
        return result;
    }

    /**
     * To be used! O(1) search time for blocks not in tags, or O(n) for blocks in tags / not present
     */
    static boolean contains(Set<ConfigBlock> blocks, Block block, World world) {
        if (blocks.contains(new BlockReference(block)))
            return true;
        return blocks.stream().anyMatch(i -> {
            if (!(i instanceof BlockTagReference))
                return false;
            Set<Block> list = ((BlockTagReference) i).getValue(world);
            return list != null && list.contains(block);
        });
    }


    static Set<ConfigBlock> of(Set<Block> values) {
        return values.stream().map(BlockReference::new).collect(Collectors.toSet());
    }
}
