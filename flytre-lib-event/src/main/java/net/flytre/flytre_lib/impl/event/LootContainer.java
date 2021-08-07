package net.flytre.flytre_lib.impl.event;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public interface LootContainer {

    List<LootPool> getPools();

    List<LootFunction> getFunctions();

    LootContextType getType();
}
