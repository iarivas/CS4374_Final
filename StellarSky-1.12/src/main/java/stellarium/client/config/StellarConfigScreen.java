package stellarium.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import stellarium.StellarSky;
import stellarium.StellarSkyReferences;

public class StellarConfigScreen extends GuiConfig {

	public StellarConfigScreen(GuiScreen parentScreen) {
		super(parentScreen, getConfigElement(), StellarSkyReferences.MODID, false, false, "Stellar Sky");
	}
	
	private static List<IConfigElement> getConfigElement() {
		Configuration config = StellarSky.INSTANCE.getCelestialConfigManager().getConfig();
		
		List<IConfigElement> retList = Lists.newArrayList();
		for(String category : config.getCategoryNames())
			if(!category.contains(Configuration.CATEGORY_SPLITTER))
				retList.add(new ConfigElement(config.getCategory(category)));
		return retList;
	}

}
