package net.flytre.flytre_lib.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class InventoryUtils {
    public static boolean isInventoryFull(Inventory inv, Direction direction) {
        return getAvailableSlots(inv, direction).allMatch((i) -> {
            ItemStack itemStack = inv.getStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    public static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory) inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
    }

    public static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem()) {
            return false;
        } else if (first.getDamage() != second.getDamage()) {
            return false;
        } else if (first.getCount() > first.getMaxCount()) {
            return false;
        } else {
            return ItemStack.areTagsEqual(first, second);
        }
    }

    public static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        } else {
            return !(inventory instanceof SidedInventory) || ((SidedInventory) inventory).canInsert(slot, stack, side);
        }
    }

    public static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory) inv).canExtract(slot, stack, facing);
    }

    public static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return InventoryUtils.getAvailableSlots(inv, facing).allMatch((i) -> inv.getStack(i).isEmpty());
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos blockPos) {
        return getInventoryAt(world, (double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, double x, double y, double z) {
        Inventory inventory = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider) block).getInventory(blockState, world, blockPos);
        } else if (block.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof Inventory) {
                inventory = (Inventory) blockEntity;
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getInventory((ChestBlock) block, blockState, world, blockPos, true);
                }
            }
        }

        if (inventory == null) {
            List<Entity> list = world.getOtherEntities(null, new Box(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.VALID_INVENTORIES);
            if (!list.isEmpty()) {
                inventory = (Inventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return inventory;
    }

}
