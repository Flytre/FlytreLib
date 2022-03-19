package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.api.base.util.Formatter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;

public class PipeResult {
    private final LinkedList<BlockPos> path;
    private final BlockPos destination;
    private final ItemStack stack;
    private final Direction direction;
    private Direction anim;
    private int length;

    public PipeResult(BlockPos destination, LinkedList<BlockPos> path, ItemStack stack, Direction direction, Direction anim) {
        this.path = path;
        this.destination = destination;
        this.stack = stack.copy();
        this.direction = direction;
        this.length = path.size();
        this.anim = anim;
    }

    public static PipeResult fromTag(NbtCompound tag) {
        BlockPos end = Formatter.arrToPos(tag.getIntArray("end"));

        LinkedList<BlockPos> path = new LinkedList<>();
        NbtList list = tag.getList("path", 11);
        for (int i = 0; i < list.size(); i++)
            path.add(Formatter.arrToPos(list.getIntArray(i)));

        int length = tag.getInt("length");
        NbtCompound stack = tag.getCompound("stack");
        ItemStack stack2 = ItemStack.fromNbt(stack);
        Direction d = Direction.byId(tag.getInt("dir"));
        Direction anim = tag.contains("anim") ? Direction.byId(tag.getInt("anim")) : null;
        PipeResult result = new PipeResult(end, path, stack2, d, anim);
        result.length = length;
        return result;
    }

    public void removeAnim() {
        anim = null;
    }

    public Direction getAnim() {
        return anim;
    }

    public LinkedList<BlockPos> getPath() {
        return path;
    }

    public BlockPos getDestination() {
        return destination;
    }

    public Direction getDirection() {
        return direction;
    }

    public NbtCompound toTag(NbtCompound tag, boolean client) {
        tag.put("end", Formatter.writePosToNbt(destination));
        NbtList list = new NbtList();
        if (!client) {
            for (BlockPos pathPos : path)
                list.add(Formatter.writePosToNbt(pathPos));
        } else {
            for (int i = 0; i < Math.min(path.size(), 2); i++) {
                list.add(Formatter.writePosToNbt(path.get(i)));
            }
        }
        tag.put("path", list);

        NbtCompound stack = new NbtCompound();
        this.stack.writeNbt(stack);
        tag.put("stack", stack);
        tag.putInt("dir", direction.getId());

        if (anim != null)
            tag.putInt("anim", anim.getId());

        if (!client)
            tag.putInt("length", length);

        return tag;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "PipeResult{" +
                "path=" + path +
                ", destination=" + destination +
                ", stack=" + stack +
                ", direction=" + direction +
                '}';
    }


    public PipeResult copy() {
        return new PipeResult(destination, new LinkedList<>(path), stack.copy(), direction, anim);
    }

}
