package net.flytre.flytre_lib.mixin.event;


import net.flytre.flytre_lib.impl.event.LootContainer;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootTable.Builder.class)
public class LootTable$BuilderMixin implements LootContainer {
    @Shadow @Final private List<LootPool> pools;

    @Shadow @Final private List<LootFunction> functions;

    @Shadow private LootContextType type;

    @Override
    public List<LootPool> flytre_lib$getPools() {
        return pools;
    }

    @Override
    public List<LootFunction> flytre_lib$getFunctions() {
        return functions;
    }

    @Override
    public LootContextType flytre_lib$getType() {
        return type;
    }

}
