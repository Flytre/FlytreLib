package net.flytre.flytre_lib.api.loader;

import net.flytre.flytre_lib.impl.loader.LoaderPropertyInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.registries.DeferredRegister;

public class RegistryReceiver {


    public static DeferredRegister<Block> getBlockDeferredRegister(String mod) {
        return LoaderPropertyInitializer.BLOCK_REGISTRIES.get(mod);
    }

    public static DeferredRegister<Item> getItemDeferredRegister(String mod) {
        return LoaderPropertyInitializer.ITEM_REGISTRIES.get(mod);
    }

    public static DeferredRegister<EntityType<?>> getEntityDeferredRegister(String mod) {
        return LoaderPropertyInitializer.ENTITY_REGISTRIES.get(mod);
    }

    public static DeferredRegister<ScreenHandlerType<?>> getScreenHandlerDeferredRegister(String mod) {
        return LoaderPropertyInitializer.SCREEN_HANDLER_REGISTRIES.get(mod);
    }

    public static DeferredRegister<BlockEntityType<?>> getBlockEntityDeferredRegister(String mod) {
        return LoaderPropertyInitializer.BLOCK_ENTITY_REGISTRIES.get(mod);
    }

    public static DeferredRegister<RecipeSerializer<?>> getRecipeSerializerDeferredRegister(String mod) {
        return LoaderPropertyInitializer.RECIPE_SERIALIZER_REGISTRIES.get(mod);
    }
}
