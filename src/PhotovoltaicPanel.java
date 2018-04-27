import java.util.Random;

/**
 * A photovoltaic panel
 * @author Luigi
 *
 */
public class PhotovoltaicPanel {

	/**
	 * panel area of the photovoltaic panel
	 * unit: m^2
	 */
	private double panelArea;

	/** 
	 * represent the percentage of actual electric energy get from sun with the energy loss in transform solar energy to electric energy
	 */
	// efficiency in the interval (0, 1)
	private double efficiency;

	/**
	 * maximum power of the photovoltaic panel
	 * unit: Watt
	 */
	private double maxPowerOutput;

	/**
	 * Build a photovoltaic panel
	 * 
	 * @param panelArea panel area of the photovoltaic panel
	 * @param maxPowerOutput maximum power of the photovoltaic panel
	 */
	public PhotovoltaicPanel( double panelArea, double maxPowerOutput ) {
		this.panelArea = panelArea;
		this.efficiency = (new Random()).nextDouble();
		this.maxPowerOutput = maxPowerOutput;
	}
	
	/**
	 * Calculate the power
	 * 
	 * @param solarRadiation
	 * @return the power of the photovoltaic panel
	 */
	public double powerOutput( double solarRadiation ) {
		
		double power = this.panelArea * solarRadiation * efficiency;
		
		return Math.min( power, this.maxPowerOutput);
	}

	/** @return the panel area of the photovoltaic panel */
	public double getPanelArea( ) {
		return this.panelArea;
	}
	
	/** @return the string of information of the photovoltaic panel */
	public String info( ) {
		return "Panel Area: " + this.panelArea + "\n";
	}
}
