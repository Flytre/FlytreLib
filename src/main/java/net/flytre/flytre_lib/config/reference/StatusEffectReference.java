package net.flytre.flytre_lib.config.reference;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class StatusEffectReference extends Reference<StatusEffect> {

    public StatusEffectReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public StatusEffectReference(@NotNull StatusEffect value) {
        super(value, Registry.STATUS_EFFECT);
    }


    public StatusEffectReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable StatusEffect getValue(World world) {
        return getValue(Registry.MOB_EFFECT_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<StatusEffect>> references) {
        return references.contains(this);
    }
}
