package net.flytre.flytre_lib.api.base.registry;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 * Implemented by ParticleManager
 */
public interface ParticleManagerRegistry {


    <T extends ParticleEffect> void altRegister(ParticleType<T> type, SpriteAwareFactory<T> factory);


    interface SpriteAwareFactory<T extends ParticleEffect> {
        ParticleFactory<T> create(SpriteProvider spriteProvider);
    }
}
