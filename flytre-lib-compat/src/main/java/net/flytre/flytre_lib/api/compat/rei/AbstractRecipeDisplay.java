package net.flytre.flytre_lib.api.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractRecipeDisplay<R extends Recipe<?>> implements Display {

    protected final R recipe;
    protected final List<EntryIngredient> inputs;
    protected final List<EntryIngredient> outputs;

    public AbstractRecipeDisplay(R recipe) {
        this.recipe = recipe;
        inputs = createInputs();
        outputs = createOutputs();
    }


    @Override
    public @NotNull Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe).map(Recipe::getId);
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(Objects.requireNonNull(Registry.RECIPE_TYPE.getId(recipe.getType())));
    }

    public R getRecipe() {
        return recipe;
    }

    public abstract List<EntryIngredient> createOutputs();

    public abstract List<EntryIngredient> createInputs();

}
