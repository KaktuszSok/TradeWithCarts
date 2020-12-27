package kaktusz.tradewithcarts.init;

import kaktusz.tradewithcarts.blocks.BlockDirectional;
import kaktusz.tradewithcarts.blocks.BlockExchangeStation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;


public class ModBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();

    public static final BlockExchangeStation EXCHANGE_STATION = new BlockExchangeStation("exchange_station", Material.ROCK);

    public static void setExtraBlockInfo()
    {

    }
}
