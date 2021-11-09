package nc.multiblock.turbine.block;

import static nc.block.property.BlockProperties.AXIS_ALL;

import nc.multiblock.turbine.tile.TileTurbineOutlet;
import nc.util.PosHelper;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTurbineOutlet extends BlockTurbinePart {
	
	public BlockTurbineOutlet() {
		super();
		setDefaultState(blockState.getBaseState().withProperty(AXIS_ALL, EnumFacing.Axis.Z));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AXIS_ALL);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(AXIS_ALL, PosHelper.AXES[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AXIS_ALL).ordinal();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileTurbineOutlet();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(AXIS_ALL, EnumFacing.getDirectionFromEntityLiving(pos, placer).getAxis());
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player == null) {
			return false;
		}
		if (hand != EnumHand.MAIN_HAND || player.isSneaking()) {
			return false;
		}
		return rightClickOnPart(world, pos, player, hand, facing);
	}
}
