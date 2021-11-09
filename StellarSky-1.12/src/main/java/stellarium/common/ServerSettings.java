package stellarium.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.config.SimpleHierarchicalNBTConfig;
import stellarapi.api.lib.config.property.ConfigProperty;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;
import stellarium.stellars.layer.StellarLayerRegistry;

public class ServerSettings extends SimpleHierarchicalNBTConfig {
	public double day, year;
	public int yearOffset, dayOffset;
	public double tickOffset;

	private ConfigPropertyDouble propDay, propYear;
	private ConfigPropertyInteger propYearOffset, propDayOffset;
	private ConfigPropertyDouble propTickOffset;
	//private ConfigPropertyInteger propStartingYear, propClockDateOffset;
	public ConfigPropertyDouble propAxialTilt, propPrecession;

	public ServerSettings() {
		StellarLayerRegistry.getInstance().composeSettings(this);

		// Removed Server_Enabled.
		// On the server it's useless considering that all features are disabled.
		// On the client, it should obey what server want.
        this.propDay = new ConfigPropertyDouble("Day_Length", "day", 24000.0);
        this.propYear = new ConfigPropertyDouble("Year_Length", "year", 365.25);
        this.propYearOffset = new ConfigPropertyInteger("Year_Offset", "yearOffset", 0);
        this.propDayOffset = new ConfigPropertyInteger("Day_Offset", "dayOffset", 0);
        this.propTickOffset = new ConfigPropertyDouble("Tick_Offset", "tickOffset", 16000.0);

        //this.propStartingYear = new ConfigPropertyInteger("Starting_Year", "startingYear", 1);
        //this.propClockDateOffset = new ConfigPropertyInteger("Clock_Date_Offset", "clockDateOffset", 0);

        this.propAxialTilt = new ConfigPropertyDouble("Axial_Tilt", "axialTilt", 23.5);
        this.propPrecession = new ConfigPropertyDouble("Precession", "precession", 0.0);

       	this.addConfigProperty(this.propDay);
       	this.addConfigProperty(this.propYear);
       	this.addConfigProperty(this.propYearOffset);
       	this.addConfigProperty(this.propDayOffset);
       	this.addConfigProperty(this.propTickOffset);
       	this.addConfigProperty(this.propAxialTilt);
       	this.addConfigProperty(this.propPrecession);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for server modifications.");
		config.setCategoryLanguageKey(category, "config.category.server");
		config.setCategoryRequiresWorldRestart(category, true);
		
		super.setupConfig(config, category);
        
        propDay.setComment("Length of a day, in a tick.");
        propDay.setRequiresWorldRestart(true);
        propDay.setLanguageKey("config.property.server.daylength");
        
        propYear.setComment("Length of a year, in a day.");
        propYear.setRequiresWorldRestart(true);
        propYear.setLanguageKey("config.property.server.yearlength");

       	propYearOffset.setComment("Year offset on world starting time.");
       	propYearOffset.setRequiresWorldRestart(true);
       	propYearOffset.setLanguageKey("config.property.server.yearoffset");

       	propDayOffset.setComment("Day offset on world starting time.");
       	propDayOffset.setRequiresWorldRestart(true);
       	propDayOffset.setLanguageKey("config.property.server.dayoffset");

       	propTickOffset.setComment("Tick offset on world starting time.");
       	propTickOffset.setRequiresWorldRestart(true);
       	propTickOffset.setLanguageKey("config.property.server.tickoffset");

        propAxialTilt.setComment("Axial tilt in degrees. Always 0.0 when Server_Enabled is false.");
        propAxialTilt.setRequiresWorldRestart(true);
        propAxialTilt.setLanguageKey("config.property.server.axialtilt");

       	propPrecession.setComment("Precession in degrees per year.");
       	propPrecession.setRequiresWorldRestart(true);
       	propPrecession.setLanguageKey("config.property.server.precession");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
       	super.loadFromConfig(config, category);
       	this.setValues();
	}
	
	/**Default for servers without Stellar Sky*/
	public void setDefault() {
		for(ConfigProperty property : this.listProperties)
			property.setAsDefault();
    	propAxialTilt.setDouble(0.0);
    	propTickOffset.setDouble(17500.0);
    	this.tickOffset = 17500.0;
    	
    	this.setValues();
	}
	
	private void setValues() {
		this.day = propDay.getDouble();
       	this.year = propYear.getDouble();
       	this.yearOffset = propYearOffset.getInt();
       	this.dayOffset = propDayOffset.getInt();
       	this.tickOffset = propTickOffset.getDouble();
	}

	
	public void readFromNBT(NBTTagCompound compound) {
       	super.readFromNBT(compound);
       	
       	this.day = propDay.getDouble();
       	this.year = propYear.getDouble();
       	this.yearOffset = propYearOffset.getInt();
       	this.dayOffset = propDayOffset.getInt();
       	this.tickOffset = propTickOffset.getDouble();
	}

	@Override
	public INBTConfig copy() {
		ServerSettings settings = new ServerSettings();
		settings.day = this.day;
		settings.year = this.year;
		settings.yearOffset = this.yearOffset;
		settings.dayOffset = this.dayOffset;
		settings.tickOffset = this.tickOffset;
		this.applyCopy(settings);
		return settings;
	}
}
