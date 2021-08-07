package net.flytre.flytre_lib.api.storage.recipe;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EnchantmentOutputProvider extends OutputProvider {

    private static final Random RANDOM = new Random();
    private final int level;
    private final boolean treasure;

    public EnchantmentOutputProvider(ItemStack stack, double chance, int level, boolean treasure) {
        super(stack, chance);
        this.level = level;
        this.treasure = treasure;
    }

    public EnchantmentOutputProvider(TaggedItem item, double chance, int level, boolean treasure) {
        super(item, chance);
        this.level = level;
        this.treasure = treasure;
    }


    @Override
    public ItemStack getStack() {
        ItemStack stack = super.getStack();
        if (!stack.hasEnchantments())
            EnchantmentHelper.enchant(RANDOM, stack, level, treasure);
        return stack;
    }
}
