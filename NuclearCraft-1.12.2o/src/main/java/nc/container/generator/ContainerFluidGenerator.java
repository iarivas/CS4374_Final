package nc.container.generator;

import nc.container.ContainerTile;
import nc.recipe.BasicRecipeHandler;
import nc.tile.ITileGui;
import nc.tile.generator.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerFluidGenerator<GENERATOR extends IFluidGenerator & ITileGui> extends ContainerTile<GENERATOR> {
	
	protected final GENERATOR tile;
	protected final BasicRecipeHandler recipeHandler;
	
	public ContainerFluidGenerator(EntityPlayer player, GENERATOR tileEntity, BasicRecipeHandler recipeHandler) {
		super(tileEntity);
		tile = tileEntity;
		this.recipeHandler = recipeHandler;
		
		tileEntity.beginUpdatingPlayer(player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUsableByPlayer(player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		tile.stopUpdatingPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		int otherSlotsSize = tile instanceof TileFluidGenerator ? ((TileFluidGenerator) tile).getOtherSlotsSize() : 0;
		int invStart = otherSlotsSize;
		int invEnd = 36 + otherSlotsSize;
		//main branch
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			//branch 1
			emptyStackHelper(index, invStart, invEnd, itemstack1, itemstack);
			//branch 2
			if (itemstack1.isEmpty()){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}	
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
	private ItemStack emptyStackHelper(int index, int invStart, int invEnd, ItemStack itemstack1, ItemStack itemstack){
		//branch 1.1
		if (index >= 0 && index < invStart && !mergeItemStack(itemstack1, invStart, invEnd, false)) {
			slot.onSlotChange(itemstack1, itemstack);
			return ItemStack.EMPTY;
		}
		//branch 1.2
		else if (index >= invStart && 
				( (recipeHandler.isValidItemInput(itemstack1) && !mergeItemStack(itemstack1, 0, 0, false)) || 
				(index >= invStart && index < invEnd - 9 && !mergeItemStack(itemstack1, invEnd - 9, invEnd, false)) ||
				(index >= invEnd - 9 && index < invEnd && !mergeItemStack(itemstack1, invStart, invEnd - 9, false)) )) {
					return ItemStack.EMPTY;
				}
		//branch 1.3
		else if(!mergeItemStack(itemstack1, invStart, invEnd, false) || itemstack1.getCount() == itemstack.getCount()) {
			return ItemStack.EMPTY;
		}
	}

}
