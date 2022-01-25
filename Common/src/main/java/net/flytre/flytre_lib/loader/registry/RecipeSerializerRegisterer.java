package net.flytre.flytre_lib.loader.registry;


import net.minecraft.recipe.RecipeSerializer;

public interface RecipeSerializerRegisterer {

    <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id);
}
