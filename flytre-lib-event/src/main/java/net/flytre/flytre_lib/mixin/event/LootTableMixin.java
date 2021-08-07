package net.flytre.flytre_lib.mixin.event;


import net.flytre.flytre_lib.impl.event.LootContainer;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements LootContainer {
    @Shadow @Final LootPool[] pools;

    @Shadow @Final LootFunction[] functions;

    @Shadow @Final private LootContextType type;

    @Override
    public List<LootPool> getPools() {
        return Arrays.asList(pools);
    }

    @Override
    public List<LootFunction> getFunctions() {
        return Arrays.asList(functions);
    }

    @Override
    public LootContextType getType() {
        return type;
    }
}
