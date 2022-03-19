package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.api.base.compat.wrench.WrenchItem;
import net.flytre.flytre_lib.loader.CustomScreenHandlerFactory;
import net.flytre.flytre_lib.loader.ScreenLoaderUtils;
import net.flytre.flytre_lib.api.storage.connectable.ItemPipeConnectable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * Blocks represent each type of block added to Minecraft. There is only ONE block object for each type of block, i.e. one
 * object represents sand (Blocks.SAND), one for dirt, one for oak logs, one for diamond ore, etc. Block object classes store
 * data about specific categories of blocks. For example, a torch can be lit or extinguished, a door can be open or closed, a log
 * can be placed 3 different ways so that the bark is on different sides, etc. Each individual block in the game is represented as
 * a BlockState, which stores the Block along with its current STATE (i.e. a lever facing along the X axis in the OFF position).
 */
public class PipeBlock extends BlockWithEntity implements ItemPipeConnectable {
    public static final EnumProperty<PipeSide> UP;
    public static final EnumProperty<PipeSide> DOWN;
    public static final EnumProperty<PipeSide> NORTH;
    public static final EnumProperty<PipeSide> SOUTH;
    public static final EnumProperty<PipeSide> EAST;
    public static final EnumProperty<PipeSide> WEST;

    private static final VoxelShape NODE;
    private static final VoxelShape C_UP;
    private static final VoxelShape C_DOWN;
    private static final VoxelShape C_EAST;
    private static final VoxelShape C_WEST;
    private static final VoxelShape C_NORTH;
    private static final VoxelShape C_SOUTH;
    private static final VoxelShape S_UP;
    private static final VoxelShape S_DOWN;
    private static final VoxelShape S_EAST;
    private static final VoxelShape S_WEST;
    private static final VoxelShape S_NORTH;
    private static final VoxelShape S_SOUTH;


    static {
        UP = EnumProperty.of("pipe_up", PipeSide.class);
        DOWN = EnumProperty.of("pipe_down", PipeSide.class);
        NORTH = EnumProperty.of("pipe_north", PipeSide.class);
        SOUTH = EnumProperty.of("pipe_south", PipeSide.class);
        EAST = EnumProperty.of("pipe_east", PipeSide.class);
        WEST = EnumProperty.of("pipe_west", PipeSide.class);
        NODE = Block.createCuboidShape(4.5, 4.5, 4.5, 11.5, 11.5, 11.5);
        C_DOWN = Block.createCuboidShape(4.5, 0, 4.5, 11.5, 5, 11.5);
        C_UP = Block.createCuboidShape(4.5, 11, 4.5, 11.5, 16, 11.5);
        C_EAST = Block.createCuboidShape(11, 4.5, 4.5, 16, 11.5, 11.5);
        C_WEST = Block.createCuboidShape(0, 4.5, 4.5, 5, 11.5, 11.5);
        C_NORTH = Block.createCuboidShape(4.5, 4.5, 0, 11.5, 11.5, 5);
        C_SOUTH = Block.createCuboidShape(4.5, 4.5, 11, 11.5, 11.5, 16);
        S_UP = Block.createCuboidShape(3.5, 14, 3.5, 12.5, 16, 12.5);
        S_DOWN = Block.createCuboidShape(3.5, 0, 3.5, 12.5, 2, 12.5);
        S_EAST = Block.createCuboidShape(14, 3.5, 3.5, 16, 12.5, 12.5);
        S_WEST = Block.createCuboidShape(0, 3.5, 3.5, 2, 12.5, 12.5);
        S_NORTH = Block.createCuboidShape(3.5, 3.5, 0, 12.5, 12.5, 2);
        S_SOUTH = Block.createCuboidShape(3.5, 3.5, 14, 12.5, 12.5, 16);
    }

