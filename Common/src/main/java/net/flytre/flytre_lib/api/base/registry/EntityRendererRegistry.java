package net.flytre.flytre_lib.api.base.registry;

import net.flytre.flytre_lib.mixin.base.EntityRenderersInvoker;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public final class EntityRendererRegistry {

    private EntityRendererRegistry() {
    }

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        EntityRenderersInvoker.flytre_lib$register(type, factory);
    }
}
