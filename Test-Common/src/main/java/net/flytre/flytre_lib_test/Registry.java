package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.loader.BlockEntityFactory;
import net.flytre.flytre_lib.loader.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.loader.ItemTabCreator;
import net.flytre.flytre_lib.loader.LoaderAgnosticRegistry;
import net.flytre.flytre_lib_test.network.PipeModeC2SPacket;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class Registry {


    public static final ConfigHandler<Config> PIPE_CONFIG = new ConfigHandler<>(new Config(), "pipe");
    public static Block ITEM_PIPE;
    public static final ItemGroup TAB = ItemTabCreator.create(
            new Identifier("pipe", "all"),
            () -> new ItemStack(ITEM_PIPE));
    public static Block FAST_PIPE = registerBlock(new PipeBlock(AbstractBlock.Settings.of(Material.METAL).hardness(0.6f)), "fast_pipe");
    public static BlockEntityType<PipeEntity> ITEM_PIPE_BLOCK_ENTITY;
    public static ScreenHandlerType<PipeHandler> ITEM_PIPE_SCREEN_HANDLER;

    static {
        ITEM_PIPE = registerBlock(new PipeBlock(AbstractBlock.Settings.of(Material.METAL).hardness(0.9f)), "item_pipe");
    }

    private Registry() {
    }

    public static <T extends Block> T registerBlock(T block, String id) {
        var tmp = LoaderAgnosticRegistry.register(block, Constants.MOD_ID, id);
        LoaderAgnosticRegistry.register(new BlockItem(block, new Item.Settings().group(TAB)), Constants.MOD_ID, id);
        return tmp;
    }


    public static void init() {
        ITEM_PIPE_BLOCK_ENTITY = LoaderAgnosticRegistry.register(BlockEntityFactory.createBuilder(PipeEntity::new, ITEM_PIPE, FAST_PIPE).build(null), "pipe", "item_pipe");
        ItemRegistry.init();
        ITEM_PIPE_SCREEN_HANDLER = LoaderAgnosticRegistry.register((ExtendedScreenHandlerFactory<PipeHandler>) PipeHandler::new, "pipe", "item_pipe");

        PacketUtils.registerC2SPacket(PipeModeC2SPacket.class, PipeModeC2SPacket::new);
        ConfigRegistry.registerServerConfig(PIPE_CONFIG);
    }


}
