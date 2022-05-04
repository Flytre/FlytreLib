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

    private static Delegate DELEGATE;

    static void setDelegate(Delegate DELEGATE) {
        LootRegistry.DELEGATE = DELEGATE;
    }

    public static LootConditionType register(JsonSerializer<? extends LootCondition> serializer, String mod, String id) {

        return Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(mod, id), new LootConditionType(serializer));

    }


    interface Delegate {
        LootConditionType register(JsonSerializer<? extends LootCondition> serializer, String mod, String id);
    }
}
