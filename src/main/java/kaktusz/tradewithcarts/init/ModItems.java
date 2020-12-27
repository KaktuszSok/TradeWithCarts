package kaktusz.tradewithcarts.init;

import kaktusz.tradewithcarts.items.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;


public class ModItems {

    public static final List<Item> ITEMS = new ArrayList<>();

    public static final ItemBase EXCHANGE_DEAL = new ItemBase("exchange_deal", CreativeTabs.TRANSPORTATION);

    public static void setExtraItemInfo()
    {
        EXCHANGE_DEAL.setLore("GOOD: 64x Oak Wood", "PAYMENT: 1x Iron Coin");
    }

}