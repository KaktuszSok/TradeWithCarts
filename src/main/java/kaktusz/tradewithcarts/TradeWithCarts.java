package kaktusz.tradewithcarts;

import kaktusz.tradewithcarts.init.ModBlocks;
import kaktusz.tradewithcarts.init.ModItems;
import kaktusz.tradewithcarts.proxy.CommonProxy;
import kaktusz.tradewithcarts.util.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class TradeWithCarts {
    @Mod.Instance
    public static TradeWithCarts instance;

    public static Logger logger;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        proxy.init();
        ModItems.setExtraItemInfo();
        ModBlocks.setExtraBlockInfo();
    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event)
    {

    }
}
