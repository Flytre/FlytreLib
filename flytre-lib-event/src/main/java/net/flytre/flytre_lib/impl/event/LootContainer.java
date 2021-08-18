package net.flytre.flytre_lib.impl.event;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public interface LootContainer {

    List<LootPool> flytre_lib$getPools();

    List<LootFunction> flytre_lib$getFunctions();

    LootContextType flytre_lib$getType();
}
