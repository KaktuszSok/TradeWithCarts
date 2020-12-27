package kaktusz.tradewithcarts.blocks;

import kaktusz.tradewithcarts.TradeWithCarts;
import kaktusz.tradewithcarts.tileentities.ExchangeStationTileEntity;
import kaktusz.tradewithcarts.util.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockExchangeStation extends BlockDirectional {

    public BlockExchangeStation(String name, Material material) {
        super(name, material);
    }

    public void RegisterTileEntity() {
        GameRegistry.registerTileEntity(ExchangeStationTileEntity.class, new ResourceLocation(Reference.MOD_ID,"exchange_station_te"));
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return side != null && side.getAxis() != EnumFacing.Axis.Y;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return side.getAxis() == EnumFacing.Axis.Y ? 0 : ((ExchangeStationTileEntity)blockAccess.getTileEntity(pos)).redstone ? 15:0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        ExchangeStationTileEntity te = new ExchangeStationTileEntity();
        te.SetBlockState(state);
        return te;
    }
}
