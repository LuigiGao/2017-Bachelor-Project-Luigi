package EnergyGenerator;
import java.util.Random;

/**
 * This class contains information of one photovoltaic panel, could use to
 * calculate the power of that panel with given weather information
 * 
 * @author Luigi
 *
 */
public class PhotovoltaicPanel {

	/**
	 * Panel area of the photovoltaic panel (in 1m^2)
	 */
	private double panelArea;

	/**
	 * A double number represents the percentage of actual electric energy get from
	 * sun with the energy loss in transform solar energy to electric energy
	 */
	// efficiency in the interval (0, 1)
	private double efficiency;

	/**
	 * Maximum power of the photovoltaic panel (in 1W)
	 */
	private double maxPowerOutput;

	/**
	 * Build a photovoltaic panel
	 * 
	 * @param panelArea
	 *            Panel area of the photovoltaic panel
	 * @param maxPowerOutput
	 *            Maximum power of the photovoltaic panel
	 */
	public PhotovoltaicPanel(double panelArea, double maxPowerOutput) {
		this.panelArea = panelArea;
		this.efficiency = 1;// (new Random()).nextDouble();
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * Calculate the power of this photovoltaic panel with given solar radiation
	 * 
	 * @param solarRadiation
	 * @return The power of the photovoltaic panel in 1W
	 */
	public double powerOutput(double solarRadiation) {

		// panelArea unit m^2 to cm^2
		double joule = this.panelArea * 10000 * solarRadiation * efficiency;

		return Math.min(joule/3600, this.maxPowerOutput);
	}

	/** @return The panel area of the photovoltaic panel */
	public double getPanelArea() {
		return this.panelArea;
	}

	/** @return The string of information of the photovoltaic panel */
	public String info() {
		return "Panel Area " + this.panelArea + " m^2\n";
	}
}
