package net.flytre.flytre_lib.mixin.storage.fluid;

import com.mojang.authlib.GameProfile;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;
import net.flytre.flytre_lib.impl.storage.fluid.gui.FluidHandlerSyncHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Attaches a fluid sync handler to the player
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private FluidHandlerSyncHandler fluidHandlerSyncHandler;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }


    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$listeners(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci) {
        fluidHandlerSyncHandler = new FluidHandlerSyncHandler.Impl((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "onScreenHandlerOpened(Lnet/minecraft/screen/ScreenHandler;)V", at = @At("TAIL"))
    public void flytre_lib$onScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo ci) {
        if (screenHandler instanceof FluidHandler)
            ((FluidHandler) screenHandler).updateSyncHandler(this.fluidHandlerSyncHandler);

    }
}
