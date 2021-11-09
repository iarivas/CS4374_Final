package stellarium.world;

import com.google.common.collect.ImmutableMap;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.WaveIntensive;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.OpticsHelper;

public class NonRefractiveSkySet implements IStellarSkySet {
	
	private boolean hideObjectsUnderHorizon;
	private float lightPollutionFactor, dispersionFactor, minimumSkyRenderBrightness;
	
	private WaveIntensive interpolation;
	
	public NonRefractiveSkySet(PerDimensionSettings settings) {
		this.hideObjectsUnderHorizon = settings.hideObjectsUnderHorizon();
		this.dispersionFactor = (float) settings.getSkyDispersionRate();
		this.lightPollutionFactor = (float) settings.getLightPollutionRate();
		this.minimumSkyRenderBrightness = (float) settings.getMinimumSkyRenderBrightness();
		
		double[] rates = settings.extinctionRates();
		this.interpolation = new WaveIntensive(
				ImmutableMap.of(
						Wavelength.red, OpticsHelper.getMultFromMag(rates[0]),
						Wavelength.V, OpticsHelper.getMultFromMag(rates[1]),
						Wavelength.B, OpticsHelper.getMultFromMag(rates[2]))
				);
	}

	@Override
	public void applyAtmRefraction(SpCoord coord) { }

	@Override
	public void disapplyAtmRefraction(SpCoord coord) { }

	@Override
	public float calculateAirmass(SpCoord coord) {
		return 0.0f;
	}

	@Override
	public boolean hideObjectsUnderHorizon() {
		return this.hideObjectsUnderHorizon;
	}

	@Override
	public float getAbsorptionFactor(float partialTicks) {
		//Assume that there is no absorption.
		return 0.0f;
	}

	@Override
	public float getDispersionFactor(Wavelength wavelength, float partialTicks) {
		return this.dispersionFactor;
	}

	@Override
	public float getExtinctionRate(Wavelength wavelength) {
		return (float) OpticsHelper.getMagFromMult(interpolation.apply(wavelength).doubleValue());
	}

	@Override
	public float getLightPollutionFactor(Wavelength wavelength, float partialTicks) {
		return this.lightPollutionFactor;
	}

	@Override
	public double getSeeing(Wavelength wavelength) {
		return 0.0;
	}

	@Override
	public float minimumSkyRenderBrightness() {
		return this.minimumSkyRenderBrightness;
	}

}
