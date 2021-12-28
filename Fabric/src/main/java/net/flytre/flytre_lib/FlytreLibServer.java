package net.flytre.flytre_lib;

import net.fabricmc.api.DedicatedServerModInitializer;

public class FlytreLibServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {

//        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
//
//            if (server.isDedicatedServer()) {
//
//                RegistryHelper<Attribute> registry = new AttributeRegistryHelper();
//                AttributeConfig.load(FabricLoader.getInstance().getConfigDir().resolve(FlytreLibConstants.MOD_ID + ".json").toFile(), registry).applyChanges(registry);
//            }
//        });
    }
}