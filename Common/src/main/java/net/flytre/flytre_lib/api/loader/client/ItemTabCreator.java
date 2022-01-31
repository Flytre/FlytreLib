package net.flytre.flytre_lib.api.loader.client;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class ItemTabCreator {

    private static ItemTabCreatorDelegate DELEGATE;

    private ItemTabCreator() {

    }

    public static ItemGroup create(Identifier name, Supplier<ItemStack> icon) {
        return DELEGATE.create(name, icon);
    }

    public static void setDelegate(ItemTabCreatorDelegate delegate) {
        ItemTabCreator.DELEGATE = delegate;
    }

    public interface ItemTabCreatorDelegate {
        ItemGroup create(Identifier name, Supplier<ItemStack> icon);
    }
}
