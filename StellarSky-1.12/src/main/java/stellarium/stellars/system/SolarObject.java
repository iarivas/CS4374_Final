package stellarium.stellars.system;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.observe.SearchRegion;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarObject;

public abstract class SolarObject extends StellarObject {	
	protected Vector3 relativePos = new Vector3();
	protected Vector3 sunPos = new Vector3();
	protected Vector3 earthPos = new Vector3();

	/**Magnitude from earth without atmosphere*/
	protected double currentMag;

	/**Albedo*/
	protected double albedo;

	/**Radius*/
	protected double radius;

	/**Mass*/
	protected double mass;

	/**Parent*/
	protected final SolarObject parent;
	private final int level;

	protected final double yearUnit;

	public SolarObject(String name, EnumObjectType type, double yearUnit) {
		super(name, new ResourceLocation(StellarSkyReferences.MODID, name), type);
		this.parent = null;
		this.level = 0;
		this.yearUnit = yearUnit;
	}

	public SolarObject(String name, SolarObject parent, EnumObjectType type, double yearUnit) {
		super(name, new ResourceLocation(StellarSkyReferences.MODID, name), type);
		this.parent = parent;
		this.level = parent.level + 1;
		this.yearUnit = yearUnit;
	}

	public void initialize() { }

	protected Vector3 initialEarthPos;
	public void initialUpdate() {
		this.initialEarthPos = this.earthPos;
	}

	public boolean isIn(SearchRegion region, SpCoord cache) {
		// TODO Find Override this method on Sun and Moon
		return region.test(cache.setWithVec(this.earthPos));
	}

	public Vector3 positionTo(SolarObject object) {
		if(this == object)
			return new Vector3(0.0, 0.0, 0.0);
		try {
			if(object.level < this.level) {
				Vector3 vector = parent.positionTo(object);
				vector.add(this.relativePos);
				return vector;
			} else {
				Vector3 vector = this.positionTo(object.parent);
				vector.sub(object.relativePos);
				return vector;
			}
		} catch(NullPointerException exception) {
			throw new IllegalArgumentException(String.format(
					"Tried to compare position between non-related objects: %s and %s!",
					this, object));
		}
	}

	public void updatePre(double year) {
		if(this.parent != null)
			relativePos.set(this.getRelativePos(year));
	}

	public void updateModulate() { }

	public void updatePos(SolarObject sun, SolarObject earth) {
		earthPos.set(this.positionTo(earth));
		sunPos.set(this.positionTo(sun));
	}

	public void updatePost(SolarObject earth) {
		if(this != earth)
			this.updateMagnitude(earth.sunPos);
	}

	protected void updateMagnitude(Vector3 earthFromSun){
		double dist=earthPos.size();
		double distS=sunPos.size();
		double distE=earthFromSun.size();
		double LvsSun=this.radius*this.radius*this.getCurrentPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
	}

	public abstract double absoluteOffset();

	@Override
	public double getCurrentPhase() {
		return (1 + sunPos.dot(this.earthPos) / (sunPos.size() * earthPos.size())) / 2;
	}

	public double phaseOffset() {
		Vector3 crossed = new Vector3();
		crossed.setCross(this.earthPos, this.sunPos);
		double k=Math.signum(crossed.dot(new Vector3(0.0, 0.0, 1.0))) * (1.0 - this.getCurrentPhase());
		if(k<0) k=k+2;
		return k/2;
	}

	@Override
	public Vector3 getCurrentPos() {
		return this.earthPos;
	}

	public abstract Vector3 getRelativePos(double year);

}
