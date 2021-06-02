package net.flytre.flytre_lib.compat.rei;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.entries.SimpleRecipeEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCustomCategory<R extends Recipe<?>> implements RecipeCategory<AbstractRecipeDisplay<R>> {
    private final RecipeType<R> recipeType;

    public AbstractCustomCategory(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }


    @Override
    public @NotNull Identifier getIdentifier() {
        return Objects.requireNonNull(Registry.RECIPE_TYPE.getId(recipeType));
    }

    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate(getIdentifier().toString());
    }

    @Override
    public abstract @NotNull EntryStack getLogo();

    public RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull RecipeEntry getSimpleRenderer(AbstractRecipeDisplay<R> recipe) {
        return SimpleRecipeEntry.from(recipe.getInputEntries(), recipe.getResultingEntries());
    }

    public List<EntryStack> getInput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : Collections.emptyList();
    }



    public List<EntryStack> getOutput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> outputs = recipeDisplay.getResultingEntries();
        return outputs.size() > index ? outputs.get(index) : Collections.emptyList();
    }


}
