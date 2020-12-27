package kaktusz.tradewithcarts.recipes;

import com.google.gson.JsonObject;
import kaktusz.tradewithcarts.items.ItemExchangeDeal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IngredientAny extends Ingredient {
    public static IngredientAny INSTANCE = new IngredientAny();

    private IngredientAny() {
        super(0);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if(stack == null) return false;
        if(stack.isEmpty()) return false;
        return !(stack.getItem() instanceof ItemExchangeDeal);
    }

    public static class Factory implements IIngredientFactory {

        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return IngredientAny.INSTANCE;
        }
    }
}
