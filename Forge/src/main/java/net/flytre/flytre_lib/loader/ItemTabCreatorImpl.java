package net.flytre.flytre_lib.loader;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

final class ItemTabCreatorImpl implements ItemTabCreator.Delegate {


    private ItemTabCreatorImpl() {

    }

    public static void init() {
        ItemTabCreator.setDelegate(new ItemTabCreatorImpl());
    }

    @Override
    public ItemGroup create(Identifier name, Supplier<ItemStack> icon) {
        return new ItemGroup(String.format("%s.%s", name.getNamespace(), name.getPath())) {
            @Override
            @Nonnull
            public ItemStack createIcon() {
                return icon.get();
            }
        };
    }
}
