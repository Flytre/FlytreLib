package net.flytre.flytre_lib.impl.storage.upgrade;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageRegistry {


    public static LootConditionType HAS_UPGRADE_CONDITION = LoaderProperties.register(new HasUpgradeLootCondition.Serializer(),"flytre_lib","has_upgrade");


    public static void init() {

    }
}
