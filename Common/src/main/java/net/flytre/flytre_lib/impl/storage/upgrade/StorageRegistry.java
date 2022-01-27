package net.flytre.flytre_lib.impl.storage.upgrade;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.loot.condition.LootConditionType;

public class StorageRegistry {


    public static LootConditionType HAS_UPGRADE_CONDITION = LoaderProperties.register(new HasUpgradeLootCondition.Serializer(), "flytre_lib", "has_upgrade");


    public static void init() {

    }
}
