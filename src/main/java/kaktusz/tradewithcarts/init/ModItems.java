package kaktusz.tradewithcarts.init;

import kaktusz.tradewithcarts.items.ItemBase;
import kaktusz.tradewithcarts.items.ItemExchangeDeal;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;


public class ModItems {

    public static final List<Item> ITEMS = new ArrayList<>();

    public static final ItemBase EXCHANGE_DEAL = new ItemExchangeDeal("exchange_deal");

    public static void setExtraItemInfo()
    {

    }

}