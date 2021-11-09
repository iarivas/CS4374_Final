package nc.network;

import nc.network.config.ConfigUpdatePacket;
import nc.network.gui.*;
import nc.network.multiblock.*;
import nc.network.radiation.PlayerRadsUpdatePacket;
import nc.network.render.BlockHighlightUpdatePacket;
import nc.network.tile.ProcessorUpdatePacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	
	public static SimpleNetworkWrapper instance = null;
	
	public PacketHandler() {}
	
	public static void registerMessages(String channelName) {
		instance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}
	
	public static void registerMessages() {
		// SERVER
		instance.registerMessage(EmptyTankPacket.Handler.class, EmptyTankPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(EmptyFilterTankPacket.Handler.class, EmptyFilterTankPacket.class, nextID(), Side.SERVER);
		
		instance.registerMessage(ToggleInputTanksSeparatedPacket.Handler.class, ToggleInputTanksSeparatedPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleVoidUnusableFluidInputPacket.Handler.class, ToggleVoidUnusableFluidInputPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleVoidExcessFluidOutputPacket.Handler.class, ToggleVoidExcessFluidOutputPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleAlternateComparatorPacket.Handler.class, ToggleAlternateComparatorPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleRedstoneControlPacket.Handler.class, ToggleRedstoneControlPacket.class, nextID(), Side.SERVER);
		
		instance.registerMessage(OpenGuiPacket.Handler.class, OpenGuiPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(OpenTileGuiPacket.Handler.class, OpenTileGuiPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(OpenSideConfigGuiPacket.Handler.class, OpenSideConfigGuiPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleItemSorptionPacket.Handler.class, ToggleItemSorptionPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ResetItemSorptionsPacket.Handler.class, ResetItemSorptionsPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleItemOutputSettingPacket.Handler.class, ToggleItemOutputSettingPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleTankSorptionPacket.Handler.class, ToggleTankSorptionPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ResetTankSorptionsPacket.Handler.class, ResetTankSorptionsPacket.class, nextID(), Side.SERVER);
		instance.registerMessage(ToggleTankOutputSettingPacket.Handler.class, ToggleTankOutputSettingPacket.class, nextID(), Side.SERVER);
		
		instance.registerMessage(ClearAllMaterialPacket.Handler.class, ClearAllMaterialPacket.class, nextID(), Side.SERVER);
		
		// CLIENT
		instance.registerMessage(ConfigUpdatePacket.Handler.class, ConfigUpdatePacket.class, nextID(), Side.CLIENT);
		
		instance.registerMessage(BlockHighlightUpdatePacket.Handler.class, BlockHighlightUpdatePacket.class, nextID(), Side.CLIENT);
		
		instance.registerMessage(ProcessorUpdatePacket.Handler.class, ProcessorUpdatePacket.class, nextID(), Side.CLIENT);
		
		instance.registerMessage(FissionIrradiatorPortUpdatePacket.Handler.class, FissionIrradiatorPortUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(FissionCellPortUpdatePacket.Handler.class, FissionCellPortUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(FissionVesselPortUpdatePacket.Handler.class, FissionVesselPortUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(FissionHeaterPortUpdatePacket.Handler.class, FissionHeaterPortUpdatePacket.class, nextID(), Side.CLIENT);
		
		instance.registerMessage(FissionIrradiatorUpdatePacket.Handler.class, FissionIrradiatorUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(SolidFissionUpdatePacket.Handler.class, SolidFissionUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(SolidFissionCellUpdatePacket.Handler.class, SolidFissionCellUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(SaltFissionUpdatePacket.Handler.class, SaltFissionUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(SaltFissionVesselUpdatePacket.Handler.class, SaltFissionVesselUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(SaltFissionHeaterUpdatePacket.Handler.class, SaltFissionHeaterUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(HeatExchangerUpdatePacket.Handler.class, HeatExchangerUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(TurbineUpdatePacket.Handler.class, TurbineUpdatePacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(TurbineRenderPacket.Handler.class, TurbineRenderPacket.class, nextID(), Side.CLIENT);
		instance.registerMessage(QuantumComputerQubitRenderPacket.Handler.class, QuantumComputerQubitRenderPacket.class, nextID(), Side.CLIENT);
		
		instance.registerMessage(PlayerRadsUpdatePacket.Handler.class, PlayerRadsUpdatePacket.class, nextID(), Side.CLIENT);
	}
	
	private static int packetId = 0;
	
	public static int nextID() {
		return packetId++;
	}
}
