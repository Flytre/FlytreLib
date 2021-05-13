package net.flytre.flytre_lib.common.util;

import net.minecraft.entity.player.PlayerEntity;

public class EnchantmentUtils {


    //DO NOT USE player's totalExperience variable
    public static int calculateTotalXp(PlayerEntity player) {
        return (int) (EnchantmentUtils.getExperienceFromLevel(player.experienceLevel) + (player.experienceProgress * player.getNextLevelExperience()));
    }

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

    public static int getExperienceFromLevel(int level) {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);

    }

    public static int getExperienceLevel(int targetXp) {
        int level = 0;
        while (true) {
            final int xpToNextLevel = nextLevelExperience(level);
            if (targetXp < xpToNextLevel) return level;
            level++;
            targetXp -= xpToNextLevel;
        }
    }
}
