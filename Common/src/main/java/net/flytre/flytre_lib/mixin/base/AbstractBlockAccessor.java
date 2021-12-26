package net.flytre.flytre_lib.mixin.base;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor {

    @Accessor("resistance")
    float getResistance();


    @Accessor("material")
    Material getMaterial();
}
