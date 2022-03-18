package net.flytre.flytre_lib.api.base.compat.wrench;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class WrenchObservers {

    private static final List<Consumer<ItemUsageContext>> USE_ON_BLOCK_OBSERVERS = new ArrayList<>();
    private static final List<TickAction> SHIFT_TICK_OBSERVERS = new ArrayList<>();
    private static final List<TickAction> NO_SHIFT_TICK_OBSERVERS = new ArrayList<>();

    private WrenchObservers() {

    }

    public static void addUseOnBlockObserver(Consumer<ItemUsageContext> observer) {
        USE_ON_BLOCK_OBSERVERS.add(observer);
    }

    public static void addShiftTickObserver(TickAction observer) {
        SHIFT_TICK_OBSERVERS.add(observer);
    }

    public static void addNoShiftTickObserver(TickAction observer) {
        NO_SHIFT_TICK_OBSERVERS.add(observer);
    }


    public static void onUseOnBlock(ItemUsageContext context) {
        USE_ON_BLOCK_OBSERVERS.forEach(observer -> observer.accept(context));
    }

    public static void onShiftTick(World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity) {
        SHIFT_TICK_OBSERVERS.forEach(observer -> observer.onTick(world, hitResult, block, player, state, blockEntity));
    }

    public static void onShiftlessTick(World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity) {
        NO_SHIFT_TICK_OBSERVERS.forEach(observer -> observer.onTick(world, hitResult, block, player, state, blockEntity));
    }

    @FunctionalInterface
    public interface TickAction {

        void onTick(World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity);

    }

}
