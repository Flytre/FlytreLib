package net.flytre.flytre_lib.loader.registry;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface EntityRendererRegisterer {

    <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory);
}
