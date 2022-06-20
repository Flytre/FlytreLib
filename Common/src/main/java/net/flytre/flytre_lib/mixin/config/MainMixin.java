package net.flytre.flytre_lib.mixin.config;


import com.google.gson.GsonBuilder;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.impl.config.auth.MicrosoftAuthenticationUtils;
import net.flytre.flytre_lib.impl.config.auth.MinecraftAccountInfo;
import net.flytre.flytre_lib.impl.config.init.FlytreLibConfig;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.client.main.Main;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin(value = Main.class)
class MainMixin {


    @Unique
    private static MinecraftAccountInfo accountInfo;

    @Unique
    private static boolean successful = false;


    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$login(String[] args, CallbackInfo ci) {
        LoaderProperties.HANDLER = new ConfigHandler<>(new FlytreLibConfig(), "flytre_lib", new GsonBuilder().setPrettyPrinting().create());
        ConfigRegistry.registerClientConfig(LoaderProperties.HANDLER);


        if (!LoaderProperties.HANDLER.getConfig().login.shouldLogin) {
            return;
        }


        try {
            accountInfo = MicrosoftAuthenticationUtils.login(
                    LoaderProperties.HANDLER.getConfig().login.username,
                    LoaderProperties.HANDLER.getConfig().login.password
            );
            successful = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "main", at = @At(value = "NEW", target = "net/minecraft/client/util/Session"))
    private static Session flytre_lib$auth_me(String username, String uuid2, String accessToken, Optional<String> xuid, Optional<String> clientId, Session.AccountType accountType) {

        if (successful)
            return new Session(accountInfo.name(), accountInfo.uuid(),
                    accountInfo.token(), Optional.empty(), Optional.empty(),
                    Session.AccountType.MOJANG);
        else
            return new Session(username, uuid2, accessToken, xuid, clientId, accountType);
    }


}
