package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.api.base.registry.ParticleManagerRegistry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin implements ParticleManagerRegistry {

    @Shadow protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleManager.SpriteAwareFactory<T> factory);

    @Override
    public <T extends ParticleEffect> void altRegister(ParticleType<T> type, ParticleManagerRegistry.SpriteAwareFactory<T> factory) {
        registerFactory(type, factory::create);
    }
}
