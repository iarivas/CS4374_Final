package net.blay09.mods.waystones.menu;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPlateContainer extends AbstractContainerMenu {

    private final WarpPlateBlockEntity blockEntity;
    private final ContainerData containerData;

    public WarpPlateContainer(int windowId, WarpPlateBlockEntity warpPlate, ContainerData containerData, Inventory playerInventory) {
        super(ModMenus.warpPlate.get(), windowId);
        this.blockEntity = warpPlate;
        this.containerData = containerData;
        warpPlate.markReadyForAttunement();

        checkContainerDataCount(containerData, 1);

        addSlot(new Slot(warpPlate, 0, 80, 45));
        addSlot(new Slot(warpPlate, 1, 80, 17));
        addSlot(new Slot(warpPlate, 2, 108, 45));
        addSlot(new Slot(warpPlate, 3, 80, 73));
        addSlot(new Slot(warpPlate, 4, 52, 45));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        for (int j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j, 8 + j * 18, 162));
        }

        addDataSlots(containerData);
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = blockEntity.getBlockPos();
        return player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64;
    }

    public float getAttunementProgress() {
        return containerData.get(0) / (float) blockEntity.getMaxAttunementTicks();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index < 5) {
                if (!this.moveItemStackTo(slotStack, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 5, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    public IWaystone getWaystone() {
        return blockEntity.getWaystone();
    }
}
