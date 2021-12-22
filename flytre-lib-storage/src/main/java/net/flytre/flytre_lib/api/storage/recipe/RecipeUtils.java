package net.flytre.flytre_lib.api.storage.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilities that are associated with recipes:
 * Things like converting Json to recipe objects, handling crafting logic, and finding
 * matching recipes
 */
public class RecipeUtils {

    private static final Map<Item, List<CraftingRecipe>> CACHE;
    private static final Queue<Item> CACHE_HANDLER;

    static {
        CACHE = new HashMap<>();
        CACHE_HANDLER = new LinkedList<>();
    }

    private RecipeUtils() {
        throw new AssertionError();
    }

    private static boolean nullCheck(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            return true;
        } else {
            Identifier identifier2;
            if (json.has("item")) {
                Identifier id = new Identifier(JsonHelper.getString(json, "item"));
                return !Registry.ITEM.getIds().contains(id);
            } else if (json.has("tag")) {
                identifier2 = new Identifier(JsonHelper.getString(json, "tag"));
                try {
                    Tag<Item> tag = ServerTagManagerHolder.getTagManager().getTag(Registry.ITEM_KEY, identifier2, (exc) -> new JsonSyntaxException("Unknown item tag '" + exc + "'"));
                    return tag == null;
                } catch (JsonSyntaxException e) {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    public static void craftOutput(Inventory inv, int lower, int upper, OutputProvider[] providers) {
        Set<Integer> checked = new HashSet<>();
        for (OutputProvider output : providers) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && InventoryUtils.canUnifyStacks(output.getStack(), inv.getStack(i))) {
                    matched = true;
                    checked.add(i);
                    inv.getStack(i).increment(output.getStack().getCount());
                    break;
                }
            if (!matched) {
                for (int i = lower; i < upper; i++) {
                    if (inv.getStack(i).isEmpty()) {
                        inv.setStack(i, output.getStack());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Check whether a set of output providers can be placed between slots [lower, upper) in an inventory
     */
    public static boolean matches(Inventory inv, int lower, int upper, OutputProvider[] outputProviders) {
        int blanks = 0; //number of empty slots
        for (int i = lower; i < upper; i++) {
            if (inv.getStack(i).isEmpty())
                blanks++;
        }
        Set<Integer> checked = new HashSet<>();
        for (OutputProvider output : outputProviders) {
            boolean matched = false;
            for (int i = lower; i < upper; i++)
                if (!checked.contains(i) && InventoryUtils.canUnifyStacks(output.getStack(), inv.getStack(i))) {
                    matched = true;
                    checked.add(i);
                    break;
                }
            if (!matched)
                if (blanks == 0)
                    return false;
                else
                    blanks--;
        }
        return true;
    }

    public static boolean craftingInputMatch(CraftingRecipe recipe, Inventory inv, int lower, int upper) {
        //Get only non-empty ingredients
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> actual = ingredients.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        //Copy the inventory and use that for parsing as u can decrement
        ArrayList<ItemStack> copy = new ArrayList<>();
        for (int i = lower; i < upper; i++) {
            copy.add(i, inv.getStack(i).copy());
        }

        for (Ingredient ingredient : actual) {
            boolean matched = false;
            for (int i = lower; i < upper; i++) {
                if (ingredient.test(copy.get(i))) {
                    matched = true;
                    copy.get(i).decrement(1);
                    break;
                }
            }
            if (!matched)
                return false;
        }
        return true;
    }

    public static void actuallyCraft(CraftingRecipe recipe, Inventory inv, int lower, int upper) {
        //Get only non-empty ingredients
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> actual = ingredients.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());

        for (Ingredient ingredient : actual)
            for (int i = lower; i < upper; i++)
                if (ingredient.test(inv.getStack(i))) {
                    ItemStack stack = inv.getStack(i);
                    if (stack.getCount() == 1 && stack.getItem().hasRecipeRemainder()) {
                        inv.setStack(i, new ItemStack(stack.getItem().getRecipeRemainder(), 1));
                    } else
                        stack.decrement(1);
                    break;
                }
    }

    /**
     * Filters out special crafting recipes
     */
    public static List<CraftingRecipe> craftingRecipesWithOutput(Item item, World world) {

        if (CACHE.containsKey(item))
            return CACHE.get(item);

        List<CraftingRecipe> recipes = world.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream().filter(i -> !(i instanceof SpecialCraftingRecipe)).filter(i -> i.getOutput().getItem() == item).collect(Collectors.toList());

        CACHE.put(item, recipes);
        CACHE_HANDLER.add(item);
        if (CACHE.size() > 10) {
            CACHE.remove(CACHE_HANDLER.poll());
        }
        return recipes;
    }

    /**
     * Filters out special crafting recipes
     */
    public static @Nullable CraftingRecipe getFirstCraftingMatch(Item item, Inventory inv, World world, int lower, int upper) {
        List<CraftingRecipe> outputs = craftingRecipesWithOutput(item, world);
        Optional<CraftingRecipe> result = outputs.stream().filter(i -> craftingInputMatch(i, inv, lower, upper)).findFirst();
        return result.orElse(null);
    }

    /**
     * Get the array of output providers from a Json recipe object
     */
    public static OutputProvider[] getOutputProviders(JsonObject json, String pluralKey, String singularKey) {
        OutputProvider[] result;
        if (JsonHelper.hasArray(json, pluralKey)) {
            JsonArray array = JsonHelper.getArray(json, pluralKey);
            result = new OutputProvider[array.size()];
            for (int i = 0; i < array.size(); i++)
                result[i] = OutputProvider.fromJson(array.get(i));
        } else {
            result = new OutputProvider[]{OutputProvider.fromJson(json.get(singularKey))};
        }

        return result;
    }

    public static QuantifiedIngredient[] getQuantifiedIngredients(JsonObject json, String pluralKey, String singularKey) {
        return getQuantifiedIngredients(json, pluralKey, singularKey, false);
    }

    /**
     * Get the array of quantified ingredients from a Json recipe object
     */
    public static QuantifiedIngredient[] getQuantifiedIngredients(JsonObject json, String pluralKey, String singularKey, boolean opt) {
        QuantifiedIngredient[] ingredients = new QuantifiedIngredient[0];
        if (JsonHelper.hasArray(json, pluralKey)) {
            JsonArray array = JsonHelper.getArray(json, pluralKey);
            ingredients = new QuantifiedIngredient[array.size()];
            for (int i = 0; i < array.size(); i++)
                ingredients[i] = QuantifiedIngredient.fromJson(array.get(i));
        } else if (JsonHelper.hasElement(json, singularKey) || !opt) {
            ingredients = new QuantifiedIngredient[]{QuantifiedIngredient.fromJson(json.get(singularKey))};
        }
        return ingredients;
    }
}
