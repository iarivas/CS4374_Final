package stellarium.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.view.ViewerInfo;

public interface IRenderCache<Obj extends StellarObject, Config extends IConfigHandler> {
	
	public void initialize(ClientSettings settings, Config specificSettings, Obj object);
	
	public void updateCache(ClientSettings settings, Config specificSettings, Obj object, ViewerInfo info);
	
	public int getRenderId();
	
}
