package net.flytre.flytre_lib.api.event;

import net.flytre.flytre_lib.impl.event.EventImpl;
import net.flytre.flytre_lib.impl.event.LootContainer;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootProcessingEvent {


    Event<LootProcessingEvent> EVENT = EventImpl.create();


    @SuppressWarnings("ConstantConditions")
    static LootTable.Builder copyFrom(LootTable table) {
        LootTable.Builder builder = new LootTable.Builder();
        LootContainer tableAccessor = (LootContainer) table;
        LootContainer builderAccessor = (LootContainer) builder;

        builderAccessor.getPools().addAll(tableAccessor.getPools());
        builderAccessor.getFunctions().addAll(tableAccessor.getFunctions());

        builder.type(tableAccessor.getType());

        return builder;
    }

    void onLootTablesProcessed(ResourceManager resourceManager, LootManager manager, Identifier id, LootTable.Builder builder, LootTableSetter setter);


    /**
     * Used to set the specified table to something totally different, rather than modifying the builder
     */
    @FunctionalInterface
    interface LootTableSetter {
        void set(LootTable table);
    }

}
