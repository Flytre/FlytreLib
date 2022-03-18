package net.flytre.flytre_lib.loader;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

final class ItemTabCreatorImpl implements ItemTabCreator.Delegate {


    private ItemTabCreatorImpl() {

    }

    public static void init() {
        ItemTabCreator.setDelegate(new ItemTabCreatorImpl());
    }

    @Override
    public ItemGroup create(Identifier name, Supplier<ItemStack> icon) {
        return FabricItemGroupBuilder.build(name, icon);
    }
}
