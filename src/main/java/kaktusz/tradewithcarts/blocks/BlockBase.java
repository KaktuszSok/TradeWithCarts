package kaktusz.tradewithcarts.blocks;

import kaktusz.tradewithcarts.TradeWithCarts;
import kaktusz.tradewithcarts.init.ModBlocks;
import kaktusz.tradewithcarts.init.ModItems;
import kaktusz.tradewithcarts.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel {

    public BlockBase(String name, Material material) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.REDSTONE);

        ModBlocks.BLOCKS.add(this);
        //noinspection ConstantConditions
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {
        TradeWithCarts.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
