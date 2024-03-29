package net.flytre.flytre_lib.mixin.storage.upgrade;


import com.mojang.authlib.GameProfile;
import net.flytre.flytre_lib.api.storage.upgrade.UpgradeHandler;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerListener;
import net.flytre.flytre_lib.impl.storage.upgrade.gui.UpgradeHandlerSyncHandler;
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
 * Attaches an upgrade sync handler to the player
 */
@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity implements UpgradeHandlerListener {


    @Unique
    private UpgradeHandlerSyncHandler upgradeHandlerSyncHandler;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void flytre_lib$listeners(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci) {
        upgradeHandlerSyncHandler = new UpgradeHandlerSyncHandler.Impl((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "onScreenHandlerOpened(Lnet/minecraft/screen/ScreenHandler;)V", at = @At("TAIL"))
    public void flytre_lib$onScreenHandlerOpened(ScreenHandler screenHandler, CallbackInfo ci) {
        if (screenHandler instanceof UpgradeHandler)
            ((UpgradeHandler) screenHandler).updateSyncHandler(this.upgradeHandlerSyncHandler);

    }
}
