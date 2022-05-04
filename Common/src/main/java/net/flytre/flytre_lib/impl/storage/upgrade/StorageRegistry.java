package net.flytre.flytre_lib.impl.storage.upgrade;

import net.flytre.flytre_lib.loader.LootRegistry;
import net.minecraft.loot.condition.LootConditionType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class StorageRegistry {



    public static final LootConditionType HAS_UPGRADE_CONDITION = LootRegistry.register(new HasUpgradeLootCondition.Serializer(), "flytre_lib", "has_upgrade");

    private StorageRegistry() {
    }


    public static void init() {

    }
}
