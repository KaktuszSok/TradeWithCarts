package kaktusz.tradewithcarts.tileentities;

import kaktusz.tradewithcarts.TradeWithCarts;
import kaktusz.tradewithcarts.blocks.BlockExchangeStation;
import kaktusz.tradewithcarts.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ExchangeStationTileEntity extends TileEntity implements ITickable {

    static final double sensitivity = 0.2D;
    IBlockState blockState;
    AxisAlignedBB searchForCartBB = null;
    public boolean redstone = false;

    public ExchangeStationTileEntity() {

    }

    public void SetBlockState(IBlockState bs) {
        blockState = bs;
    }

    private void CalculateSearchBB() { //Here we cache the bounding box for chest cart detection
        if(blockState == null) {
            blockState = world.getBlockState(pos);
        }

        EnumFacing facing = blockState.getValue(BlockExchangeStation.FACING);
        BlockPos position = pos.offset(facing);
        //x,y,z are the coordinates of the centre of the block in front of us
        double x = position.getX() + 0.5D;
        double y = position.getY() + 0.5D;
        double z = position.getZ() + 0.5D;

        searchForCartBB = new AxisAlignedBB(x - sensitivity, y - sensitivity, z - sensitivity, x + sensitivity, y + sensitivity, z + sensitivity);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }

    @Override
    public void update() {
        if(searchForCartBB == null) CalculateSearchBB();

        boolean success = tryDoExchange();

        if(redstone == success) {
            redstone = !success;
            world.notifyNeighborsOfStateChange(pos, blockType, true);
        }
    }

    boolean tryDoExchange() {
        IInventory cart = searchForEntityInventory();
        if(cart == null) return false; //no storage cart
        if(cart.getStackInSlot(0).getItem() != ModItems.EXCHANGE_DEAL) return false; //no deal available

        //we have a cart that wants a deal - check if we are able to handle one right now
        IInventory topInv = searchForBlockInventory(1);
        if(topInv == null || topInv.getSizeInventory() < 2 || topInv.getStackInSlot(0).getItem() != ModItems.EXCHANGE_DEAL) return false; //top inventory invalid
        //TODO: check if goods and prices match
        IInventory bottomInv = searchForBlockInventory(-1);
        if(bottomInv == null || bottomInv.getSizeInventory() < 1) return false; //output inventory invalid

        //TODO: check if cart has enough payment
        //TODO: check if topInv has enough supply
        //TODO: check if bottomInv has enough space
        //TODO: check if cart has enough space

        //TRANSACTION CAN COMMENCE
        //TODO: remove payment from cart and add to bottomInv
        //TODO: remove supply from topInv and add to cart
        return true;
    }

    IInventory searchForBlockInventory(int yOffset) {
        IInventory result = null;
        BlockPos position = pos.add(0, yOffset, 0);

        IBlockState state = world.getBlockState(position);
        Block block = state.getBlock();
        if (block.hasTileEntity(state))
        {
            TileEntity tileentity = world.getTileEntity(position);

            if (tileentity instanceof IInventory)
            {
                result = (IInventory)tileentity;

                if (result instanceof TileEntityChest && block instanceof BlockChest)
                {
                    result = ((BlockChest)block).getContainer(world, position, true);
                }
            }
        }

        return result;
    }

    IInventory searchForEntityInventory() {
        IInventory result = null;

        TradeWithCarts.logger.debug(searchForCartBB.minX + "-" + searchForCartBB.maxX + ", " +
                searchForCartBB.minY + "-" + searchForCartBB.maxY + ", " +
                searchForCartBB.minZ + "-" + searchForCartBB.maxZ);
        List<Entity> list = this.world.getEntitiesInAABBexcluding(null, searchForCartBB, EntitySelectors.HAS_INVENTORY);

        if (!list.isEmpty())
        {
            result = (IInventory)list.get(this.world.rand.nextInt(list.size()));
        }

        return result;
    }
}
