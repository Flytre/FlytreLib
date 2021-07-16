package net.flytre.flytre_lib.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ReloadConfigCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {


        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager
                .literal("reloadconfig")
                .executes(i -> {
                    int x = ConfigRegistry.reloadServerConfigs(i.getSource().getServer().getPlayerManager());
                    i.getSource().sendFeedback(new LiteralText("Reloaded " + x + " configs."), true);
                    return x;
                });

        dispatcher.register(command);
    }

}
