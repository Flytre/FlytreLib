package net.flytre.flytre_lib.impl.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ReloadConfigCommand {
    private ReloadConfigCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        @SuppressWarnings("SpellCheckingInspection") LiteralArgumentBuilder<ServerCommandSource> command = CommandManager
                .literal("reloadconfig")
                .executes(i -> {
                    int x = ConfigRegistry.reloadServerConfigs(i.getSource().getServer().getPlayerManager());
                    i.getSource().sendFeedback(Text.literal("Reloaded " + x + " configs."), true);
                    return x;
                });

        dispatcher.register(command);
    }

}
