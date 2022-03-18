package net.flytre.flytre_lib.api.base.util;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class InventoryUtils {
    private InventoryUtils() {
        throw new AssertionError();
    }

    public static boolean isInventoryFull(Inventory inv, Direction direction) {
        return getAvailableSlots(inv, direction).allMatch((i) -> {
            ItemStack itemStack = inv.getStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    public static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory) inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
    }

    /**
     * Whether two stacks could be merged at all, i.e. 33 stone and 34 stone into 64 stone and 3 stone
     */
    public static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (first.getItem() != second.getItem())
            return false;

        if (first.getDamage() != second.getDamage())
            return false;

        if (first.getCount() > first.getMaxCount())
            return false;

        return ItemStack.areNbtEqual(first, second);
    }

    /**
     * Whether two stacks  can be fully combined into one stack
     */
    public static boolean canUnifyStacks(ItemStack first, ItemStack second) {
        return canMergeItems(first, second) && first.getCount() + second.getCount() <= first.getMaxCount();
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
        } else if (block instanceof BlockEntityProvider) {
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

    /**
     * Get the full contents of the player inventory as a list
     */
    public static List<ItemStack> getCombinedInventory(PlayerInventory inv) {
        List<ItemStack> result = new ArrayList<>(inv.main);
        result.addAll(inv.offHand);
        result.addAll(inv.armor);
        return result;
    }

    /**
     * Count the number of each type of item in an inventory
     */
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

    /**
     * Max slot is exclusive, min is inclusive
     */
    public static ItemStack putStackInInventory(ItemStack stack, Inventory inventory, int minSlot, int maxSlot) {
        for (int i = minSlot; i < maxSlot && !stack.isEmpty(); i++)
            stack = mergeStackIntoSlot(stack, inventory, i);
        return stack;
    }

    /**
     * Merge a stack into an inventory and return the remainder
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

    public static @Nullable ItemStack getHoldingStack(LivingEntity entity, Predicate<ItemStack> condition) {
        ItemStack stack = entity.getOffHandStack();
        if (!condition.test(stack))
            stack = entity.getMainHandStack();

        if (!condition.test(stack))
            return null;
        return stack;
    }

    /**
     * Write inventory nbt to a subtag which is not Inventory
     */
    public static NbtCompound writeNbt(NbtCompound tag, DefaultedList<ItemStack> stacks, String key) {
        NbtCompound temp = new NbtCompound();
        Inventories.writeNbt(temp, stacks);
        tag.put(key, temp.get("Items"));
        return tag;
    }

    /**
     * Read inventory nbt from a subtag which is not Inventory
     */
    public static NbtCompound readNbt(NbtCompound tag, DefaultedList<ItemStack> stacks, String key) {
        NbtCompound temp = new NbtCompound();

        if (tag.get(key) == null)
            return tag;

        temp.put("Items", tag.get(key));
        Inventories.readNbt(temp, stacks);
        return tag;
    }


    public static int getFirstEmptySlot(DefaultedList<ItemStack> stacks) {
        return getFirstEmptySlot(stacks, 0, stacks.size());
    }

    /**
     * @param stacks the inventory contents
     * @param min    inclusive first index to search at
     * @param max    exclusive last index
     * @return the first empty slot in an inventory
     */
    public static int getFirstEmptySlot(DefaultedList<ItemStack> stacks, int min, int max) {
        int index = -1;
        for (int i = min; i < max; i++) {
            if (stacks.get(i).isEmpty()) {
                index = i;
                break;
            }
        }
        return index;
    }
}
