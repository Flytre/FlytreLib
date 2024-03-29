package net.flytre.flytre_lib.api.base.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class to help with enchanting and experience
 */
public final class EnchantmentUtils {


    private EnchantmentUtils() {
        throw new AssertionError();
    }

    /**
     * Calculate the amount of xp the player has. The totalExperience variable is inaccurate
     */
    public static int calculateTotalXp(PlayerEntity player) {
        return (int) (EnchantmentUtils.getExperienceFromLevel(player.experienceLevel) + (player.experienceProgress * player.getNextLevelExperience()));
    }

    /**
     * Calculate the amount of xp needed to move from [level] to [level + 1]
     */
    public static int nextLevelExperience(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;

        if (level >= 15)
            return 37 + (level - 15) * 5;

        return 7 + level * 2;
    }

    /**
     * Arithmetic sum of first n elements
     * Experience formula
     *
     * @param n  the number of terms to sum
     * @param a0 the base value of each term in the series
     * @param d  the common difference
     * @return a0 * n (base amount of experience for each level) + d * sum(i=1, n, i) (the total amount of experience added from the experience increase from each level)
     */
    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    /**
     * Gets the amount of experience needed to go from [0] to [level]
     */
    public static int getExperienceFromLevel(int level) {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);

    }

    /**
     * Gets the level of the player with [targetXp] xp
     */
    public static int getExperienceLevel(int targetXp) {
        int level = 0;
        while (true) {
            final int xpToNextLevel = nextLevelExperience(level);
            if (targetXp < xpToNextLevel) return level;
            level++;
            targetXp -= xpToNextLevel;
        }
    }

    /**
     * Apply an operation to the enchantment map
     */
    public static void modifyEnchants(ItemStack stack, Consumer<Map<Enchantment, Integer>> modifier) {
        Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
        modifier.accept(map);
        EnchantmentHelper.set(map, stack);
    }
}
