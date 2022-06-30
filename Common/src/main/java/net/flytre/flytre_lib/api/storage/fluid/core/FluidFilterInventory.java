package net.flytre.flytre_lib.api.storage.fluid.core;


import net.flytre.flytre_lib.api.storage.inventory.IOType;
import net.flytre.flytre_lib.api.storage.inventory.filter.FilterSettings;
import net.flytre.flytre_lib.api.storage.inventory.filter.ResourceFilter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parallel to filter inventories, for fluids
 */

public class FluidFilterInventory implements FluidInventory, ResourceFilter<FluidStack>, FilterSettings {

    private final int height;
    public DefaultedList<FluidStack> fluids;
    private boolean matchNbt;
    private boolean matchMod;
    private int filterType;


    public FluidFilterInventory(DefaultedList<FluidStack> fluids, int filterType, int height, boolean matchNbt, boolean matchMod) {
        this.fluids = fluids;
        this.filterType = filterType;
        this.height = height;
        this.matchNbt = matchNbt;
        this.matchMod = matchMod;
    }

    public static FluidFilterInventory readNbt(NbtCompound tag, int defaultHeight) {
        int height = tag.contains("height") ? tag.getInt("height") : defaultHeight;
        int filterType = tag.getInt("type");
        boolean matchNbt = tag.getBoolean("nbtMatch");
        boolean matchMod = tag.getBoolean("modMatch");
        DefaultedList<FluidStack> fluids = DefaultedList.ofSize(height * 9, FluidStack.EMPTY);
        FluidInventory.readNbt(tag, fluids);
        return new FluidFilterInventory(fluids, filterType, height, matchNbt, matchMod);
    }

    /**
     * Will never be used, dummy hashmap
     *
     * @return dummy hashmap
     */
    @Override
    public Map<Direction, IOType> getFluidIO() {
        return new HashMap<>();
    }

    @Override
    public DefaultedList<FluidStack> getFluids() {
        return fluids;
    }

    @Override
    public long capacity() {
        return height * 9L;
    }

    @Override
    public long slotCapacity() {
        return 1;
    }

    @Override
    public void markDirty() {
        fixFluids();
    }


    private void fixFluids() {
        DefaultedList<FluidStack> fixed = DefaultedList.ofSize(this.fluids.size(), FluidStack.EMPTY);
        int index = 0;
        for (FluidStack stack : this.fluids) {
            if (!stack.isEmpty()) {
                fixed.set(index++, stack);
            }
        }
        this.fluids = fixed;
    }


    public Set<Fluid> getFilterFluids() {
        Set<Fluid> res = new HashSet<>();
        for (FluidStack item : fluids)
            res.add(item.getFluid());
        res.remove(Fluids.EMPTY);
        return res;
    }

    public boolean passFilterTest(FluidStack stack) {
        Set<Fluid> filterItems = getFilterFluids();

        if (matchMod) {
            String stackMod = Registry.FLUID.getId(stack.getFluid()).getNamespace();


            if (matchNbt) {
                return (filterType == 0) == fluids.stream().anyMatch(test -> stackMod.equals(Registry.FLUID.getId(test.getFluid()).getNamespace()) && FluidStack.areNbtEqual(test, stack));
            } else {
                return (filterType == 0) == filterItems.stream().anyMatch(test -> stackMod.equals(Registry.FLUID.getId(test).getNamespace()));
            }
        }

        boolean bl = (filterType == 0) == filterItems.contains(stack.getFluid());
        if (!matchNbt) {
            return bl;
        }

        //match nbt and item
        if (!bl)
            return false;

        Set<FluidStack> stacks = fluids.stream().filter(i -> !i.isEmpty()).collect(Collectors.toSet());
        for (FluidStack test : stacks) {
            if (test.getFluid() == stack.getFluid() && FluidStack.areNbtEqual(test, stack))
                return true;
        }

        return false;

    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public boolean isMatchNbt() {
        return matchNbt;
    }

    public void setMatchNbt(boolean matchNbt) {
        this.matchNbt = matchNbt;
    }

    public boolean isMatchMod() {
        return matchMod;
    }

    public void setMatchMod(boolean matchMod) {
        this.matchMod = matchMod;
    }

    public void toggleModMatch() {
        this.matchMod = !this.matchMod;
    }

    public void toggleFilterType() {
        this.filterType = (this.filterType == 1 ? 0 : 1);
    }

    public NbtCompound writeNbt() {
        NbtCompound tag = new NbtCompound();
        FluidInventory.writeNbt(tag, this.fluids);
        tag.putInt("type", this.filterType);
        tag.putInt("height", this.height);
        tag.putBoolean("nbtMatch", this.matchNbt);
        tag.putBoolean("modMatch", this.matchMod);
        return tag;
    }

    public void onOpen(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    public void onClose(PlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.PLAYERS, 1f, 1f);
        player.playSound(SoundEvents.BLOCK_METAL_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    @Override
    public boolean isEmpty() {
        return this.fluids.stream().allMatch(FluidStack::isEmpty);
    }

}
