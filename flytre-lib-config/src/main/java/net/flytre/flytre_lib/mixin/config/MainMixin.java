package net.flytre.flytre_lib.mixin.config;


import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import net.flytre.flytre_lib.impl.config.init.ClientConfigInitializer;
import net.flytre.flytre_lib.impl.config.init.ConfigInitializer;
import net.minecraft.client.main.Main;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
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

    static {
        ClientConfigInitializer.HANDLER.handle();
    }

    @Inject(method = "main", at = @At("HEAD"))
    private static void hoco_sg$login(String[] args, CallbackInfo ci) {


        if(!ClientConfigInitializer.HANDLER.getConfig().login.shouldLogin) {
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
    private static Session hoco_sg$auth_me(String username, String uuid2, String accessToken, String accountType) {

        if (successful)
            return new Session(name, uuid, token, type);
        else
            return new Session(username, uuid2, accessToken, accountType);
    }


}
