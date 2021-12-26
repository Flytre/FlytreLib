package net.flytre.flytre_lib.api.storage.inventory;

import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

/**
 * Implemented inventory to prevent errors when creating your custom containers!
 * Highly recommended over default inventory unless you need absolutely crazy
 * behavior!
 */
public interface EasyInventory extends SidedInventory, IOTypeProvider {


    DefaultedList<ItemStack> getItems();

    Map<Direction, IOType> getItemIO();

    default Map<Direction, IOType> getIOType() {
        return getItemIO();
    }

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(getItems(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    default ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(getItems(), slot);
        markDirty();
        return stack;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default void clear() {
        getItems().clear();
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, size()).toArray();
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack) && getItemIO().get(dir).canInsert();
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return getItemIO().get(dir).canExtract();
    }

    default ItemStack addStack(ItemStack stack, Direction dir) {
        return customAddStack(stack, getAvailableSlots(dir), (i, stk) -> canExtract(i, stk, dir));
    }

    default ItemStack addStackInternal(ItemStack stack) {
        return customAddStack(stack, IntStream.range(0, size()).toArray(), this::isValid);
    }


    default ItemStack customAddStack(ItemStack stack, int[] slots, BiPredicate<Integer, ItemStack> condition) {
        for (int i : slots) {
            stack = InventoryUtils.mergeStackIntoSlot(stack, this, i);
            if (stack.isEmpty())
                break;
        }
        return stack;
    }

}
