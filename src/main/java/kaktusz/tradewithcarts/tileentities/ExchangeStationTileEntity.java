package kaktusz.tradewithcarts.tileentities;

import kaktusz.tradewithcarts.blocks.BlockExchangeStation;
import kaktusz.tradewithcarts.init.ModBlocks;
import kaktusz.tradewithcarts.init.ModItems;
import kaktusz.tradewithcarts.items.ItemExchangeDeal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class ExchangeStationTileEntity extends TileEntity implements ITickable {

    static final double sensitivity = 0.2D;
    IBlockState blockState;
    EnumFacing forward;
    AxisAlignedBB searchForCartBB = null;
    public boolean redstone = false;

    public ExchangeStationTileEntity() {
        blockType = ModBlocks.EXCHANGE_STATION;
    }

    public void SetBlockState(IBlockState bs) {
        blockState = bs;
        forward = blockState.getValue(BlockExchangeStation.FACING);
    }

    private void CalculateSearchBB() { //Here we cache the bounding box for chest cart detection
        if(blockState == null) {
            SetBlockState(world.getBlockState(pos));
        }

        BlockPos position = pos.offset(forward);
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
            world.notifyNeighborsOfStateChange(pos, ModBlocks.EXCHANGE_STATION, true);
        }
    }

    boolean tryDoExchange() {
        IInventory cart = searchForEntityInventory();
        if(cart == null || cart.getSizeInventory() < 2) return false; //no storage cart
        ItemStack cartDeal = cart.getStackInSlot(0);
        if(cartDeal.isEmpty() || cartDeal.getItem() != ModItems.EXCHANGE_DEAL) return false; //no deal available

        //we have a cart that wants a deal - check if we are able to handle one right now
        IInventory topInv = searchForBlockInventory(1);
        if(topInv == null || topInv.getSizeInventory() < 2 || topInv.getStackInSlot(0).getItem() != ModItems.EXCHANGE_DEAL) return false; //top inventory invalid
        ItemStack ourDeal = topInv.getStackInSlot(0);

        //check if goods and prices match:
        int ratio = ItemExchangeDeal.checkDealsMatching(ourDeal, cartDeal);
        if(ratio == 0) return false;

        IInventory bottomInv = searchForBlockInventory(-1);
        if(bottomInv == null || bottomInv.getSizeInventory() < 1) return false; //output inventory invalid

        ItemStack good = ItemExchangeDeal.getGood(ourDeal);
        ItemStack payment = ItemExchangeDeal.getPayment(ourDeal);

        //check if cart has enough payment:
        if(!doesInventoryContainStack(payment, cart, 1, forward.getOpposite()))
            return false;

        //check if topInv has enough supply:
        if(!doesInventoryContainStack(good, topInv, 1, EnumFacing.DOWN))
            return false;

        //check if bottomInv has enough space:
        if(!doesInventoryHaveSpace(payment, bottomInv, 0, EnumFacing.UP))
            return false;

        //check if cart has enough space:
        if(!doesInventoryHaveSpace(good, cart, 1, forward.getOpposite()))
            return false;

            //TRANSACTION CAN COMMENCE
        //remove payment from cart and add to bottomInv:
        RemoveStackFromInventory(payment, cart, 1, null);
        AddStackToInventory(payment, bottomInv, 0, EnumFacing.UP);

        //remove supply from topInv and add to cart
        RemoveStackFromInventory(good, topInv, 1, EnumFacing.DOWN);
        AddStackToInventory(good, cart, 1, null);

        world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 0.3F, 0.8F);

        return true;
    }

    boolean doesInventoryContainStack(ItemStack stack, IInventory inv, int startIndex, @Nullable EnumFacing side) {
        int count = 0;
        if(inv instanceof ISidedInventory && side != null)
        {
            ISidedInventory sidedInv = (ISidedInventory) inv;
            int[] slots = sidedInv.getSlotsForFace(side);
            for (int i : slots)
            {
                if (areStacksStackable(inv.getStackInSlot(i), stack))
                    count += inv.getStackInSlot(i).getCount();
                if (count >= stack.getCount()) return true;
            }
            return false;
        }

        for(int i = startIndex; i < inv.getSizeInventory(); i++)
        {
            if (areStacksStackable(inv.getStackInSlot(i), stack))
                count += inv.getStackInSlot(i).getCount();
            if(count >= stack.getCount()) return true;
        }
        return false;
    }

    boolean doesInventoryHaveSpace(ItemStack stack, IInventory inv, int startIndex, @Nullable EnumFacing side) {
        int countLeft = stack.getCount();
        if(inv instanceof ISidedInventory && side != null)
        {
            ISidedInventory sidedInv = (ISidedInventory)inv;
            int[] slots = sidedInv.getSlotsForFace(side);
            for (int i : slots)
            {
                if (!inv.isItemValidForSlot(i, stack)) continue;
                if (inv.getStackInSlot(i).isEmpty()) {
                    return true;
                }
                countLeft -= getCombineDelta(stack, inv.getStackInSlot(i));

                if (countLeft == 0) return true;
            }
            return false;
        }

        for(int i = startIndex; i < inv.getSizeInventory(); i++)
        {
            if(!inv.isItemValidForSlot(i, stack)) continue;
            if(inv.getStackInSlot(i).isEmpty()) {
                return true;
            }
            countLeft -= getCombineDelta(stack, inv.getStackInSlot(i));

            if(countLeft == 0) return true;
        }
        return false;
    }

    void RemoveStackFromInventory(ItemStack stack, IInventory inv, int startIndex, @Nullable EnumFacing side) {
        int countLeft = stack.getCount();
        if(inv instanceof ISidedInventory && side != null)
        {
            ISidedInventory sidedInv = (ISidedInventory) inv;
            int[] slots = sidedInv.getSlotsForFace(side);
            for (int i : slots)
            {
                ItemStack slotStack = inv.getStackInSlot(i);
                if (areStacksStackable(inv.getStackInSlot(i), stack))
                {
                    int amtToRemove = Math.min(countLeft, slotStack.getCount());
                    inv.decrStackSize(i, amtToRemove);
                    countLeft -= amtToRemove;
                }
                if (countLeft == 0) return;
            }
            return;
        }

        for(int i = startIndex; i < inv.getSizeInventory(); i++)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            if (areStacksStackable(inv.getStackInSlot(i), stack))
            {
                int amtToRemove = Math.min(countLeft, slotStack.getCount());
                inv.decrStackSize(i, amtToRemove);
                countLeft -= amtToRemove;
            }
            if (countLeft == 0) return;
        }
    }

    void AddStackToInventory(ItemStack stack, IInventory inv, int startIndex, @Nullable EnumFacing side) {
        int countLeft = stack.getCount();
        if(inv instanceof ISidedInventory && side != null)
        {
            ISidedInventory sidedInv = (ISidedInventory) inv;
            int[] slots = sidedInv.getSlotsForFace(side);
            for (int i : slots)
            {
                ItemStack slotStack = inv.getStackInSlot(i);
                if(!inv.isItemValidForSlot(i, stack)) continue;
                if(slotStack.isEmpty()) {
                    inv.setInventorySlotContents(i, stack.copy());
                    return;
                }
                int amtToAdd = Math.min(stack.getCount(), getCombineDelta(stack, slotStack));
                if (amtToAdd > 0)
                {
                    slotStack.setCount(slotStack.getCount() + amtToAdd);
                    inv.setInventorySlotContents(i, slotStack);
                    countLeft -= amtToAdd;
                }
                if (countLeft == 0) return;
            }
            return;
        }

        for(int i = startIndex; i < inv.getSizeInventory(); i++)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            if(!inv.isItemValidForSlot(i, stack)) continue;
            if(slotStack.isEmpty()) {
                inv.setInventorySlotContents(i, stack.copy());
                return;
            }
            int amtToAdd = Math.min(stack.getCount(), getCombineDelta(stack, slotStack));
            if (amtToAdd > 0)
            {
                slotStack.setCount(slotStack.getCount() + amtToAdd);
                inv.setInventorySlotContents(i, slotStack);
                countLeft -= amtToAdd;
            }
            if (countLeft == 0) return;
        }
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

        List<Entity> list = this.world.getEntitiesInAABBexcluding(null, searchForCartBB, EntitySelectors.HAS_INVENTORY);

        if (!list.isEmpty())
        {
            result = (IInventory)list.get(this.world.rand.nextInt(list.size()));
        }

        return result;
    }

    private static boolean areStacksStackable(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        else if (stack1.getMetadata() != stack2.getMetadata())
        {
            return false;
        }
        else return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * @return 0 if stacks can't combine, otherwise the amount of items that would transfer from stack 1 to stack 2.
     */
    private static int getCombineDelta(ItemStack stack1, ItemStack stack2)
    {
        if(stack2.isEmpty()) return stack1.getCount();
        if(!areStacksStackable(stack1, stack2)) return 0;
        else {
            return Math.min(stack1.getCount(), stack2.getMaxStackSize() - stack2.getCount());
        }
    }
}
