import java.util.Random;

/**
 * This class contains information of one wind turbine, could use to calculate
 * the power of that wind turbine with given weather information
 * 
 * @author Luigi
 *
 */

public class WindTurbine {

	/**
	 * Blade length of the wind turbine (in 1m)
	 */
	private double bladeLength;

	/**
	 * Area that swept by the blade of the wind turbine (in 1m^2)
	 */
	private double bladeSweptArea;

	/**
	 * A double number represents the percentage of actual electric energy get from
	 * wind with the energy loss in transform wind energy to electric energy
	 */
	// efficiency in the interval (0, 1)
	private double efficiency;

	/**
	 * Maximum power of the wind turbine unit: Watt
	 */
	private double maxPowerOutput;

	/**
	 * Build a wind turbine
	 * 
	 * @param bladeLength
	 *            Blade length of the wind turbine
	 * @param maxPowerOutput
	 *            Maximum power of the wind turbine
	 */
	public WindTurbine(double bladeLength, double maxPowerOutput) {
		this.bladeLength = bladeLength;
		this.bladeSweptArea = Math.PI * Math.pow(this.bladeLength, 2);
		this.efficiency = (new Random()).nextDouble();
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * Calculate the power of this wind turbine with given air density and wind
	 * speed
	 * 
	 * @param airDensity
	 * @param windSpeed
	 * @return the power of the wind turbines
	 */
	public double powerOutput(double airDensity, double windSpeed) {

		double power = 1 / 2 * this.bladeSweptArea * airDensity * Math.pow(windSpeed, 3) * this.efficiency;

		return Math.min(power, this.maxPowerOutput);
	}

	/** @return The blade length of the wind turbine */
	public double getBladeLength() {
		return this.bladeLength;
	}

	/** @return The area swept by blade of the wind turbine */
	public double getBladeSweptArea() {
		return this.bladeSweptArea;
	}

	/** @return The string of information of the wind turbine */
	public String info() {
		return "Blade Length: " + this.bladeLength + ", Swept Area: " + this.bladeSweptArea + "\n";
	}

}
