package net.flytre.flytre_lib.api.config.reference.item;

import net.flytre.flytre_lib.api.config.reference.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemReference extends Reference<Item> implements ConfigItem {

    public ItemReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public ItemReference(@NotNull Item value) {
        super(value, Registry.ITEM);
    }


    public ItemReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable Item getValue(World world) {
        return getValue(Registry.ITEM_KEY, world);
    }


}
