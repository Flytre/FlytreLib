package net.flytre.flytre_lib.mixin.event;


import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.flytre.flytre_lib.api.event.LootProcessingEvent;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Shadow
    private Map<Identifier, LootTable> tables;

    @Inject(method = "apply*", at = @At("RETURN"))
    private void flytre_lib$modify(Map<Identifier, JsonObject> objectMap, ResourceManager manager, Profiler profiler, CallbackInfo info) {
        Map<Identifier, LootTable> modifiedTables = new HashMap<>();

        tables.forEach((id, table) -> {
            LootTable.Builder builder = LootProcessingEvent.copyFrom(table);
            LootProcessingEvent.EVENT.getListeners().forEach(processor -> processor.onLootTablesProcessed(manager, (LootManager) (Object) this, id, builder, (mt) -> modifiedTables.put(id, mt)));
            modifiedTables.computeIfAbsent(id, (__) -> builder.build());
        });

        tables = ImmutableMap.copyOf(modifiedTables);

    }
}
