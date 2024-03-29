package net.flytre.flytre_lib.loader;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * Used to create new creative item tabs
 */
public final class ItemTabCreator {

    private static Delegate DELEGATE;

    private ItemTabCreator() {
        throw new AssertionError();
    }

    /**
     * Creates a new creative inventory tab
     *
     * @param name an identifier pointing to the name of the tab
     * @param icon the item to use as the icon
     * @return a reference to the tab
     */
    public static ItemGroup create(Identifier name, Supplier<ItemStack> icon) {
        return DELEGATE.create(name, icon);
    }

    static void setDelegate(Delegate delegate) {
        ItemTabCreator.DELEGATE = delegate;
    }

    interface Delegate {
        ItemGroup create(Identifier name, Supplier<ItemStack> icon);
    }
}