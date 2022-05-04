package net.flytre.flytre_lib.loader;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;

class LootRegistryImpl implements LootRegistry.Delegate {

    private LootRegistryImpl() {
    }

    public static void init() {
        LootRegistry.setDelegate(new LootRegistryImpl());
    }


    @Override
    public LootConditionType register(JsonSerializer<? extends LootCondition> serializer, String mod, String id) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(mod, id), new LootConditionType(serializer));
    }
}
