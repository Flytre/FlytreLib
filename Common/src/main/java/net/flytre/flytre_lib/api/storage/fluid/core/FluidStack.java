package net.flytre.flytre_lib.api.storage.fluid.core;

import com.google.gson.JsonObject;
import net.flytre.flytre_lib.api.base.util.Formatter;
import net.flytre.flytre_lib.api.storage.recipe.JsonFraction;
import net.flytre.flytre_lib.mixin.storage.fluid.FluidBlockAccessor;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * ItemStacks but for fluids!
 */
public class FluidStack {

    /**
     * The empty fluid stack!
     */
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);
    public static final long UNITS_PER_BUCKET = 81000;
    public static final long UNITS_PER_EXPERIENCE = UNITS_PER_BUCKET / 250L;
    private final Fluid fluid;
    private long units;
    @Nullable
    private NbtCompound nbt;

    /**
     * Instantiates a new Fluid stack.
     *
     * @param fluid the fluid
     * @param units the number of units
     */
    public FluidStack(@NotNull Fluid fluid, long units) {
        this.fluid = fluid;
        this.units = units;
    }

    public FluidStack(@NotNull Fluid fluid, long units, NbtCompound nbt) {
        this.fluid = fluid;
        this.units = units;
        this.nbt = nbt;
    }

    /**
     * Whether two fluid stacks are equal
     *
     * @param left  the left
     * @param right the right
     * @return the boolean
     */
    public static boolean areEqual(FluidStack left, FluidStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        } else {
            return !left.isEmpty() && !right.isEmpty() && left.isEqual(right);
        }
    }

    public static boolean areNbtEqual(FluidStack left, FluidStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.nbt == null && right.nbt != null) {
            return false;
        }
        return left.nbt == null || left.nbt.equals(right.nbt);
    }

    /**
     * Get a fluid stack from a tag
     *
     * @param tag the tag
     * @return the fluid stack
     */
    public static FluidStack readNbt(NbtCompound tag) {
        Fluid fluid = tag.contains("id") ? Registry.FLUID.get(Identifier.tryParse(tag.getString("id"))) : Fluids.EMPTY;

        if (fluid == Fluids.EMPTY)
            return FluidStack.EMPTY;

        long amount = tag.getLong("amount");
        if (!tag.contains("tag"))
            return new FluidStack(fluid, amount);

        return new FluidStack(fluid, amount, tag.getCompound("tag"));
    }

    //DOES NOT SUPPORT NBT
    public static FluidStack fromJson(JsonObject object) {
        long amount = FluidStack.UNITS_PER_BUCKET;
        if (JsonHelper.hasPrimitive(object, "amount")) {
            amount = JsonHelper.getLong(object, "amount");
        } else if (JsonHelper.hasElement(object, "amount")) {
            JsonFraction fraction = JsonFraction.fromJson(object.getAsJsonObject("amount"));
            amount = (long) (((double) fraction.getNumerator() / (double) fraction.getDenominator()) * FluidStack.UNITS_PER_BUCKET);
        }
        String attempt = JsonHelper.getString(object, "fluid");
        Identifier fluid = new Identifier(attempt);

        return new FluidStack(Registry.FLUID.getOrEmpty(fluid).orElseThrow(() -> new IllegalStateException("Fluid: " + attempt + " does not exist")), amount);
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        Identifier fluid = buf.readIdentifier();
        Fluid f = Registry.FLUID.getOrEmpty(fluid).orElse(Fluids.EMPTY);
        long amount = buf.readLong();
        boolean hasNbt = buf.readBoolean();

        if (hasNbt)
            return new FluidStack(f, amount, buf.readNbt());

        return new FluidStack(f, amount);
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public long getAmount() {
        return units;
    }

    /**
     * Sets amount.
     *
     * @param units the number of units
     */
    public void setAmount(long units) {
        this.units = units;
    }

    public double getBuckets() {
        return (double) units / UNITS_PER_BUCKET;
    }

    /**
     * Split fluid stack into two stacks with the new stack containing up to |amount| fluid.
     *
     * @param amount the amount
     * @return the fluid stack
     */
    public FluidStack split(long amount) {
        long i = Math.min(amount, this.units);
        FluidStack stack = this.copy();
        stack.setAmount(i);
        this.decrement(i);
        return stack;
    }

    /**
     * Whether the stack is empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        } else if (this.getFluid() != Fluids.EMPTY) {
            return this.units <= 0;
        } else {
            return true;
        }
    }

    /**
     * Copy fluid stack.
     *
     * @return the fluid stack
     */
    public FluidStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new FluidStack(this.fluid, this.units, this.nbt);
        }
    }

    /**
     * Decrement amount.
     *
     * @param units the number of units
     */
    public void decrementAmount(long units) {
        this.units = Math.max(0, this.units - units);
    }

    private boolean isEqual(FluidStack stack) {
        if (this.units != stack.units) {
            return false;
        }
        if (this.fluid != stack.fluid) {
            return false;
        }
        if (this.nbt == null && stack.nbt != null) {
            return false;
        }
        return this.nbt == null || this.nbt.equals(stack.nbt);
    }

    public String toString() {
        return this.units + " " + Registry.FLUID.getId(this.getFluid()) + (nbt == null ? "" : nbt.toString());
    }

    /**
     * Increment.
     *
     * @param amount the amount
     */
    public void increment(long amount) {
        this.setAmount(this.units + amount);
    }

    /**
     * Decrement.
     *
     * @param amount the amount
     */
    public void decrement(long amount) {
        this.increment(-amount);
    }

    /**
     * Gets fluid.
     *
     * @return the fluid
     */
    public Fluid getFluid() {
        return fluid;
    }

    /**
     * Can increment boolean.
     *
     * @param amount the amount
     * @param cap    the cap
     * @return the boolean
     */
    public boolean canIncrement(long amount, long cap) {
        return this.units + amount <= cap;
    }


    /**
     * Store a fluid stack in a tag
     *
     * @param tag the tag
     * @return the compound tag
     */
    public NbtCompound writeNbt(NbtCompound tag) {
        if (this.fluid != Fluids.EMPTY)
            tag.putString("id", Registry.FLUID.getId(fluid).toString());
        tag.putLong("amount", this.units);
        if (nbt != null)
            tag.put("tag", nbt);
        return tag;
    }


    public List<Text> toTooltip(boolean multiline) {
        List<Text> tooltip = new ArrayList<>();
        for (FluidBlock block : FluidBlocks.getInstance().getFluidBlocks()) {
            FlowableFluid fluid = ((FluidBlockAccessor) block).getFluid();
            if (fluid == getFluid()) {
                if (multiline) {
                    MutableText line = Text.translatable(block.getTranslationKey());
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                    tooltip.add(line);
                    line = Text.literal(Formatter.formatNumber((double) getAmount() / UNITS_PER_BUCKET, "B "));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);

                    if (fluid instanceof FluidTooltipData)
                        ((FluidTooltipData) fluid).addTooltipInfo(this, tooltip);

                    if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                        Identifier id = Registry.FLUID.getId(fluid);
                        line = Text.literal(id.toString());
                        line = line.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                        tooltip.add(line);
                    }
                    tooltip.add(Formatter.getModNameToolTip(Registry.FLUID.getId(getFluid()).getNamespace()));
                } else {
                    MutableText line = Text.literal(Formatter.formatNumber((double) getAmount() / UNITS_PER_BUCKET, "B ")).append(Text.translatable(block.getTranslationKey()));
                    line = line.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                    tooltip.add(line);
                }
                break;
            }
        }
        return tooltip;
    }

    public void toPacket(PacketByteBuf packet) {
        Identifier fluid = Registry.FLUID.getId(getFluid());
        packet.writeIdentifier(fluid);
        packet.writeLong(getAmount());
        packet.writeBoolean(nbt != null);
        if (nbt != null)
            packet.writeNbt(nbt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FluidStack that = (FluidStack) o;

        if (units != that.units) return false;
        if (!fluid.equals(that.fluid)) return false;
        return Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        int result = fluid.hashCode();
        result = 31 * result + (int) (units ^ (units >>> 32));
        result = 31 * result + (nbt != null ? nbt.hashCode() : 0);
        return result;
    }

    public void setNbt(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    public NbtCompound getOrCreateNbt() {
        if (this.nbt == null) {
            this.setNbt(new NbtCompound());
        }
        return this.nbt;
    }


    public NbtCompound getOrCreateSubNbt(String key) {
        if (this.nbt == null || !this.nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = new NbtCompound();
            this.setSubNbt(key, nbtCompound);
            return nbtCompound;
        }
        return this.nbt.getCompound(key);
    }

    public void setSubNbt(String key, NbtElement element) {
        this.getOrCreateNbt().put(key, element);
    }
}
