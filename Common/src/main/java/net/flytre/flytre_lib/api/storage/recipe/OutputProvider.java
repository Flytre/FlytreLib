package net.flytre.flytre_lib.api.storage.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

/**
 * An OutputProvider provides the output of a slot of a recipe. OutputProviders can provide
 * items, item stacks, items from tags, and quantified
 * Instantiate using static factories
 */

public abstract class OutputProvider {

    protected final double chance;

    private OutputProvider(double chance) {
        this.chance = chance;
    }

    public static OutputProvider fromPacket(PacketByteBuf buf) {
        Types type = buf.readEnumConstant(Types.class);
        if (type == Types.ENCHANT)
            return EnchantmentOutputProvider.fromPacket(buf);
        else if (type == Types.TAG)
            return TagOutputProvider.fromPacket(buf);
        else if (type == Types.STACK)
            return StackOutputProvider.fromPacket(buf);
        throw new IllegalArgumentException("Invalid packet type");
    }

    public static OutputProvider fromJson(JsonElement jsonElement) {
        JsonObject json = null;
        if (jsonElement.isJsonObject())
            json = (JsonObject) jsonElement;


        //If tag
        if (JsonHelper.hasString(json, "tag")) {
            Identifier id = new Identifier(JsonHelper.getString(json, "tag"));
            int i = JsonHelper.getInt(json, "count", 1);
            double chance = JsonHelper.getFloat(json, "chance", 1);
            return new TagOutputProvider(new TaggedItem(id, i), chance);
        }

        //If item
        if (jsonElement.isJsonPrimitive()) {
            String id = jsonElement.getAsString();
            Identifier identifier2 = new Identifier(id);
            ItemStack itemStack = Registry.ITEM.getOrEmpty(identifier2).map(ItemStack::new).orElse(ItemStack.EMPTY);
            return new StackOutputProvider(itemStack, 1.0);
        }

        assert json != null;
        String string = JsonHelper.getString(json, "item");
        Item item = Registry.ITEM.getOrEmpty(new Identifier(string)).orElse(Items.AIR);
        int i = JsonHelper.getInt(json, "count", 1);
        double chance = JsonHelper.getFloat(json, "chance", 1);

        //If enchantment data
        if (JsonHelper.hasNumber(json, "level")) {
            int level = JsonHelper.getInt(json, "level");
            boolean treasure = JsonHelper.hasBoolean(json, "treasure") && JsonHelper.getBoolean(json, "treasure");
            return new EnchantmentOutputProvider(new ItemStack(item, i), chance, level, treasure);
        }

        return new StackOutputProvider(new ItemStack(item, i), chance);
    }

    public static OutputProvider from(ItemStack stack, double chance) {
        return new StackOutputProvider(stack, chance);
    }

    public static OutputProvider from(TaggedItem item, double chance) {
        return new TagOutputProvider(item, chance);
    }

    public static OutputProvider from(ItemStack stack, double chance, int level, boolean treasure) {
        return new EnchantmentOutputProvider(stack, chance, level, treasure);
    }

    abstract ItemStack getStack();

    public double getChance() {
        return chance;
    }

    public void toPacket(PacketByteBuf buf) {

        if (this instanceof EnchantmentOutputProvider)
            buf.writeEnumConstant(Types.ENCHANT);
        if (this instanceof StackOutputProvider)
            buf.writeEnumConstant(Types.STACK);
        else if (this instanceof TagOutputProvider)
            buf.writeEnumConstant(Types.TAG);
        else
            throw new IllegalStateException();

        toPacketImpl(buf);
    }

    protected abstract void toPacketImpl(PacketByteBuf buf);


    private enum Types {
        STACK,
        TAG,
        ENCHANT
    }

    private static class StackOutputProvider extends OutputProvider {
        protected final ItemStack stack;

        public StackOutputProvider(ItemStack stack, double chance) {
            super(chance);
            this.stack = stack;
        }

        public static OutputProvider fromPacket(PacketByteBuf buf) {
            return new StackOutputProvider(buf.readItemStack(), buf.readDouble());
        }

        @Override
        ItemStack getStack() {
            return stack;
        }

        @Override
        public void toPacketImpl(PacketByteBuf buf) {
            buf.writeItemStack(stack);
            buf.writeDouble(chance);
        }

        @Override
        public String toString() {
            return "StackOutputProvider{" +
                    "chance=" + chance +
                    ", stack=" + stack +
                    '}';
        }
    }

    private static class TagOutputProvider extends OutputProvider {
        private final TaggedItem taggedItem;

        public TagOutputProvider(TaggedItem item, double chance) {
            super(chance);
            this.taggedItem = item;
        }

        public static OutputProvider fromPacket(PacketByteBuf buf) {
            TaggedItem item = new TaggedItem(buf.readIdentifier(), buf.readInt());
            return new TagOutputProvider(item, buf.readDouble());
        }

        @Override
        ItemStack getStack() {
            return taggedItem.getItemStack();
        }

        @Override
        public void toPacketImpl(PacketByteBuf buf) {
            buf.writeIdentifier(taggedItem.getPath());
            buf.writeInt(taggedItem.getQty());
            buf.writeDouble(chance);
        }

        @Override
        public String toString() {
            return "TagOutputProvider{" +
                    "chance=" + chance +
                    ", taggedItem=" + taggedItem +
                    '}';
        }
    }

    private static class EnchantmentOutputProvider extends StackOutputProvider {
        private static final Random RANDOM = Random.create();
        private final int level;
        private final boolean treasure;

        public EnchantmentOutputProvider(ItemStack stack, double chance, int level, boolean treasure) {
            super(stack, chance);
            this.level = level;
            this.treasure = treasure;
        }

        public static OutputProvider fromPacket(PacketByteBuf buf) {
            ItemStack stack = buf.readItemStack();
            double chance = buf.readDouble();
            int level = buf.readInt();
            boolean treasure = buf.readBoolean();
            return new EnchantmentOutputProvider(stack, chance, level, treasure);
        }

        @Override
        public ItemStack getStack() {
            ItemStack stack = super.getStack();
            if (!stack.hasEnchantments())
                EnchantmentHelper.enchant(RANDOM, stack, level, treasure);
            return stack;
        }

        @Override
        public void toPacketImpl(PacketByteBuf buf) {
            super.toPacketImpl(buf);
            buf.writeInt(level);
            buf.writeBoolean(treasure);
        }

        @Override
        public String toString() {
            return "EnchantmentOutputProvider{" +
                    "chance=" + chance +
                    ", stack=" + stack +
                    ", level=" + level +
                    ", treasure=" + treasure +
                    '}';
        }
    }
}
