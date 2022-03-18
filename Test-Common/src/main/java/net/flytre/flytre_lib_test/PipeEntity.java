package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.base.util.InventoryUtils;
import net.flytre.flytre_lib.loader.CustomScreenHandlerFactory;
import net.flytre.flytre_lib.storage.inventory.filter.FilterInventory;
import net.flytre.flytre_lib.storage.inventory.filter.Filtered;
import net.flytre.flytre_lib.storage.inventory.filter.packet.FilterEventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Some terms defined:
 * -An ItemStack (item stack) represents some quantity of some item. For example, 64 carrots, 10 apples, or 1 enchanted diamond sword.
 * -A block entity is used to store custom data and logic for blocks with special properties. An example would be a chest, which stores data
 * about what items are inside or a sign, which stores data about the text written on it.
 * -Implementing ExtendedScreenHandlerFactory basically means that pipes have screens
 * -BlockEntityClientSerializable is an interface that allows me to send custom data from the server to the client about this block entity
 * -Filtered is an interface from my Minecraft modding library which indicates that this class contains a filter (which restricts the passage of items by a predicate, essentially)
 * -A BlockState is used to store predefined states for a block. For example, the way stairs are facing, whether a lamp is lit, or what 4 sides of a tree trunk have bark
 * -A BlockPos is basically just a coordinate of 3 integers
 * -Nbt is Minecraft's method of serializing data (to a file), and is used for any data that needs to persist between play sessions
 * <p>
 * <p>
 * The way pipes work, is that a sequence of pipes connects source(s) to destination(s). Sources are indicated by the presence of a servo on that end of the pipe
 * which indicates that items should be pulled from that location and not deposited there. After analyzing the network of connected pipes from that pipe, the filters,
 * and the configuration of the pipe (i.e. round robin mode, and "wrenched" sides, explained below), the pipe chooses the route and transfers the item from the source to the right
 * destination. This should be the closest (least distance travelled) valid destination, if round robin mode is off, or the next valid destination, if round robin is on.
 * <p>
 * <p>
 * That all being said, a pipe entity is a block entity that stores data about a pipe
 */
public final class PipeEntity extends BlockEntity implements CustomScreenHandlerFactory, Filtered, FilterEventHandler {