    public PipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(UP, PipeSide.NONE)
                .with(DOWN, PipeSide.NONE)
                .with(NORTH, PipeSide.NONE)
                .with(SOUTH, PipeSide.NONE)
                .with(EAST, PipeSide.NONE)
                .with(WEST, PipeSide.NONE));
    }

    public static EnumProperty<PipeSide> getProperty(Direction facing) {
        return switch (facing) {
            case UP -> UP;
            case DOWN -> DOWN;
            case EAST -> EAST;
            case WEST -> WEST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        Item item = player.getStackInHand(hand).getItem();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {

            if (!(item == ItemRegistry.SERVO) && !(item instanceof WrenchItem)) {
                for (Direction dir : Direction.values()) {
                    if (state.get(getProperty(dir)) == PipeSide.SERVO) {
                        this.openScreen(world, pos, (ServerPlayerEntity) player);
                        return ActionResult.CONSUME;
                    }
                }
                return ActionResult.PASS;
            }

            Direction side = hit.getSide();
            PipeSide current = state.get(getProperty(side));
            //SERVO
            if (item == ItemRegistry.SERVO) {
                if (current == PipeSide.CONNECTED || current == PipeSide.NONE) {
                    BlockState newState = state.with(getProperty(side), PipeSide.SERVO);
                    world.setBlockState(pos, newState);
                    setWrenched(world, pos, side, false);
                    if (!player.isCreative()) {
                        player.getStackInHand(hand).decrement(1);
                    }
                }
            } else {
                //WRENCH
                if (current == PipeSide.SERVO) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.SERVO));
                    world.setBlockState(pos, state.with(getProperty(side), PipeSide.NONE));
                    setWrenched(world, pos, side, false);
                } else {
                    BlockState state1 = world.getBlockState(pos.offset(side));
                    if (state1.getBlock() instanceof PipeBlock && state.get(getProperty(side)) == PipeSide.NONE && isWrenched(world, pos.offset(side), side.getOpposite())) {
                        world.setBlockState(pos.offset(side), state1.with(getProperty(side.getOpposite()), PipeSide.NONE));
                        setWrenched(world, pos.offset(side), side.getOpposite(), false);
                    } else if (!isWrenched(world, pos, side)) {
                        setWrenched(world, pos, side, true);
                        if (state1.getBlock() instanceof PipeBlock && state1.get(getProperty(side.getOpposite())) != PipeSide.SERVO) {
                            world.setBlockState(pos.offset(side), state1.with(getProperty(side.getOpposite()), PipeSide.NONE));
                            setWrenched(world, pos.offset(side), side.getOpposite(), false);
                        }
                    } else {
                        world.setBlockState(pos, state.with(getProperty(side), PipeSide.NONE));
                        setWrenched(world, pos, side, false);
                        Block block = state1.getBlock();
                        BlockEntity entity = world.getBlockEntity(pos.offset(side));
                        if (isConnectable(block, entity))
                            world.setBlockState(pos, state.with(getProperty(side), PipeSide.CONNECTED));

                    }
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = NODE;
        if (state.get(UP) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_UP, BooleanBiFunction.OR);
        if (state.get(DOWN) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_DOWN, BooleanBiFunction.OR);
        if (state.get(NORTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_NORTH, BooleanBiFunction.OR);
        if (state.get(EAST) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_EAST, BooleanBiFunction.OR);
        if (state.get(SOUTH) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_SOUTH, BooleanBiFunction.OR);
        if (state.get(WEST) != PipeSide.NONE)
            shape = VoxelShapes.combineAndSimplify(shape, C_WEST, BooleanBiFunction.OR);

        if (state.get(UP) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_UP, BooleanBiFunction.OR);
        if (state.get(DOWN) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_DOWN, BooleanBiFunction.OR);
        if (state.get(NORTH) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_NORTH, BooleanBiFunction.OR);
        if (state.get(EAST) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_EAST, BooleanBiFunction.OR);
        if (state.get(SOUTH) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_SOUTH, BooleanBiFunction.OR);
        if (state.get(WEST) == PipeSide.SERVO)
            shape = VoxelShapes.combineAndSimplify(shape, S_WEST, BooleanBiFunction.OR);

        return shape;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {

        if (world.isClient())
            return state;

        Block neighbor = world.getBlockState(posFrom).getBlock();
        if (state.get(getProperty(direction)) == PipeSide.SERVO || isWrenched(world, pos, direction))
            return state;

        if (neighbor instanceof PipeBlock && isWrenched(world, posFrom, direction.getOpposite())) {
            return state;
        }

        return state.with(getProperty(direction), isConnectable(neighbor, world.getBlockEntity(posFrom)) ? PipeSide.CONNECTED : PipeSide.NONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : Direction.values()) {
            BlockState neighborState = ctx.getWorld().getBlockState(blockPos.offset(direction));
            Block neighbor = neighborState.getBlock();

            if (neighbor instanceof PipeBlock && isWrenched(ctx.getWorld(), blockPos.offset(direction), direction.getOpposite()))
                state = state.with(getProperty(direction), PipeSide.NONE);
            else
                state = state.with(getProperty(direction), isConnectable(neighbor, ctx.getWorld().getBlockEntity(blockPos.offset(direction))) ? PipeSide.CONNECTED : PipeSide.NONE);

        }
        return state;
    }

    private boolean isConnectable(Block block, BlockEntity entity) {
        return block instanceof ItemPipeConnectable || block instanceof InventoryProvider || (entity instanceof Inventory && ((Inventory) entity).size() > 0);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            for (Direction dir : Direction.values())
                if (state.get(getProperty(dir)) == PipeSide.SERVO)
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegistry.SERVO));
            if (!world.isClient) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof PipeEntity pipeEntity) {
                    for (ItemStack stack : pipeEntity.getQueuedStacks()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);

    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeEntity(pos, state);
    }

    private void openScreen(World world, BlockPos pos, ServerPlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PipeEntity) {
            boolean bl = false;
            BlockState state = world.getBlockState(pos);
            for (Direction dir : Direction.values()) {
                if (state.get(getProperty(dir)) == PipeSide.SERVO)
                    bl = true;
            }

            if (bl)
                ScreenLoaderUtils.openScreen(player, (CustomScreenHandlerFactory) blockEntity);
        }


    }


    private boolean isWrenched(WorldAccess world, BlockPos pos, Direction d) {
        BlockEntity b = world.getBlockEntity(pos);
        if (!(b instanceof PipeEntity))
            return false;
        return ((PipeEntity) b).wrenched.get(d);
    }

    private void setWrenched(WorldAccess world, BlockPos pos, Direction d, boolean value) {
        BlockEntity b = world.getBlockEntity(pos);
        if (!(b instanceof PipeEntity))
            return;
        ((PipeEntity) b).wrenched.put(d, value);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Registry.ITEM_PIPE_BLOCK_ENTITY, (world2, pos, state2, entity) -> {
            if (!world.isClient) entity.tick();
            else entity.clientTick();
        });
    }

    /**
     * What is this for?
     * Basically, if a block adjacent to a pipe gets changed and that's also connectable to the pipe network, the pipe
     * adjacent to the block will tell all pipes in the network to clear their caches and recalculate routes. This is really
     * important to prevent stale, inaccurate cached values.
     *
     * @param state   Current BlockState of Pipe
     * @param world   Current world
     * @param pos     Current BlockPos of Pipe
     * @param block   Previous block before update
     * @param fromPos Position of block that caused the update
     * @param notify  ??? Notify neighbors maybe?
     */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {

        BlockState changedState = world.getBlockState(fromPos); //Get the new block at the modified position.
        if (changedState.getBlock() instanceof AirBlock || isConnectable(changedState.getBlock(), world.getBlockEntity(fromPos))) {
            BlockEntity pipeEntity = world.getBlockEntity(pos);
            if (pipeEntity instanceof PipeEntity pipe) {
                pipe.clearNetworkCache();
            }
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }
}
