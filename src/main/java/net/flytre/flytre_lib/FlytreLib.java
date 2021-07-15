package net.flytre.flytre_lib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.common.inventory.filter.Filtered;
import net.flytre.flytre_lib.config.ReloadConfigCommand;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlytreLib implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Flytre Lib");


    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> ReloadConfigCommand.register(dispatcher));
        filterPackets();
    }

    //INITIALIZE BLOCK ENTITY FILTER PACKETS
    private void filterPackets() {
        ServerPlayNetworking.registerGlobalReceiver(Filtered.BLOCK_FILTER_MODE, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof Filtered)
                    ((Filtered) entity).getFilter().setFilterType(newMode);
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(Filtered.BLOCK_NBT_MATCH, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof Filtered)
                    ((Filtered) entity).getFilter().setMatchNbt(newMode == 1);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(Filtered.BLOCK_MOD_MATCH, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof Filtered)
                    ((Filtered) entity).getFilter().setMatchMod(newMode == 1);
            });
        });

    }
}
