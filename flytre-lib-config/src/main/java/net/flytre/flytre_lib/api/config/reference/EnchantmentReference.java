package net.flytre.flytre_lib.api.config.reference;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class EnchantmentReference extends Reference<Enchantment> {

    public EnchantmentReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public EnchantmentReference(@NotNull Enchantment value) {
        super(value, Registry.ENCHANTMENT);
    }


    public EnchantmentReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable Enchantment getValue(World world) {
        return getValue(Registry.ENCHANTMENT_KEY, world);
    }


}
