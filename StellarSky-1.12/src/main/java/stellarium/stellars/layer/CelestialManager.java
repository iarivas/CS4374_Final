package stellarium.stellars.layer;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.StellarManager;

public class CelestialManager {

	private boolean isRemote;
	private boolean commonInitialized = false;
	private List<StellarCollection> layers = Lists.newArrayList();
	
	private CelestialManager() { }
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		StellarLayerRegistry registry = StellarLayerRegistry.getInstance();
		registry.composeLayer(isRemote, this.layers);
	}
	
	public List<StellarCollection> getLayers() {
		return this.layers;
	}
	
	public void initializeClient(ClientSettings settings) {		
		StellarSky.INSTANCE.getLogger().info("Initializing Celestial Layers with Client Settings...");
		String layerName = null;
		try {
			for(StellarCollection layer : this.layers) {
				layerName = layer.getConfigName();
				layer.getType().initializeClient(layerName != null? settings.getSubConfig(layerName) : null, layer);
			}
		} catch(Exception exception) {
	    	StellarSky.INSTANCE.getLogger().fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.INSTANCE.getLogger().info("Successfully initialized Celestial Layers with Client Settings!");
	}
	
	public void initializeCommon(StellarManager manager, ServerSettings settings) {
		StellarSky.INSTANCE.getLogger().info("Initializing Celestial Layers with Common Settings...");
		String layerName = null;
		try {
			for(StellarCollection layer : this.layers) {
				layer.setManager(manager);
				layerName = layer.getConfigName();
				layer.getType().initializeCommon(layerName != null? settings.getSubConfig(layerName) : null, layer);
			}
		} catch(Exception exception) {
	    	StellarSky.INSTANCE.getLogger().fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.INSTANCE.getLogger().info("Successfully initialized Celestial Layers with Common Settings!");
    	this.commonInitialized = true;
	}
	
	public void update(double currentYear) {
		for(StellarCollection layer : this.layers)
			layer.getType().updateLayer(layer, currentYear);
	}


	public CelestialManager copyFromClient() {
		CelestialManager copied = new CelestialManager();
		copied.isRemote = this.isRemote;
		copied.layers = Lists.newArrayList(
				Iterables.transform(this.layers,
						new Function<StellarCollection, StellarCollection>() {
							@Override
							public StellarCollection apply(StellarCollection input) {
								return input.copyFromClient();
							}
				}));

		return copied;
	}
	
	public boolean commonInitialized() {
		return this.commonInitialized;
	}

}
