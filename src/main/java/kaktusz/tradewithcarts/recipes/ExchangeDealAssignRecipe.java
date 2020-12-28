package kaktusz.tradewithcarts.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import kaktusz.tradewithcarts.items.ItemExchangeDeal;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ExchangeDealAssignRecipe extends ShapelessOreRecipe implements IRecipe {

    public ExchangeDealAssignRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result) {
        super(group, input, result);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        ItemStack deal = null;
        ItemStack good = null;
        ItemStack payment = null;
        for(int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            if(!slotStack.isEmpty())
            {
                if(deal == null) {
                    if ((slotStack.getItem()) instanceof ItemExchangeDeal) //found the deal
                    {
                        deal = slotStack;
                        continue;
                    }
                    return false; //first item was not deal
                }
                if ((slotStack.getItem()) instanceof ItemExchangeDeal) return false; //found a second deal - invalid.

                if(good == null) {
                    good = slotStack;
                    continue;
                }
                if(payment == null) {
                    payment = slotStack;
                    continue;
                }
                return false; //more than 3 items
            }
        }
        if(deal == null || good == null || payment == null) return false;

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack result = super.getCraftingResult(inv);

        ItemStack deal = null;
        ItemStack good = null;
        ItemStack payment = null;
        for(int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            if(!slotStack.isEmpty())
            {
                if(deal == null) {
                    if ((slotStack.getItem()) instanceof ItemExchangeDeal) //found the deal
                    {
                        deal = slotStack;
                        continue;
                    }
                }
                if(good == null) {
                    good = slotStack;
                    continue;
                }
                if(payment == null) {
                    payment = slotStack;
                    continue;
                }
                break;
            }
        }

        ItemExchangeDeal.setGood(result, good);
        ItemExchangeDeal.setPayment(result, payment);

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
            if(!(inv.getStackInSlot(i).getItem() instanceof ItemExchangeDeal)) //item is not exchange deal - keep it in grid
            {
                ItemStack retStack = inv.getStackInSlot(i).copy();
                retStack.setCount(1);
                ret.set(i, retStack);
            }
        }
        return ret;
    }

    public static ExchangeDealAssignRecipe factory(JsonContext context, JsonObject json)
    {
        String group = JsonUtils.getString(json, "group", "");

        NonNullList<Ingredient> ings = NonNullList.create();
        for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
            ings.add(CraftingHelper.getIngredient(ele, context));

        if (ings.isEmpty())
            throw new JsonParseException("No ingredients for shapeless recipe");

        ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
        return new ExchangeDealAssignRecipe(group.isEmpty() ? null : new ResourceLocation(group), ings, itemstack);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            return ExchangeDealAssignRecipe.factory(context, json);
        }
    }
}
