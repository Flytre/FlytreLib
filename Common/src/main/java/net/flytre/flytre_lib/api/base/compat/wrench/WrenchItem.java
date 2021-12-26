package net.flytre.flytre_lib.api.base.compat.wrench;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WrenchItem extends Item {


    private static final List<Consumer<ItemUsageContext>> USE_ON_BLOCK_ACTIONS = new ArrayList<>();
    private static final List<WrenchTick> SHIFT_TICK = new ArrayList<>();
    private static final List<WrenchTick> NO_SHIFT_TICK = new ArrayList<>();

    public WrenchItem(Settings settings) {
        super(settings);
    }

    public static void addUseOnBlockAction(Consumer<ItemUsageContext> action) {
        USE_ON_BLOCK_ACTIONS.add(action);
    }

    public static void addShiftTickAction(WrenchTick ticker) {
        SHIFT_TICK.add(ticker);
    }

    public static void addNoShiftTickAction(WrenchTick ticker) {
        NO_SHIFT_TICK.add(ticker);
    }

    @Override
    public final ActionResult useOnBlock(ItemUsageContext context) {
        for (Consumer<ItemUsageContext> action : USE_ON_BLOCK_ACTIONS)
            action.accept(context);
        return ActionResult.SUCCESS;
    }


    @Override
    public final void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        def:
        {
            if (!selected || !(entity instanceof PlayerEntity) || world.isClient)
                break def;
            PlayerEntity player = (PlayerEntity) entity;
            BlockHitResult hitResult = Item.raycast(world, player, RaycastContext.FluidHandling.NONE);

            if (hitResult.getType() == HitResult.Type.MISS)
                break def;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            Block block = state.getBlock();

            if (player.isSneaking())
                for (WrenchTick shiftTick : SHIFT_TICK)
                    shiftTick.ticker(world, hitResult, block, player, state, blockEntity);
            else
                for (WrenchTick noShiftTick : NO_SHIFT_TICK)
                    noShiftTick.ticker(world, hitResult, block, player, state, blockEntity);

        }
        customTickCode(stack, world, entity, slot, selected);
    }

    protected void customTickCode(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

    }

}
