package net.flytre.flytre_lib.loader;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;

/**
 * Used to register anything related to custom loot tables
 */
public final class LootRegistry {

    private LootRegistry() {
        throw new AssertionError();
    }

    public static LootConditionType register(JsonSerializer<? extends LootCondition> serializer, String mod, String id) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(mod, id), new LootConditionType(serializer));
    }
}
