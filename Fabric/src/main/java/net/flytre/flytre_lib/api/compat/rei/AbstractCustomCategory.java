package net.flytre.flytre_lib.api.compat.rei;

import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.SimpleDisplayRenderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class AbstractCustomCategory<R extends Recipe<?>> implements DisplayCategory<AbstractRecipeDisplay<R>> {
    private final RecipeType<R> recipeType;

    public AbstractCustomCategory(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }


    @Override
    public CategoryIdentifier<? extends AbstractRecipeDisplay<R>> getCategoryIdentifier() {
        return CategoryIdentifier.of(Objects.requireNonNull(Registry.RECIPE_TYPE.getId(recipeType)));
    }

    @Override
    public @NotNull Text getTitle() {
        return new TranslatableText(getIdentifier().toString());
    }

    @Override
    public abstract @NotNull Renderer getIcon();

    public RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull DisplayRenderer getDisplayRenderer(AbstractRecipeDisplay<R> recipe) {
        return SimpleDisplayRenderer.from(recipe.getInputEntries(), recipe.getOutputEntries());
    }

    public EntryIngredient getInput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<EntryIngredient> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : EntryIngredient.empty();
    }



    public EntryIngredient getOutput(AbstractRecipeDisplay<R> recipeDisplay, int index) {
        List<EntryIngredient> outputs = recipeDisplay.getOutputEntries();
        return outputs.size() > index ? outputs.get(index) : EntryIngredient.empty();
    }


}
