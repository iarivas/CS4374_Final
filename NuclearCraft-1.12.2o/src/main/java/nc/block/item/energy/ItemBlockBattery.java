package nc.block.item.energy;

import nc.multiblock.battery.BatteryType;
import nc.tile.internal.energy.EnergyConnection;
import net.minecraft.block.Block;

public class ItemBlockBattery extends ItemBlockEnergy {
	
	public ItemBlockBattery(Block block, long capacity, int maxTransfer, int energyTier, String... tooltip) {
		super(block, capacity, maxTransfer, energyTier, EnergyConnection.BOTH, tooltip);
	}
	
	public ItemBlockBattery(Block block, BatteryType type, String... tooltip) {
		this(block, type.getCapacity(), type.getMaxTransfer(), type.getEnergyTier(), tooltip);
	}
}
