package net.flytre.flytre_lib_test;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;

public class TimedPipeResult {
    public static final TimedPipeResult DEFAULT;

    static {
        PipeResult result = new PipeResult(BlockPos.ORIGIN, new LinkedList<>(), ItemStack.EMPTY, Direction.NORTH, Direction.NORTH);
        DEFAULT = new TimedPipeResult(result, 9999);
    }

    private final PipeResult pipeResult;
    private int time;
    private boolean stuck;

    public TimedPipeResult(PipeResult pipeResult, int time) {
        this(pipeResult, time, false);
    }

    public TimedPipeResult(PipeResult pipeResult, int time, boolean stuck) {
        this.pipeResult = pipeResult;
        this.time = time;
        this.stuck = stuck;
    }

    public static TimedPipeResult fromTag(NbtCompound tag) {
        NbtCompound pipeTag = tag.getCompound("result");
        PipeResult result = PipeResult.fromTag(pipeTag);
        int time = tag.getInt("time");
        boolean stuck = tag.getBoolean("stuck");
        return new TimedPipeResult(result, time, stuck);
    }

    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    public PipeResult getPipeResult() {
        return pipeResult;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void decreaseTime() {
        this.time--;
        if (time <= 0)
            getPipeResult().removeAnim();
    }

    public NbtCompound toTag(NbtCompound tag, boolean client) {
        NbtCompound pipeTag = new NbtCompound();
        tag.put("result", pipeResult.toTag(pipeTag, client));
        tag.putInt("time", time);
        tag.putBoolean("stuck", stuck);
        return tag;
    }

}
