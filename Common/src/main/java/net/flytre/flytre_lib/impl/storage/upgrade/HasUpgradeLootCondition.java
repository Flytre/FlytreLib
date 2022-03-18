package net.flytre.flytre_lib.impl.storage.upgrade;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class HasUpgradeLootCondition implements LootCondition {

    static final HasUpgradeLootCondition INSTANCE = new HasUpgradeLootCondition();

    private HasUpgradeLootCondition() {
    }


    @Override
    public LootConditionType getType() {
        return StorageRegistry.HAS_UPGRADE_CONDITION;
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockEntity entity = lootContext.get(LootContextParameters.BLOCK_ENTITY);
        if (entity instanceof UpgradeInventory upgradeInventory) {
            return !upgradeInventory.hasNoUpgrades();
        }
        return false;
    }

    public static class Serializer
            implements JsonSerializer<HasUpgradeLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, HasUpgradeLootCondition arg, JsonSerializationContext jsonSerializationContext) {
        }

        @Override
        public HasUpgradeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

    }


}
