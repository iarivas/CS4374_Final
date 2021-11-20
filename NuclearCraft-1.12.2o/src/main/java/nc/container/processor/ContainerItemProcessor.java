package nc.container.processor;

import nc.container.ContainerTile;
import nc.init.NCItems;
import nc.recipe.BasicRecipeHandler;
import nc.tile.ITileGui;
import nc.tile.processor.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerItemProcessor<PROCESSOR extends IItemProcessor & ITileGui> extends ContainerTile<PROCESSOR> {
	
	protected final PROCESSOR tile;
	protected final BasicRecipeHandler recipeHandler;
	
	protected static final ItemStack SPEED_UPGRADE = new ItemStack(NCItems.upgrade, 1, 0);
	protected static final ItemStack ENERGY_UPGRADE = new ItemStack(NCItems.upgrade, 1, 1);
	
	public ContainerItemProcessor(EntityPlayer player, PROCESSOR tileEntity, BasicRecipeHandler recipeHandler) {
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
		final boolean hasUpgrades = tile instanceof IUpgradable && ((IUpgradable) tile).hasUpgrades();
		int upgrades = hasUpgrades ? ((IUpgradable) tile).getNumberOfUpgrades() : 0;
		int invStart = tile.getItemInputSize() + tile.getItemOutputSize() + upgrades;
		int speedUpgradeSlot = tile.getItemInputSize() + tile.getItemOutputSize();
		int otherUpgradeSlot = tile.getItemInputSize() + tile.getItemOutputSize() + 1;
		int invEnd = tile.getItemInputSize() + tile.getItemOutputSize() + 36 + upgrades;
		
		//main branch
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
		//branch 1
		if (index >= tile.getItemInputSize() && index < invStart && !mergeItemStack(itemstack1, invStart, invEnd, false)) {
			slot.onSlotChange(itemstack1, itemstack);
			return ItemStack.EMPTY;
		}
		//branch 2
		else if (!mergeItemStack(itemstack1, invStart, invEnd, false) || itemstack1.getCount() == itemstack.getCount()){
			return ItemStack.EMPTY;
		}

		//branch 3
		else if (index >= invStart && hasUpgrades && itemstack1.getItem() == NCItems.upgrade) {
			//branch 3.1
			if ((tile.isItemValidForSlot(speedUpgradeSlot, itemstack1) && !mergeItemStack(itemstack1, speedUpgradeSlot, speedUpgradeSlot + 1, false)) ||
				(tile.isItemValidForSlot(otherUpgradeSlot, itemstack1) && !mergeItemStack(itemstack1, otherUpgradeSlot, otherUpgradeSlot + 1, false))){
					return ItemStack.EMPTY;
				}
			//branch 3.2
			else  if ( (recipeHandler.isValidItemInput(itemstack1) && !mergeItemStack(itemstack1, 0, tile.getItemInputSize(), false) )||
				 	   (index >= invStart && index < invEnd - 9 && !mergeItemStack(itemstack1, invEnd - 9, invEnd, false) ) ||
				 	   (index >= invEnd - 9 && index < invEnd && !mergeItemStack(itemstack1, invStart, invEnd - 9, false) ) ){
							return ItemStack.EMPTY;
						}
		}
		//branch 4
		if (itemstack1.isEmpty()){
			slot.putStack(ItemStack.EMPTY);
		}else{
			slot.onSlotChanged();
		}
		slot.onTake(player, itemstack1);
	}
	return itemstack;
	}
}
