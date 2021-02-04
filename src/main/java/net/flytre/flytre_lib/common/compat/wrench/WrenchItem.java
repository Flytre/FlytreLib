package net.flytre.flytre_lib.common.compat.wrench;

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


    public static List<Consumer<ItemUsageContext>> USE_ON_BLOCK_ACTIONS = new ArrayList<>();
    public static List<WrenchTick> SHIFT_TICK = new ArrayList<>();
    public static List<WrenchTick> NO_SHIFT_TICK = new ArrayList<>();


    public WrenchItem(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        for (Consumer<ItemUsageContext> action : USE_ON_BLOCK_ACTIONS) {
            action.accept(context);
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected || !(entity instanceof PlayerEntity) || world.isClient)
            return;
        PlayerEntity player = (PlayerEntity) entity;
        BlockHitResult hitResult = Item.raycast(world, player, RaycastContext.FluidHandling.NONE);

        if (hitResult.getType() == HitResult.Type.MISS)
            return;

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

}
