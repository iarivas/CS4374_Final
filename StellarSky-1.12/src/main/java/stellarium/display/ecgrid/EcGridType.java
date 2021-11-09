package stellarium.display.ecgrid;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.display.IDisplayElementType;
import stellarium.display.IDisplayRenderer;

public class EcGridType implements IDisplayElementType<EcGridSettings, EcGridCache> {

	@Override
	public EcGridSettings generateSettings() {
		return new EcGridSettings();
	}

	@Override
	public EcGridCache generateCache() {
		return new EcGridCache();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new EcGridRenderer();
	}

	@Override
	public String getName() {
		return "Ecliptic_Coordinate_Grid";
	}

}
