package net.flytre.flytre_lib.common.inventory.filter;

import net.minecraft.util.Identifier;

public interface Filtered {

    Identifier BLOCK_FILTER_MODE = new Identifier("flytre_lib", "filter_mode");
    Identifier BLOCK_NBT_MATCH = new Identifier("flytre_lib", "nbt_match");
    Identifier BLOCK_MOD_MATCH = new Identifier("flytre_lib", "mod_match");


    FilterInventory getFilter();
}
