package net.flytre.flytre_lib.mixin.config;


import com.mojang.authlib.Agent;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.impl.config.init.ClientConfigInitializer;
import net.minecraft.client.main.Main;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Mixin(Main.class)
public class MainMixin {


    @Unique
    private static String name;
    @Unique
    private static String uuid;
    @Unique
    private static String token;
    @Unique
    private static String type;
    private static boolean successful = false;


    @Inject(method = "main", at = @At("HEAD"))
    private static void flytre_lib$login(String[] args, CallbackInfo ci) {

        ConfigRegistry.registerClientConfig(ClientConfigInitializer.HANDLER);


        if (!ClientConfigInitializer.HANDLER.getConfig().login.shouldLogin) {
            return;
        }


        YggdrasilUserAuthentication userAuth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY,
                UUID.randomUUID().toString()).createUserAuthentication(Agent.MINECRAFT);
        userAuth.setUsername(ClientConfigInitializer.HANDLER.getConfig().login.username);
        userAuth.setPassword(ClientConfigInitializer.HANDLER.getConfig().login.password);
        try {
            userAuth.logIn();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return;
        }

        successful = true;

        name = userAuth.getSelectedProfile().getName();
        uuid = UUIDTypeAdapter.fromUUID(userAuth.getSelectedProfile().getId());
        token = userAuth.getAuthenticatedToken();
        type = userAuth.getUserType().getName();
    }

    @Redirect(method = "main", at = @At(value = "NEW", target = "net/minecraft/client/util/Session"))
    private static Session flytre_lib$auth_me(String username, String uuid2, String accessToken, Optional<String> xuid, Optional<String> clientId, Session.AccountType accountType) {

        //TODO: TEST 1.18
        if (successful)
            return new Session(name, uuid, token, Optional.empty(), Optional.empty(), Objects.equals(type, UserType.LEGACY.getName()) ? Session.AccountType.LEGACY : Session.AccountType.MOJANG);
        else
            return new Session(username, uuid2, accessToken, xuid, clientId, accountType);
    }


}
