package net.flytre.flytre_lib.api.storage.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

import java.util.function.Predicate;

/**
 * A quantified ingredient is a recipe ingredient that also has a mandated quantity.
 */
public final class QuantifiedIngredient implements Predicate<ItemStack> {

    private final Ingredient ingredient;
    private final int quantity;

    public QuantifiedIngredient(Ingredient ingredient, int quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public static QuantifiedIngredient fromJson(JsonElement json) {
        Ingredient ingredient = Ingredient.fromJson(json);
        int quantity = 1;
        if (json.isJsonObject() && JsonHelper.hasPrimitive((JsonObject) json, "count")) {
            quantity = JsonHelper.getInt((JsonObject) json, "count");
        }
        return new QuantifiedIngredient(ingredient, quantity);
    }

    public static QuantifiedIngredient fromPacket(PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        int quantity = buf.readInt();
        return new QuantifiedIngredient(ingredient, quantity);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void toPacket(PacketByteBuf packet) {
        ingredient.write(packet);
        packet.writeInt(quantity);
    }

    public boolean isEmpty() {
        return ingredient.isEmpty() || quantity == 0;
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= quantity;
    }


    public ItemStack[] getMatchingStacks() {
        ingredient.getMatchingStacks();
        ItemStack[] result = ingredient.getMatchingStacks();
        for (ItemStack stack : result) {
            stack.setCount(quantity);
        }
        return result;
    }

    @Override
    public String toString() {
        return "QuantifiedIngredient{" +
                "ingredient=" + ingredient.toJson() +
                ", quantity=" + quantity +
                '}';
    }
}
