package kaktusz.tradewithcarts.items;

import kaktusz.tradewithcarts.TradeWithCarts;
import kaktusz.tradewithcarts.init.ModItems;
import kaktusz.tradewithcarts.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ItemBase extends Item implements IHasModel {

    protected List<String> lore;

    public ItemBase(String name, CreativeTabs tab)
    {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(tab);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels()
    {
        TradeWithCarts.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public void setLore(String... lore)
    {
        this.lore = Arrays.asList(lore);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(lore != null)
            tooltip.addAll(lore);
    }
}