package net.flytre.flytre_lib.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static List<ItemStack> getCombinedInventory(PlayerInventory inv) {
        List<ItemStack> result = new ArrayList<>(inv.main);
        result.addAll(inv.offHand);
        result.addAll(inv.armor);
        return result;
    }

    public static Map<Item, Integer> countInventoryContents(Inventory inventory) {
        Map<Item, Integer> result = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (result.containsKey(stack.getItem())) {
                result.put(stack.getItem(), stack.getCount() + result.get(stack.getItem()));
            } else
                result.put(stack.getItem(), stack.getCount());
        }
        return result;
    }

    public static ItemStack putStackInInventory(ItemStack stack, Inventory inventory) {
        return putStackInInventory(stack, inventory, 0, inventory.size());
    }

    /*
    MAX SLOT IS EXCLUSIVE, MIN IS INCLUSIVE
     */
    public static ItemStack putStackInInventory(ItemStack stack, Inventory inventory, int minSlot, int maxSlot) {
        for (int i = minSlot; i < maxSlot && !stack.isEmpty(); i++)
            stack = mergeStackIntoSlot(stack, inventory, i);
        return stack;
    }

    /*
    Merge a stack into an inventory and return the remainder
     */
    public static ItemStack mergeStackIntoSlot(ItemStack stack, Inventory inventory, int slot) {
        ItemStack itemStack = inventory.getStack(slot);
        boolean bl = false;

        if (itemStack.isEmpty()) {
            inventory.setStack(slot, stack);
            stack = ItemStack.EMPTY;
            bl = true;
        } else if (canMergeItems(itemStack, stack)) {
            int i = stack.getMaxCount() - itemStack.getCount();
            int j = Math.min(stack.getCount(), i);
            stack.decrement(j);
            itemStack.increment(j);
            bl = j > 0;
        }

        if (bl) {
            inventory.markDirty();
        }


        return stack;
    }
}
