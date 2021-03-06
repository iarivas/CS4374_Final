package nc.util;

import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;

import mekanism.api.gas.IGasHandler;
import mekanism.common.base.ILogisticalTransporter;
import net.minecraftforge.common.capabilities.*;

public class CapabilityHelper {
	
	// Mekanism
	
	@CapabilityInject(IGasHandler.class)
	public static final Capability<IGasHandler> GAS_HANDLER_CAPABILITY = null;
	
	@CapabilityInject(ILogisticalTransporter.class)
	public static final Capability<ILogisticalTransporter> LOGISTICAL_TRANSPORTER_CAPABILITY = null;
	
	// Common Capabilities
	
	@CapabilityInject(ITemperature.class)
	public static final Capability<ITemperature> CAPABILITY_TEMPERATURE = null;
	
	@CapabilityInject(IWorker.class)
	public static final Capability<IWorker> CAPABILITY_WORKER = null;
}
