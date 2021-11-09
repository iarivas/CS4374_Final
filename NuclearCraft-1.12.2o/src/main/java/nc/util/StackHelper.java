package nc.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.*;

public class StackHelper {
	
	public static ItemStack fixItemStack(Object object) {
		if (object instanceof ItemStack) {
			ItemStack stack = ((ItemStack) object).copy();
			if (stack.getCount() == 0) {
				stack.setCount(1);
			}
			return stack;
		}
		else if (object instanceof Item) {
			return new ItemStack((Item) object, 1);
		}
		else {
			if (!(object instanceof Block)) {
				throw new RuntimeException(String.format("Invalid ItemStack: %s", object));
			}
			return new ItemStack((Block) object, 1);
		}
	}
	
	public static ItemStack blockStateToStack(IBlockState state) {
		if (state == null) {
			return ItemStack.EMPTY;
		}
		Block block = state.getBlock();
		if (block == null) {
			return ItemStack.EMPTY;
		}
		int meta = block.getMetaFromState(state);
		return new ItemStack(block, 1, meta);
	}
	
	public static IBlockState getBlockStateFromStack(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		if (stack.getItem() == Items.AIR) {
			return Blocks.AIR.getDefaultState();
		}
		if (stack.isEmpty()) {
			return null;
		}
		int meta = getMetadata(stack);
		Item item = stack.getItem();
		if (!(item instanceof ItemBlock)) {
			return null;
		}
		ItemBlock itemBlock = (ItemBlock) item;
		return itemBlock.getBlock().getStateFromMeta(meta);
	}
	
	public static int getMetadata(ItemStack stack) {
		return Items.DIAMOND.getMetadata(stack);
	}
	
	public static ItemStack changeStackSize(ItemStack stack, int size) {
		ItemStack newStack = stack.copy();
		newStack.setCount(size);
		return newStack.copy();
	}
	
	public static String stackPath(ItemStack stack) {
		return Item.REGISTRY.getNameForObject(stack.getItem()).getPath();
	}
	
	public static String stackName(ItemStack stack) {
		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(stack.getItem());
		return resourcelocation == null ? "null" : resourcelocation.toString() + ":" + getMetadata(stack);
	}
	
	public static String stackListNames(List<ItemStack> list) {
		String names = "";
		for (ItemStack stack : list) {
			names += ", " + stackName(stack);
		}
		return names.substring(2);
	}
	
	/** Stack tag comparison without checking capabilities such as radiation */
	public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
		if (stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		}
		else if (!stackA.isEmpty() && !stackB.isEmpty()) {
			if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
				return false;
			}
			return stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound());
		}
		
		return false;
	}
	
	public static ItemStack getBucket(FluidStack fluidStack) {
		return FluidUtil.getFilledBucket(fluidStack);
	}
	
	public static ItemStack getBucket(String fluidName) {
		return getBucket(new FluidStack(FluidRegistry.getFluid(fluidName), Fluid.BUCKET_VOLUME));
	}
}
