package nc.block.fluid;

import java.util.Random;

import nc.fluid.FluidCoolant;
import nc.util.PotionHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFluidCoolant extends NCBlockFluid {
	
	public BlockFluidCoolant(FluidCoolant fluid) {
		super(fluid, Material.WATER);
		setQuantaPerBlock(4);
	}
	
	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			((EntityLivingBase) entityIn).addPotionEffect(PotionHelper.newEffect(2, 1, 100));
			((EntityLivingBase) entityIn).addPotionEffect(PotionHelper.newEffect(18, 1, 100));
		}
	}
	
	@Override
	protected boolean canMixWithFluids(World world, BlockPos pos, IBlockState state) {
		return false;
	}
	
	@Override
	protected boolean shouldMixWithAdjacentFluid(World world, BlockPos pos, IBlockState state, IBlockState otherState) {
		return false;
	}
	
	@Override
	protected IBlockState getSourceMixingState(World world, BlockPos pos, IBlockState state) {
		return Blocks.OBSIDIAN.getDefaultState();
	}
	
	@Override
	protected IBlockState getFlowingMixingState(World world, BlockPos pos, IBlockState state) {
		return Blocks.COBBLESTONE.getDefaultState();
	}
	
	@Override
	protected boolean canSetFireToSurroundings(World world, BlockPos pos, IBlockState state, Random rand) {
		return false;
	}
	
	@Override
	protected IBlockState getFlowingIntoWaterState(World world, BlockPos pos, IBlockState state, Random rand) {
		return null;
	}
}
