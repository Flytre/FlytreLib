package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.base.compat.wrench.WrenchItem;
import net.flytre.flytre_lib.base.compat.wrench.WrenchObservers;
import net.flytre.flytre_lib.loader.LoaderAgnosticRegistry;
import net.flytre.flytre_lib_test.item.ServoItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ItemRegistry {
    public static final Item SERVO = new ServoItem(new Item.Settings().group(Registry.TAB));
    public static final Item WRENCH = new WrenchItem(new Item.Settings().group(Registry.TAB).maxCount(1));

    private ItemRegistry() {
    }

    public static void init() {
        WrenchObservers.addUseOnBlockObserver(context -> {
            if (!context.getWorld().isClient && context.getPlayer() != null && context.getPlayer().isSneaking()) {
                BlockPos pos = context.getBlockPos();
                BlockEntity entity = context.getWorld().getBlockEntity(pos);

                if ((entity instanceof PipeEntity)) {
                    ((PipeEntity) entity).setRoundRobinMode(!((PipeEntity) entity).isRoundRobinMode());
                }

            }
        });
        WrenchObservers.addShiftTickObserver((World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity) -> {
            if (block instanceof PipeBlock) {
                BlockEntity entity = world.getBlockEntity(hitResult.getBlockPos());
                if (entity instanceof PipeEntity) {
                    boolean isRoundRobin = ((PipeEntity) entity).isRoundRobinMode();
                    player.sendMessage(new TranslatableText("item.pipe.wrench.2").append(": " + isRoundRobin), true);
                }
            }
        });
        WrenchObservers.addNoShiftTickObserver((World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity) -> {
            boolean wrenched;
            if (block instanceof PipeBlock && blockEntity instanceof PipeEntity) {
                wrenched = ((PipeEntity) blockEntity).wrenched.get(hitResult.getSide());

                player.sendMessage(new TranslatableText("item.pipe.wrench.1").append(" (" + hitResult.getSide().name() + "): " + wrenched), true);
            }
        });
        register(SERVO, "servo");
        register(WRENCH, "wrench");
    }

    private static <T extends Item> T register(T item, String id) {
        return LoaderAgnosticRegistry.register(item, Constants.MOD_ID, id);
    }

}
