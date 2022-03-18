package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.api.base.registry.EntityAttributeRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class LoaderEvents {

    private LoaderEvents() {
    }

    public static void preInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            EntityAttributeSetterImpl.getEntityAttributes().forEach(i -> EntityAttributeRegistry.register(i.entityType(), i.attributes().get()));
        });
    }
}
