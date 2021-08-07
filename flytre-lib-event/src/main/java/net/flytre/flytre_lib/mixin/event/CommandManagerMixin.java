package net.flytre.flytre_lib.mixin.event;

import com.mojang.brigadier.CommandDispatcher;
import net.flytre.flytre_lib.api.event.CommandRegistrationEvent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;

    /**
     * Requires Fabric fork of mixin to run; Can replace with TAIL, but will calculate after ambiguities
     */
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
    private void flytre_lib$registerCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        CommandRegistrationEvent.EVENT.getListeners().forEach(i -> i.onCommandsRegistered(this.dispatcher, environment == CommandManager.RegistrationEnvironment.DEDICATED));
    }
}