    /**
     * Cache is stored as a linked hash map, so unused entries are sometimes automatically removed to save memory
     * <p>
     * <p>
     * It's important to note that in find-nearest mode (default), the cache stores the valid destination found
     * or lack thereof, while in round-robin mode the cache stores ALL possible destinations regardless of whether an item can actually be inserted and then validates them
     * when the cache is asked to retrieve the value.
     * <p>
     * The reason for this is that the memory cost and computational cost would be high in find-nearest, while in round-robin those costs
     * are expected and re-calculating the cache everytime the state of an inventory changes so that an item can now or no longer be inserted would be costly
     * <p>
     * <p>
     * Important things to note about caching.
     * -Needs to handle pipes being removed / added
     * -Needs to handle destination inventories being removed / added
     * -Needs to handle state of destination inventories changing (i.e. filling up, emptying out)
     * -Cannot consume too much memory or that will slow other processes
     * -Needs to handle conditions being updated, i.e. filter being updated
     */
    private final transient LinkedHashMap<CacheKey, CacheResult> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<CacheKey, CacheResult> eldest) {

            long time = world == null ? Long.MAX_VALUE : world.getTime();
            long entryTime = eldest.getValue().time;
            return time - entryTime > 500 && size() > 10;
        }
    };

    /**
     * The network stores the locations of all pipes in the network; The same set is shared across all pipes in the network so it must
     * not be altered inappropriately.
     */
    public transient Set<BlockPos> network = new HashSet<>();


    /**
     * Stores a map of each side of the pipe (indicated by direction) and whether that side is "wrenched"
     * Sides that are "wrenches" artificially block incoming connections, so that pipes can be placed
     * side by side without connecting
     */
    public Map<Direction, Boolean> wrenched;

    /**
     * How long since the cache has been cleared by block update.
     * The point of this variable is to prevent clearing the cache multiple times in the same tick,
     * which is an expensive operation because it propagates through the entire pipe network and has
     * to clear the caches of all pipes after finding them
     */
    private int ticksSinceLastCacheClear = 0;
    /**
     * How long ago the cache was used. If the cache hasn't been used in a long time, which means
     * items stopped flowing through that pipe, just empty the cache to save memory
     */
    private transient long lastCacheTick = 0;

    /**
     * Represents the items currently flowing through the pipe.
     * A "TimedPipeResult" stores:
     * How long until this item has passed through this pipe and needs to go to the next one
     * Whether the item is stuck and is attempted to go to an invalid destination
     * (for example, if a pipe in the route is broken after the route the item will take is calculated)
     * Data about the item that's going through the pipe, i.e. what item it is and what properties it has
     * The queue of pipes the item must travel through to reach the destination
     * The location of the destination (on the coordinate plane)
     * Client: What side of the pipe the item is travelling towards, for item rendering purposes
     * The total distance this item needs to travel
     */
    private Set<TimedPipeResult> items;

    /**
     * The following two variables store data about round robin mode:
     * -Is the pipe in round-robin mode? (Versus closest valid destination)
     * -Of all the round-robin destinations, which destination is next?
     */
    private int roundRobinIndex;
    private boolean roundRobinMode;

    /**
     * A pipe can only transmit items so often. This stores the cooldown until it can transmit another item
     */
    private int cooldown;

    /**
     * A filter inventory is a utility class in my Minecraft library mod that stores information about a filter -
     * namely, data about what kinds of items are allowed through it and whether any given item would be allowed to
     * pass through
     */
    private FilterInventory filter;

    /**
     * Stores when data needs to be synced to the client, then syncs it when this is true
     */
    private boolean needsSync;


    /**
     * How fast this pipe should move items, so its speed.
     */
    private int ticksPerOperation = 20;
    /**
     * Checks if ticksPerOperation is set to match whether this is a fast pipe or normal, and if not
     * takes care of that.
     */
    private boolean speedSet = false;


    /**
     * Something to note is that state-dependent data about a block entity is usually initialized in the readNbt() method, and not in the
     * constructor. Therefore the constructor is almost meaningless except as initializing a default state.
     */
    public PipeEntity(BlockPos pos, BlockState state) {
        super(Registry.ITEM_PIPE_BLOCK_ENTITY, pos, state);
        roundRobinIndex = 0;
        roundRobinMode = false;
        items = new HashSet<>();
        wrenched = Arrays.stream(Direction.values()).collect(Collectors.toMap(Function.identity(), i -> false));
        filter = FilterInventory.readNbt(new NbtCompound(), 1); //Basically, its asking the filter inventory to read from no nbt, so it creates a default filter inventory.
        needsSync = false;
    }


    /**
     * A useful analogy of this function is to think of a 3D area fill scenario, where walls cannot be traversed.
     * In this analogy, this function detects whether each adjacent block is a wall or empty.
     *
     * @param startingPos    The position of the block to execute this function on.
     * @param world          The world that this block is in.
     * @param stack          The item stack that is being transferred
     * @param checkInsertion whether to check if said item stack needs to actually be insertable into potential inventories
     * @return The collection of potential directions the item could go next
     * <p>
     */
    public static Collection<Direction> transferableDirections(BlockPos startingPos, World world, ItemStack stack, boolean checkInsertion, boolean stuck) {
        Set<Direction> result = new HashSet<>();

        if (world == null)
            return result;

        PipeEntity me = (PipeEntity) world.getBlockEntity(startingPos);

        if (me == null)
            return result;

        for (Direction direction : Direction.values()) { //For each possible direction

            if ((me.getSide(direction) == PipeSide.CONNECTED)) { //If this pipe is connected to something
                BlockPos pos = startingPos.offset(direction);
                BlockEntity entity = world.getBlockEntity(pos);
                Inventory inventory = InventoryUtils.getInventoryAt(world, pos);

                if (entity instanceof PipeEntity pipeEntity) { //If connected to another pipe
                    PipeSide state = world.getBlockState(startingPos.offset(direction)).get(PipeBlock.getProperty(direction.getOpposite()));
                    boolean valid = state == PipeSide.CONNECTED || (state == PipeSide.SERVO && (pipeEntity.filter.isEmpty() || pipeEntity.filter.passFilterTest(stack)));
                    if (valid)
                        result.add(direction);
                } else if (inventory != null) { //If connected to an inventory
                    if (!checkInsertion || canInsertFirm(world, me.network, stack, inventory, direction, stuck)) { //If the stack can be inserted into that inventory
                        result.add(direction);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param stack       the item stack to check for
     * @param destination the destination inventory
     * @param direction   the direction to insert from
     * @return Checks if item stack X be inserted into inventory Y from Direction D. However, it also takes into consideration all other items flowing into the inventory
     * from the network, and if those being inserted already would make it impossible for item stack X to be inserted, MAY return false.
     */
    private static boolean canInsertFirm(World world, Set<BlockPos> network, ItemStack stack, Inventory destination, Direction direction, boolean isStuck) {


        //Works because you can only extract stacks of 1 item at a time.
        var flows = getFlows(network, world);
        var wrapped = new WrappedItemStack(stack);


        //basically, if multiple items are stuck they will block EACH OTHER from finding a valid container; using isStuck to manually override that behavior prevents this.
        int flowCt = isStuck ? 0 : (flows.containsKey(wrapped) ? (int) flows.get(wrapped).stream().filter(k -> InventoryUtils.getInventoryAt(world, k.getPipeResult().getDestination()) == destination).count() : 0);


        //if there are no items of that type flowing through the pipe return true
        if (flowCt == 0) {
            return destination != null && InventoryUtils.getAvailableSlots(destination, direction.getOpposite()).anyMatch(i -> {
                ItemStack slotStack = destination.getStack(i);
                if (InventoryUtils.canInsert(destination, stack, i, direction.getOpposite()))
                    return slotStack.isEmpty() || (InventoryUtils.canMergeItems(slotStack, stack) && slotStack.getCount() < slotStack.getMaxCount());
                return false;
            });
        } else {
            //basically, estimate how many items are going into the container and if it's going to be full, don't send the item
            if (destination == null)
                return false;
            int[] slots = InventoryUtils.getAvailableSlots(destination, direction.getOpposite()).toArray();
            ItemStack copy = stack.copy();
            copy.setCount(flowCt + 1);
            for (int slot : slots) {
                ItemStack slotStack = destination.getStack(slot);

                if (slotStack.getCount() >= slotStack.getMaxCount())
                    continue;

                if (InventoryUtils.canInsert(destination, copy, slot, direction.getOpposite())) {
                    if (slotStack.isEmpty()) {
                        if (copy.getCount() < 64)
                            return true;
                        else
                            copy.decrement(64);
                    } else if (InventoryUtils.canMergeItems(slotStack, copy)) {
                        int target = slotStack.getMaxCount() - slotStack.getCount();
                        if (copy.getCount() <= target)
                            return true;
                        else
                            copy.decrement(target);
                    }
                }
            }
            return false;
        }

    }

    //get all items flowing thru a network
    public static Map<WrappedItemStack, Set<TimedPipeResult>> getFlows(Set<BlockPos> network, World world) {
        Map<WrappedItemStack, Set<TimedPipeResult>> flows = new HashMap<>();

        assert world != null;
        for (BlockPos pos : network) {
            if (world.getBlockEntity(pos) instanceof PipeEntity pipe) {
                for (TimedPipeResult result : pipe.getQueuedItems()) {
                    WrappedItemStack stack = new WrappedItemStack(result.getPipeResult().getStack());
                    flows.putIfAbsent(stack, new HashSet<>());
                    flows.get(stack).add(result);
                }
            }
        }
        return flows;
    }

    /**
     * The validate method is used to ensure that a previously calculated cached route is still valid
     * This could return false if the destination block is destroyed or becomes full, a filter is changed so the item is no longer valid,
     * a pipe along the route is destroyed, etc.
     */
    private boolean validate(ItemStack stack, BlockPos start, PipeResult result) {
        assert world != null;
        Iterator<BlockPos> iterator = result.getPath().iterator();
        BlockEntity startEntity = world.getBlockEntity(start);

        if (startEntity == null)
            return false;

        while (iterator.hasNext()) {
            BlockPos next = iterator.next();
            if (next.equals(start))
                continue;

            PipeEntity nextPipe = (PipeEntity) world.getBlockEntity(next);
            @NotNull Direction direction = Objects.requireNonNull(Direction.fromVector(next.subtract(start)));
            if (!validateChain(stack, next, startEntity, nextPipe, direction))
                return false;

            start = next;
            startEntity = nextPipe;
        }

        BlockPos finalPos = result.getDestination();
        assert startEntity != null;
        return canInsertFirm(((PipeEntity) startEntity).world, ((PipeEntity) startEntity).network, stack, InventoryUtils.getInventoryAt(world, finalPos), Objects.requireNonNull(Direction.fromVector(finalPos.subtract(start))), false);
    }

    private boolean validateChain(ItemStack stack, BlockPos next, BlockEntity currentEntity, BlockEntity nextEntity, @NotNull Direction direction) {
        assert world != null;

        PipeEntity currentPipe = currentEntity instanceof PipeEntity ? (PipeEntity) currentEntity : null;
        PipeEntity nextPipe = nextEntity instanceof PipeEntity ? (PipeEntity) nextEntity : null;

        if (nextPipe != null && (currentPipe == null || currentPipe.getSide(direction) == PipeSide.CONNECTED)) {
            PipeSide state = world.getBlockState(next).get(PipeBlock.getProperty(direction.getOpposite()));
            return state == PipeSide.CONNECTED || (state == PipeSide.SERVO && (nextPipe.filter.isEmpty() || nextPipe.filter.passFilterTest(stack)));
        }

        return false;
    }

    @Override
    public FilterInventory getFilter() {
        return filter;
    }

    public Set<TimedPipeResult> getQueuedItems() {
        return items;
    }

    /**
     * One=true indicates the normal mode, where the method returns after finding the nearest valid location. When one is false, used for round-
     * robin mode, it finds all possible (including ones where the item cannot be inserted due to the state of the inventory) locations and returns them in order sorted from nearest to furthest.
     * Performs a BFS search and returns the first valid location it finds to dump the items into
     */
    private List<PipeResult> internalFindDestinations(ItemStack stack, BlockPos start, boolean one, boolean stuck) {

        Direction animate = null;
        for (Direction dir : Direction.values()) {
            if (getPos().offset(dir).equals(start))
                animate = dir;
        }
        List<PipeResult> result = new ArrayList<>();
        if (world == null)
            return result;

        Deque<PipeResult> toVisit = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<VisitedAngle> inventorySides = new HashSet<>();
        toVisit.add(new PipeResult(this.getPos(), new LinkedList<>(), stack, Direction.NORTH, animate));

        while (toVisit.size() > 0) {
            PipeResult popped = toVisit.pop();
            BlockPos current = popped.getDestination();
            Queue<BlockPos> path = popped.getPath();

            if (!current.equals(start)) {
                if (InventoryUtils.getInventoryAt(world, current) != null) {
                    VisitedAngle side = new VisitedAngle(popped.getDirection(), current);
                    if (!(inventorySides.contains(side))) {
                        result.add(popped);
                        inventorySides.add(side);
                        if (one)
                            return result;
                    }
                }
            }

            if (world.getBlockEntity(current) instanceof PipeEntity) {
                Collection<Direction> neighbors = PipeEntity.transferableDirections(current, world, stack, one, stuck);
                for (Direction d : neighbors) {
                    if (!visited.contains(current.offset(d)) || InventoryUtils.getInventoryAt(world, current.offset(d)) != null) {
                        LinkedList<BlockPos> newPath = new LinkedList<>(path);
                        newPath.add(current);
                        toVisit.add(new PipeResult(current.offset(d), newPath, stack, d.getOpposite(), animate));
                    }
                }
            }
            visited.add(current);
        }

        return result;
    }

    /**
     * @return the list of routes the designated item could take.
     * <p>
     * Internally, it uses a cache based approach although this is subject to change.
     */
    public List<PipeResult> findDestinations(ItemStack stack, BlockPos start, boolean one, boolean stuck) {
        assert world != null;
        CacheKey key = new CacheKey(stack, start, one);
        if (cache.containsKey(key)) {
            List<PipeResult> val = cache.get(key).value;


            //If the cache returns an empty result and one=false, this could be because the destination inventories cannot
            //store an item.
            boolean clear = val.isEmpty() && Math.abs(world.getTime() - cache.get(key).time) > 100;


            if (!clear && !one) {
                lastCacheTick = world.getTime();
                return val.stream().filter(i -> canInsertFirm(world, network, stack, InventoryUtils.getInventoryAt(world, i.getDestination()), i.getDirection().getOpposite(), stuck)).map(PipeResult::copy).collect(Collectors.toList());
            } else if (!clear && (val.stream().allMatch(i -> validate(stack, start, i)))) {
                lastCacheTick = world.getTime();
                //Copy the cache value to prevent a reference leak which enables modifying the cache
                return val.stream().map(PipeResult::copy).collect(Collectors.toList());
            } else {
                cache.remove(key);
            }
        }

        List<PipeResult> toCache = internalFindDestinations(stack, start, one, stuck);
        cache.put(key, new CacheResult(world.getTime(), toCache));
        lastCacheTick = world.getTime();
        //Copy the cache value to prevent a reference leak which enables modifying the cache
        return toCache.stream().map(PipeResult::copy).collect(Collectors.toList());

    }

    /**
     * This method is used for item inventory extraction. It searches all connected inventories that
     * are marked for extraction (via servo), and attempts to find an item it can extract and transfer to destination
     * inventory(s)
     */
    private void addToQueue() {
        for (Direction d : Direction.values()) {
            if (hasServo(d) && cooldown <= 0) {
                assert world != null;
                Inventory out = InventoryUtils.getInventoryAt(world, this.pos.offset(d));
                Direction opp = d.getOpposite();

                if (out == null || InventoryUtils.isInventoryEmpty(out, opp))
                    continue;

                int[] arr = InventoryUtils.getAvailableSlots(out, opp).toArray();
                for (int i : arr) {

                    ItemStack stack = out.getStack(i);
                    if (!InventoryUtils.canExtract(out, stack, i, opp) || (!filter.isEmpty() && !filter.passFilterTest(stack)))
                        continue;

                    if (stack.isEmpty())
                        continue;
                    ItemStack one = stack.copy();
                    one.setCount(1);

                    PipeResult result;
                    if (isRoundRobinMode()) {
                        List<PipeResult> results = findDestinations(one, this.pos.offset(d), false, false);
                        if (results.size() <= roundRobinIndex) {
                            roundRobinIndex = 0;
                        }

                        result = roundRobinIndex < results.size() ? results.get(roundRobinIndex++) : null;
                    } else {
                        List<PipeResult> results = findDestinations(one, this.pos.offset(d), true, false);
                        result = results.size() == 0 ? null : results.get(0);
                    }
                    if (result != null) {
                        items.add(new TimedPipeResult(result, ticksPerOperation * 3 / 2));
                        stack.decrement(1);
                        out.markDirty();
                        markDirty();
                        if (result.getLength() < Registry.PIPE_CONFIG.getConfig().maxRenderPipeLength)
                            needsSync = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the PipeSide for the given Direction, aka whether/how the pipe is connected at that side.
     */
    public PipeSide getSide(Direction d) {
        if (world == null)
            return null;
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof PipeBlock))
            return null;
        return state.get(PipeBlock.getProperty(d));
    }

    public boolean hasServo(Direction d) {
        return getSide(d) == PipeSide.SERVO;
    }


    /**
     * Serialize data to be saved for when the area the pipe is in is unloaded.
     */
    @Override
    protected void writeNbt(NbtCompound tag) {
        tag.putInt("wrenched", Formatter.mapToInt(wrenched));
        tag.putInt("rri", this.roundRobinIndex);
        tag.putBoolean("rrm", this.roundRobinMode);
        tag.putInt("cooldown", this.cooldown);
        NbtList list = new NbtList();
        for (TimedPipeResult piped : items)
            list.add(piped.toTag(new NbtCompound(), false));
        tag.put("queue", list);
        tag.put("filter", filter.writeNbt());
    }

    public boolean isRoundRobinMode() {
        return roundRobinMode;
    }

    public void setRoundRobinMode(boolean roundRobinMode) {
        this.roundRobinMode = roundRobinMode;
    }

    /**
     * Read serialized data to set the state of the pipe when the area its in is loaded
     */
    @Override
    public void readNbt(NbtCompound tag) {
        if (tag.contains("cooldown"))
            this.cooldown = tag.getInt("cooldown");

        if (tag.contains("rri"))
            this.roundRobinIndex = tag.getInt("rri");

        if (tag.contains("rrm"))
            this.roundRobinMode = tag.getBoolean("rrm");

        if (tag.contains("wrenched"))
            this.wrenched = Formatter.intToMap(tag.getInt("wrenched"));
        readQueueFromTag(tag);

        if (tag.contains("filter")) {
            NbtCompound filter = tag.getCompound("filter");
            this.filter = FilterInventory.readNbt(filter, 1);
        }

        if (tag.contains("ticksPerOperation"))
            ticksPerOperation = tag.getInt("ticksPerOperation");

        super.readNbt(tag);
    }


    /**
     * Gets a collection of all item stacks that are travelling through the pipe currently
     */
    public Collection<ItemStack> getQueuedStacks() {
        Set<ItemStack> result = new HashSet<>();

        for (TimedPipeResult pipeResult : items) {
            result.add(pipeResult.getPipeResult().getStack());
        }

        return result;
    }


    /**
     * Looks long but is not scary:
     * <p>
     * Basically, tick down the time remaining of each item in the pipe
     * If the time is 0, try and transfer it to the next pipe along the sequence
     * If the next pipe / destination doesn't exist, handle that.
     * Else, remove this item from the pipe and add it to the next pipe
     */
    public void tickQueuedItems() {
        Set<TimedPipeResult> toRemove = new HashSet<>(items.size() / 2 + 1);
        Set<TimedPipeResult> toAdd = new HashSet<>(items.size() / 2 + 1);


        for (TimedPipeResult timed : items) {
            timed.decreaseTime();
            if (timed.getTime() <= 0) {
                if (timed.getPipeResult().getLength() < Registry.PIPE_CONFIG.getConfig().maxRenderPipeLength)
                    needsSync = true;
                Queue<BlockPos> path = timed.getPipeResult().getPath();
                if (this.pos.equals(path.peek()))
                    path.poll(); //remove current block
                if (path.size() > 0) {
                    BlockPos next = path.peek();
                    assert world != null;
                    BlockEntity entity = world.getBlockEntity(next);
                    if (!(entity instanceof PipeEntity pipeEntity)) {
                        tickHelper(toRemove, toAdd, timed);
                    } else {
                        timed.setStuck(false);
                        pipeEntity.addResultToPending(timed);
                        toRemove.add(timed);
                        timed.setTime(pipeEntity.ticksPerOperation);
                    }
                } else {
                    boolean transferred = transferItem(timed);

                    if (!transferred) {
                        tickHelper(toRemove, toAdd, timed);
                    } else
                        toRemove.add(timed);
                }
            }
        }

        items.removeAll(toRemove);
        items.addAll(toAdd);

    }

    /**
     * Random helper method to avoid duplicate code. Either marks the current piped item as stuck, or moves it
     * to the next pipe.
     */
    private void tickHelper(Set<TimedPipeResult> toRemove, Set<TimedPipeResult> toAdd, TimedPipeResult timed) {
        List<PipeResult> results = findDestinations(timed.getPipeResult().getStack(), getPos(), true, false);
        if (results.size() == 0) {
            timed.setTime(20);
            timed.setStuck(true);
        } else {
            TimedPipeResult zero = new TimedPipeResult(results.get(0), ticksPerOperation, false);
            toAdd.add(zero);
            toRemove.add(timed);
        }
    }

    /**
     * Client should only handle client-side logic, which is just rendering
     * Tick functions are executed every 50 milliseconds
     */
    public void clientTick() {
        for (TimedPipeResult timed : items) {
            timed.decreaseTime();
        }
    }

    /**
     * Server side logic.
     * Tick functions are executed every 50 milliseconds, so there are 20 ticks per second
     */
    public void tick() {

        if (network.isEmpty()) { //basically, artifically construct the network of this pipe if its empty;
            clearNetworkCache();
        }

        if (!speedSet && world != null) {
            speedSet = true;
            ticksPerOperation = world.getBlockState(pos).getBlock() == Registry.FAST_PIPE ? 8 : 20;
        }

        if (cooldown > 0)
            cooldown--;

        tickQueuedItems();

        if (cooldown <= 0) {
            addToQueue();
            cooldown = ticksPerOperation / 2;
        }


        if (needsSync) {
            sync();
            needsSync = false;
        }

        if (world == null || (world.getTime() - lastCacheTick > 600)) { //30 secs
            cache.clear();
        }

        ticksSinceLastCacheClear++;
    }

    /**
     * Attempts to transfer an item to the destination from the last pipe.
     * Return false if it fails so executing code can handle it
     */
    private boolean transferItem(TimedPipeResult timedPipeResult) {

        PipeResult processed = timedPipeResult.getPipeResult();
        assert world != null;
        Inventory inv = InventoryUtils.getInventoryAt(world, processed.getDestination());

        if (inv == null)
            return false;

        if (InventoryUtils.isInventoryFull(inv, processed.getDirection())) {
            return false;
        }

        int[] slots = InventoryUtils.getAvailableSlots(inv, processed.getDirection()).toArray();
        for (int i : slots) {
            ItemStack currentStack = inv.getStack(i);
            if (InventoryUtils.canInsert(inv, processed.getStack(), i, processed.getDirection())) {
                if (currentStack.isEmpty()) {
                    inv.setStack(i, processed.getStack());
                    inv.markDirty();
                    return true;
                } else if (InventoryUtils.canMergeItems(currentStack, processed.getStack())) {
                    if (currentStack.getCount() < currentStack.getMaxCount()) {
                        currentStack.increment(1);
                        inv.markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * The reason to use this method instead of adding directly is to decrease network traffic by not sending an update to the client
     * if this item does not need to be rendered.
     */
    public void addResultToPending(TimedPipeResult result) {
        this.items.add(result);
        if (result.getPipeResult().getLength() < Registry.PIPE_CONFIG.getConfig().maxRenderPipeLength)
            sync();
    }


    /**
     * Send data to the client about what to display on the pipe's screen.
     */
    @Override
    public void sendPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(this.filter.getFilterType());
        packetByteBuf.writeBoolean(this.filter.isMatchMod());
        packetByteBuf.writeBoolean(this.filter.isMatchNbt());
        packetByteBuf.writeBoolean(this.isRoundRobinMode());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.pipe.item_pipe");
    }

    /**
     * ScreenHandlers represent server side logic for a screen displayed client sde
     */
    @Override
    public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PipeHandler(syncId, inv, this);
    }

    @Override
    public @NotNull Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, (i -> ((PipeEntity) i).toClientTag()));
    }

    private void readQueueFromTag(NbtCompound tag) {
        NbtList list = tag.getList("queue", 10);
        items = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            TimedPipeResult result = TimedPipeResult.fromTag(list.getCompound(i));
            items.add(result);
        }
    }

    public NbtCompound toClientTag() {
        NbtCompound tag = new NbtCompound();
        Config cfg = Registry.PIPE_CONFIG.getConfig();
        NbtList list = new NbtList();
        for (TimedPipeResult piped : items)
            if (piped.getPipeResult().getLength() < cfg.maxRenderPipeLength)
                list.add(piped.toTag(new NbtCompound(), true));

        tag.put("queue", list);
        tag.putInt("ticksPerOperation", ticksPerOperation);
        super.writeNbt(tag);
        return tag;
    }

    private void sync() {
        if (!Registry.PIPE_CONFIG.getConfig().renderItems)
            return;
        Objects.requireNonNull(getWorld()).updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    public int getTicksPerOperation() {
        return ticksPerOperation;
    }

    /**
     * Clears the caches of all pipes in the network. Used to invalidate stale cache values
     */
    public void clearNetworkCache() {
        //Basically if you try to clear the cache multiple times in 1 tick, i.e. with a large number of blocks being placed at once,
        //The cache will not have regenerated yet, so it'll still be empty BUT you still recur through the whole network, causing lag.
        //To solve this, I store how long since the last cache clear as an integer.
        if (ticksSinceLastCacheClear == 0)
            return;

        Set<BlockPos> network = new HashSet<>();
        clearNetworkCacheRipple(network);
    }

    private void clearNetworkCacheRipple(Set<BlockPos> network) {
        network.add(pos);
        this.cache.clear();
        this.network = network; //note that all pipes in a network SHARE the same network set AND it's mutable. Dangerous, huh?
        ticksSinceLastCacheClear = 0;
        for (Direction direction : Direction.values()) { //For each possible direction

            if ((getSide(direction) == PipeSide.CONNECTED)) { //If this pipe is connected to something
                BlockPos pos = this.pos.offset(direction);

                if (network.contains(pos))
                    continue;

                assert world != null;
                if (world.getBlockEntity(pos) instanceof PipeEntity pipeEntity) { //If connected to another pipe
                    PipeSide state = world.getBlockState(this.pos.offset(direction)).get(PipeBlock.getProperty(direction.getOpposite()));
                    if (state == PipeSide.CONNECTED || state == PipeSide.SERVO) {
                        pipeEntity.clearNetworkCacheRipple(network);
                    }
                }
            }
        }
    }


    /**
     * Event that's automagically called when a filter update packet is received so the cache can be cleared.
     */
    @Override
    public void onPacketReceived() {
        clearNetworkCache();
    }

    private record CacheKey(@NotNull ItemStack stack, @NotNull BlockPos start, boolean one) {


        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            CacheKey cacheKey = (CacheKey) o;

            if (one != cacheKey.one) return false;
            if (!start.equals(cacheKey.start))
                return false;
            return ItemStack.areEqual(stack, cacheKey.stack);
        }

        @Override
        public int hashCode() {
            int result = net.minecraft.util.registry.Registry.ITEM.getId(stack.getItem()).hashCode(); // O(1)
            result = 31 * result + start.hashCode();
            result = 31 * result + (one ? 1 : 0);
            return result;
        }
    }


    /**
     * A cache value stores both the time it was calculated and the actual value.
     * The reason for this is that I arbitrarily told the cache to store up to 10 values to save memory from being polluted
     * by unused cache values.
     * This becomes a problem if the pipe is set on round-robin mode and is transmitting to
     * more than 10 outputs, causing the cache to become useless. The solution to this issue is to
     * not remove cache values that are too new, thus preserving the integrity of the cache.
     */
    private record CacheResult(long time, List<PipeResult> value) {

    }


    /**
     * When doing the BFS search, the pos stores the actual position while the direction stores
     * the direction travelled to get to that position. This is important because you can access
     * inventories from multiple sides, and in round-robin mode it should hit all sides.
     */
    private record VisitedAngle(@Nullable Direction direction, BlockPos pos) {


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VisitedAngle that = (VisitedAngle) o;

            if (direction != that.direction) return false;
            return Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            int result = direction != null ? direction.hashCode() : 0;
            result = 31 * result + (pos != null ? pos.hashCode() : 0);
            return result;
        }
    }

    /**
     * the WrappedItemStack class is used to give ItemStacks a hashCode and equals
     */
    private record WrappedItemStack(ItemStack stack) {
        @Override
        public boolean equals(Object wrapped) {
            if (this == wrapped)
                return true;

            if (wrapped == null || getClass() != wrapped.getClass())
                return false;

            return ItemStack.areEqual(this.stack, ((WrappedItemStack) wrapped).stack);
        }

        @Override
        public int hashCode() {
            if (stack == null)
                return 0;

            return net.minecraft.util.registry.Registry.ITEM.getRawId(stack.getItem()) +
                    stack.getCount() * 31 +
                    (stack.getNbt() != null ? stack.getNbt().hashCode() * 31 * 31 : 0);
        }
    }
}
