import java.util.Random;

/**
 * A wind turbine
 * @author Luigi
 *
 */

public class WindTurbine {

	/**
	 * blade length of the wind turbine
	 * unit: m
	 */
	private double bladeLength;
	
	/**
	 * area that swept by the blade of the wind turbine
	 * unit: m^2
	 */
	private double bladeSweptArea;
	
	/**
	 * represent the percentage of actual electric energy get from wind with the energy loss in transform wind energy to electric energy
	 */
	// efficiency in the interval (0, 1)
	private double efficiency;
	
	/**
	 * maximum power of the wind turbine
	 * unit: Watt
	 */
	private double maxPowerOutput;
	
	/**
	 * Build a wind turbine
	 * 
	 * @param bladeLength blade length of the wind turbine
	 * @param maxPowerOutput maximum power of the wind turbine
	 */
	public WindTurbine( double bladeLength, double maxPowerOutput ) {
		this.bladeLength = bladeLength;
		this.bladeSweptArea = Math.PI * Math.pow( this.bladeLength, 2);
		this.efficiency = (new Random()).nextDouble();
		this.maxPowerOutput = maxPowerOutput;
	}
	
	/**
	 * Calculate the power
	 * 
	 * @param airDensity
	 * @param windSpeed
	 * @return the power of the wind turbines
	 */
	public double powerOutput( double airDensity, double windSpeed ) {
		
		double power = 1/2 * this.bladeSweptArea * airDensity * Math.pow( windSpeed, 3) * this.efficiency;
		
		return Math.min( power, this.maxPowerOutput);
	}
	
	/** @return the blade length of the wind turbine */
	public double getBladeLength( ) {
		return this.bladeLength;
	}
	
	/** @return the area swept by blade of the wind turbine */
	public double getBladeSweptArea( ) {
		return this.bladeSweptArea;
	}
	
	/** @return the string of information of the wind turbine */
	public String info( ) {
		return "Blade Length: " + this.bladeLength + ", Swept Area: " + this.bladeSweptArea + "\n";
	}
	
}
