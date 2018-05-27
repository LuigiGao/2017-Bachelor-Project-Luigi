package Network;

import java.util.ArrayList;
import java.util.Random;

import EnergyGenerator.PhotovoltaicPanel;
import EnergyGenerator.WindTurbine;
import Parser.ProsumerConsumptionParser;

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

	// extra
	// add a storage
	// private ArrayList<Double> recording;
	// private double energy_remaining;
	// private double[] previous_energy;

	/** A array list of daily consumption of energy of the prosumer */
	private double[] energy_consumption;

	private Random random;

	/**
	 * Build a house that both consume and produce energy
	 * 
	 * @param id
	 *            A number that automatically generate by program to represent a
	 *            prosumer
	 * @param houseNumber
	 *            A identity of prosumer
	 */
	public Prosumer(int id, int houseNumber) {
		this.id = id;
		this.houseNumber = houseNumber;
		this.windTurbines = new ArrayList<WindTurbine>();
		this.photovoltaicPanels = new ArrayList<PhotovoltaicPanel>();
		this.random = new Random();
	}

	public Prosumer(int id, int houseNumber, String consumption) {
		this(id, houseNumber);
		this.energy_consumption = (new ProsumerConsumptionParser(consumption)).parseConsumption();
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
	 * @return A id that automatically generate by program to represent a prosumer
	 */
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

	/** @return A price varies between 0.06 and 0.08 per kWh */
	public double getCurrentPrice() {
		return random.nextDouble() * (0.08 - 0.06) + 0.06;
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
		info = info + "Energy Consumption:\n";
		for (int i = 0; i < 24; i++) {
			info = info + "  at " + i + ": " + this.energy_consumption[i] + "\n";
		}
		return info;
	}

	/**
	 * Calculate the power of all renewable energy producers with given weather
	 * information
	 * 
	 * @param airDensity
	 * @param windSpeed
	 * @param solarRadiation
	 * @return the total power combining of all wind turbines and photovoltaic
	 *         panels (in 1W)
	 */
	public double powerOutput(double airDensity, double windSpeed, double solarRadiation) {
		double power = 0;

		for (WindTurbine wt : this.windTurbines) {
			power = power + wt.powerOutput(airDensity, windSpeed);
		}
		for (PhotovoltaicPanel pp : this.photovoltaicPanels) {
			power = power + pp.powerOutput(solarRadiation);
		}

		return power;
	}

	/**
	 * 
	 * @param hour time in a day
	 * @return energy that the prosumer use in that hour (in 1kWh)
	 */
	// also the output when renewable energy generator is not considered
	public double energyConsume(int hour) {

		// hour is within one day, so is between 0 and 23
		if (hour < 0 || hour > 23) {
			System.out.println("Error: Time(in hour) is out of bound");
			return 0;
		}

		// assume the energy consumption data at hour is a mean in kWh
		double base = this.energy_consumption[hour];

		// assume edges of plus/negative 20% is bound 95% of area of normal distribution
		// and calculate standard deviation
		double stdDev = (0.4 * base) / (2 * 1.96);
		//return 0;
		return Math.max(random.nextGaussian() * stdDev + base, 0);

	}
	
	/**
	 * Calculate the total net energy for given hour
	 * @param airDensity
	 * @param windSpeed
	 * @param solarRadiation
	 * @param duration_h
	 * @return energy in kWh
	 */
	public double output(double airDensity, double windSpeed, double solarRadiation, int duration_h) {

		// energy production in 1 hour
		// energy = power * time
		double energy_production = powerOutput(airDensity, windSpeed, solarRadiation) / 1000 * 1; // in kWh
		double energy_consumption = energyConsume(duration_h); // in kWh

		// energy_remaining is the remaining energy from previous
		// double total_energy = energy_production + this.energy_remaining - energy_consumption;
		double total_energy = energy_production - energy_consumption;

		//double result = record(total_energy, duration_h);

		return total_energy;
		// return result;
	}

	/* extra prediction
	// predict production - consumption for next hour
	private double predictingConsumption(int duration_h) {
		// not finish
		return 0;
	}

	// net_energy > 0 here
	private double record(double net_energy, int duration_h) {

		// add new record (addition)
		// this.recording.add(net_energy, duration_h);

		if (net_energy < 0) {
			return net_energy;
		}

		// predict future net_energy
		double prediction = predictingConsumption(duration_h);

		double result = net_energy;

		// energy at storage is recalculate
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
	*/
}
