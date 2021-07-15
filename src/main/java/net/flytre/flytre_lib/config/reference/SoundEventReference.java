package net.flytre.flytre_lib.config.reference;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SoundEventReference extends Reference<SoundEvent> {

    public SoundEventReference(@NotNull Identifier identifier) {
        super(identifier);
    }

    public SoundEventReference(@NotNull SoundEvent value) {
        super(value, Registry.SOUND_EVENT);
    }


    public SoundEventReference(String namespace, String path) {
        super(namespace, path);
    }

    @Override
    public @Nullable SoundEvent getValue(World world) {
        return getValue(Registry.SOUND_EVENT_KEY, world);
    }

    @Override
    public boolean isIn(Collection<? extends Reference<SoundEvent>> references) {
        return references.contains(this);
    }
}
