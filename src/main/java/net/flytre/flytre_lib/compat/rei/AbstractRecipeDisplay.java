package net.flytre.flytre_lib.compat.rei;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractRecipeDisplay<R extends Recipe<?>> implements RecipeDisplay {

    protected final R recipe;
    protected final List<List<EntryStack>> inputs;
    protected final List<List<EntryStack>> outputs;

    public AbstractRecipeDisplay(R recipe) {
        this.recipe = recipe;
        inputs = createInputs();
        outputs = createOutputs();
    }


    @Override
    public @NotNull Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(recipe).map(Recipe::getId);
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getRequiredEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return outputs;
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return Objects.requireNonNull(Registry.RECIPE_TYPE.getId(recipe.getType()));
    }

    public R getRecipe() {
        return recipe;
    }

    public abstract List<List<EntryStack>> createOutputs();

    public abstract List<List<EntryStack>> createInputs();

}
