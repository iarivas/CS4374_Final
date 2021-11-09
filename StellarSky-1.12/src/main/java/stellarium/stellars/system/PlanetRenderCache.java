package stellarium.stellars.system;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class PlanetRenderCache implements IObjRenderCache<Planet, IConfigHandler> {
	
	protected boolean shouldRender, shouldRenderSurface;
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 pos = new Vector3();
	protected float brightness;
	protected float size;

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, Planet object) {

	}

	@Override
	public void updateCache(Planet object, ViewerInfo info) {
		pos.set(object.earthPos);
		info.coordinate.getProjectionToGround().transform(this.pos);
		pos.normalize();
		pos.scale(LayerRHelper.DEEP_DEPTH);

		// TODO Calculation fix, venus shouldn't be as high as -5.7 mag
		this.brightness = OpticsHelper.getBrightnessFromMag(object.currentMag);

		this.size = (float) (object.radius / object.earthPos.size());

		this.shouldRender = true;
		this.shouldRenderSurface = this.shouldRender && false;
		// MAYBE planet rendering, which needs over 100x multiplier
		this.brightness *= 0.5f;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return PlanetRenderer.INSTANCE;
	}

}
