package net.flytre.flytre_lib.mixin.base;


import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Properties;

/**
 * Automatically set online mode to false if in dev environment
 */
@Mixin(ServerPropertiesHandler.class)
abstract class ServerPropertiesHandlerMixin extends AbstractPropertiesHandler<ServerPropertiesHandler> {

    public ServerPropertiesHandlerMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean parseBoolean(String key, boolean fallback) {
        return super.parseBoolean(key, key.equals("online-mode") ? !LoaderProperties.isDevEnvironment() : fallback);
    }
}
