package net.flytre.flytre_lib.api.event;

import com.mojang.brigadier.CommandDispatcher;
import net.flytre.flytre_lib.impl.event.EventImpl;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface CommandRegistrationEvent {

    Event<CommandRegistrationEvent> EVENT = EventImpl.create();

    void onCommandsRegistered(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated);

}
