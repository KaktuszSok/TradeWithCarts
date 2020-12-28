package kaktusz.tradewithcarts.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemExchangeDeal extends ItemBase {

    public ItemExchangeDeal(String name) {
        super(name, CreativeTabs.TRANSPORTATION);
    }

    /**
     * @param a deal of the seller
     * @param b deal of the buyer
     * @return 0 if deals do not match, 1 if deals match in 1:1 ratio, 2 if deal a matches deal b in a 1:2 ratio, etc.
     */
    public static int checkDealsMatching(ItemStack a, ItemStack b) {
        if(a == null || b == null) return 0;
        if(!(a.getItem() instanceof ItemExchangeDeal && b.getItem() instanceof ItemExchangeDeal)) return 0;

        ItemStack goodA = getGood(a);
        ItemStack goodB = getGood(b);
        if(goodA == null || goodB == null) return 0;
        if(!ItemStack.areItemsEqual(goodA, goodB)) return 0;
        if(goodA.getCount() % goodB.getCount() != 0) return 0; //only accept deals when #A is a multiple of #B
        int ratio = goodA.getCount() / goodB.getCount();

        ItemStack payA = getPayment(a);
        ItemStack payB = getPayment(b);
        if(payA == null || payB == null) return 0;
        if(!ItemStack.areItemsEqual(payA, payB)) return 0;
        if(payB.getCount()*ratio >= payA.getCount()) return ratio; //ACCEPT if the buyer is willing to pay at least as much as the seller wants for the goods
        return 0;
    }

    public static ItemStack getGood(ItemStack deal) {
        return getStackFromNBT(deal, "tradewithcarts:good");
    }
    public static void setGood(ItemStack deal, ItemStack good) {
        setStackForNBT(deal, good, "tradewithcarts:good");
    }

    public static ItemStack getPayment(ItemStack deal) {
        return getStackFromNBT(deal, "tradewithcarts:payment");
    }
    public static void setPayment(ItemStack deal, ItemStack payment) {
        setStackForNBT(deal, payment, "tradewithcarts:payment");
    }

    private static ItemStack getStackFromNBT(ItemStack deal, String key) {
        if(!(deal.getItem() instanceof ItemExchangeDeal)) return null;

        NBTTagCompound nbt = deal.getTagCompound();
        if(nbt == null) return null;
        if(!nbt.hasKey(key)) return null;
        return new ItemStack(nbt.getCompoundTag(key));
    }
    private static void setStackForNBT(ItemStack deal, ItemStack stack, String key) {
        if(!(deal.getItem() instanceof ItemExchangeDeal)) return;
        if(stack == null) return;

        NBTTagCompound nbt = deal.getTagCompound();
        if(nbt == null) nbt = new NBTTagCompound();


        NBTTagCompound stackNBT = stack.serializeNBT();
        nbt.setTag(key, stackNBT);

        deal.setTagCompound(nbt);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ItemStack good = getGood(stack);
        ItemStack payment = getPayment(stack);
        if(good != null && payment != null) {
            tooltip.add("GOOD: " + good.getCount() + "x " + good.getItem().getItemStackDisplayName(good));
            tooltip.add("PAYMENT: " + payment.getCount() + "x " + payment.getItem().getItemStackDisplayName(payment));
        }
        else {
            tooltip.add("Assign a good and payment by putting this in the crafting grid with the good to the right and the payment beneath.");
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ItemStack good = getGood(stack);
        if(good == null || getPayment(stack) == null)
            return super.getItemStackDisplayName(stack);

        return TextFormatting.GRAY + super.getItemStackDisplayName(stack) + TextFormatting.RESET + " (" + good.getItem().getItemStackDisplayName(good) + ")";
    }
}
