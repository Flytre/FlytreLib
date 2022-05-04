package net.flytre.flytre_lib.mixin.storage;


import net.flytre.flytre_lib.impl.storage.upgrade.StorageRegistry;
import net.minecraft.loot.condition.LootConditionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootConditionTypes.class)
public class LootConditionTypesMixin {


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void flytre_lib$registerLootCondition(CallbackInfo ci) {
        StorageRegistry.init();
    }
}
