import java.util.ArrayList;

/**
 * This class contains information about one prosumer, is used to simulate the
 * energy production and consumption of that prosumer
 * 
 * @author Luigi
 *
 */

public class Prosumer {

	/** A number that automatically generate by program to represent a prosumer */
	// which is also the index of column and row
	private int id;

	/** House number of the prosumer */
	private int houseNumber;

	/** A array of wind turbines */
	private ArrayList<WindTurbine> windTurbines;

	/** A array of photovoltaic panels */
	private ArrayList<PhotovoltaicPanel> photovoltaicPanels;

	// add a storage
	private ArrayList<Double> recording;
	private double energy_remaining;

	/** A array list of daily consumption of energy of the prosumer */
	private ArrayList<Double> energy_consumption;

	private double net_profit = 0;

	/**
	 * Build a house that both consume and produce energy
	 * 
	 * @param id
	 *            A number that automatically generate by program to represent a prosumer
	 * @param houseNumber
	 *            A identity of prosumer
	 */
	public Prosumer(int id, int houseNumber) {
		this.id = id;
		this.houseNumber = houseNumber;
		this.windTurbines = new ArrayList<WindTurbine>();
		this.photovoltaicPanels = new ArrayList<PhotovoltaicPanel>();
	}

	/**
	 * Add a wind turbine to the prosumer
	 * 
	 * @param wt
	 *            wind turbine
	 */
	public void addWindTurbine(WindTurbine wt) {
		this.windTurbines.add(wt);
	}

	/**
	 * Add a photovoltaic panel to the prosumer
	 * 
	 * @param pp
	 *            photovoltaic panel
	 */
	public void addPhotovoltaicPanel(PhotovoltaicPanel pp) {
		this.photovoltaicPanels.add(pp);
	}

	/**
	 * Calculate the power of all renewable energy producers with given weather information
	 * 
	 * @param airDensity
	 * @param windSpeed
	 * @param solarRadiation
	 * @return the total power combining of all wind turbines and photovoltaic
	 *         panels
	 */
	public double power(double airDensity, double windSpeed, double solarRadiation) {
		double power = 0;

		for (WindTurbine wt : this.windTurbines) {
			power = power + wt.powerOutput(airDensity, windSpeed);
		}
		for (PhotovoltaicPanel pp : this.photovoltaicPanels) {
			power = power + pp.powerOutput(solarRadiation);
		}

		return power * 60;
	}

	/** @return A id that automatically generate by program to represent a prosumer */
	public int getID() {
		return this.id;
	}

	/** @return The house number of the prosumer */
	public int getHouseNumber() {
		return this.houseNumber;
	}

	/** @return A array list of wind turbines */
	public ArrayList<WindTurbine> getWindTurbines() {
		return this.windTurbines;
	}

	/** @return A array list of photovoltaic panels */
	public ArrayList<PhotovoltaicPanel> getPhotovoltaicPanels() {
		return this.photovoltaicPanels;
	}

	/**
	 * @return A string of house number, information of wind turbines, and
	 *         information of photovoltaic panels
	 */
	public String info() {
		String info = "House number: " + this.houseNumber + "\n";
		info = info + "Wind Turbines:\n";
		for (WindTurbine wt : this.windTurbines) {
			info = info + wt.info();
		}
		info = info + "Photovoltaic Panels:\n";
		for (PhotovoltaicPanel pp : this.photovoltaicPanels) {
			info = info + pp.info();
		}

		return info;
	}

	// predict production - consumption for next hour
	private double predictingConsumption(int duration_h ) {
		1
		return 0;
	}

	// net_energy > 0 here
	private double record(double net_energy, int duration_h) {

		// add new record
		this.recording.add(net_energy, duration_h);

		if (net_energy < 0) {
			return net_energy;
		}

		// predict future net_energy
		double prediction = predictingConsumption(duration_h);

		double result = net_energy;
		if (prediction < 0) {
			if (result - prediction < 0) {
				this.energy_remaining = result;
				result = 0;
			} else {
				this.energy_remaining = prediction;
				result = result - prediction;
			}
		}

		return result;
	}

	private double output(double airDensity, double windSpeed, double solarRadiation, int duration_h) {

		// energy production in 1 hour
		double energy_production = power(airDensity, windSpeed, solarRadiation) * 1 * 60 * 60;
		double net_energy = energy_production + this.energy_remaining - this.energy_consumption.get(duration_h);
		this.energy_remaining = 0;

		double result = record(net_energy, duration_h);

		return result;
	}
}
