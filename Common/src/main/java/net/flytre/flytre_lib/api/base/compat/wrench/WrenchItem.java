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

public class WrenchItem extends Item {

    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public final ActionResult useOnBlock(ItemUsageContext context) {
        WrenchObservers.onUseOnBlock(context);
        return ActionResult.SUCCESS;
    }


    @Override
    public final void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        tickActions:
        {
            if (!selected || !(entity instanceof PlayerEntity player) || world.isClient)
                break tickActions;
            BlockHitResult hitResult = Item.raycast(world, player, RaycastContext.FluidHandling.NONE);

            if (hitResult.getType() == HitResult.Type.MISS)
                break tickActions;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            Block block = state.getBlock();

            if (player.isSneaking())
                WrenchObservers.onShiftTick(world, hitResult, block, player, state, blockEntity);
            else
                WrenchObservers.onShiftlessTick(world, hitResult, block, player, state, blockEntity);


        }
        customTickCode(stack, world, entity, slot, selected);
    }

    protected void customTickCode(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

    }

}
