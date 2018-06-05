package Network;

import java.util.ArrayList;
import java.util.Random;

import Parser.ProsumerConsumptionParser;
import PhotovoltaicPanel.*;
import WindTurbine.*;

/**
 * This class contains information about one prosumer, is used to simulate the
 * energy production and consumption of that prosumer
 * 
 * Resource:
 * https://en.wikipedia.org/wiki/Wind_power_in_the_Netherlands
 * Electricity consumption and household characteristics: Implications for census-taking in a smart metered future
 * https://www.cbs.nl/en-gb/news/2017/22/share-of-renewable-energy-at-5-9-in-2016
 * 
 * @author Luigi
 *
 */

public class Prosumer {

	/** A number that automatically generate by program to represent the prosumer */
	// which is also the index of column and row
	private int id;

	/** Prosumer's name */
	private String householder;

	/** A array of wind turbines */
	private ArrayList<WindTurbine> windTurbines;

	/** A array of photovoltaic panels */
	private ArrayList<PhotovoltaicPanel> photovoltaicPanels;

	/** A array list of hourly mean consumption of energy of the prosumer */
	private double[] energy_consumption;

	private Random random;

	/**
	 * Build a prosumer, randomly generate wind turbines and PV panels, 
	 * and randomly select energy consumption
	 * 
	 * @param id
	 * @param householder
	 */
	public Prosumer(int id, String household) {
		this.id = id;
		this.householder = household;
		this.random = new Random();

		// random generate wind turbines and PV panels
		generateWindTurbine( );
		generatePVpanel( );
		
		// random generate the consumption
		String[] consumptions = { "EnergyConsumption_PaidWork.json",
								  "EnergyConsumption_Relative.json",
								  "EnergyConsumption_Unemployed.json",
								  "EnergyConsumption_Retired.json" };
		this.energy_consumption = (new ProsumerConsumptionParser( "src/Data/" + consumptions[random.nextInt(4)] )).parseConsumption();
	}

	/** Estimate the wind turbines owned by the prosumer */
	private void generateWindTurbine() {
		this.windTurbines = new ArrayList<WindTurbine>();

		double percentOfOwner = 0.3;
		if (random.nextDouble() <= percentOfOwner) {
			double nWindTurbines = random.nextInt(2) + 1;
			for (int i = 0; i < nWindTurbines; i++) {
				switch (random.nextInt(3)) {
				case 0:
					this.windTurbines.add(new BergeyExcel10());
					break;
				case 1:
					this.windTurbines.add(new BritwindR9000());
					break;
				case 2:
					this.windTurbines.add(new TheSkystream37());
					break;
				}
			}
		}
	}

	/** Estimate the PV panels owned by the prosumer */
	private void generatePVpanel() {
		this.photovoltaicPanels = new ArrayList<PhotovoltaicPanel>();

		double percentOfOwner = 0.6;
		if (random.nextDouble() <= percentOfOwner) {
			double nPVpanels = random.nextInt(11) + 10;
			switch (random.nextInt(3)) {
			case 0:
				for (int i = 0; i < nPVpanels; i++) {
					this.photovoltaicPanels.add(new AllmaxM_Plus());
				}
				break;
			case 1:
				for (int i = 0; i < nPVpanels; i++) {
					this.photovoltaicPanels.add(new CSUN_S156_5BB());
				}
				break;
			case 2:
				for (int i = 0; i < nPVpanels; i++) {
					this.photovoltaicPanels.add(new NeONR_LG365Q1C_A5());
				}
				break;
			}
		}
	}

	/**
	 * Add one wind turbine to the prosumer
	 * 
	 * @param wt Wind turbine
	 */
	public void addWindTurbine(WindTurbine wt) {
		this.windTurbines.add(wt);
	}

	/**
	 * Add a photovoltaic panel to the prosumer
	 * 
	 * @param pp PV panel
	 */
	public void addPhotovoltaicPanel(PhotovoltaicPanel pp) {
		this.photovoltaicPanels.add(pp);
	}

	/** @return A id that represents a prosumer */
	public int getID() {
		return this.id;
	}

	/** @return The name of householder */
	public String getHousehold() {
		return this.householder;
	}

	/** @return A array list of wind turbines */
	public ArrayList<WindTurbine> getWindTurbines() {
		return this.windTurbines;
	}

	/** @return A array list of PV panels */
	public ArrayList<PhotovoltaicPanel> getPhotovoltaicPanels() {
		return this.photovoltaicPanels;
	}

	/** @return A estimated price varies between 0.06 and 0.16 per kWh */
	public double getCurrentPrice() {
		// the prive varying form electric utility's selling and buying price
		return random.nextDouble() * (0.16 - 0.06) + 0.06;
	}

	/** @return The name of householder, wind turbines, and PV panels */
	public String getInfo() {
		String info = "Householder: " + this.householder + "\n";
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
	
	/** @return The name of householder, number of wind turbines and PV panels */
	public String getGeneratorInfo() {
		String info = "Householder: " + this.householder + " ";
		info = info + "Wind Turbines: " + windTurbines.size() + " ";
		info = info + "Photovoltaic Panels: " + photovoltaicPanels.size() + "\n";
		return info;
	}
	/**
	 * Estimate the energy generated by prosumer for one hour
	 * 
	 * @param airDensity Air Density in 1 kg/m^3
	 * @param windSpeed Wind Speed in 1 m/s
	 * @param solarRadiation Solar Radiation in 1 kW/m^2
	 * @return Energy in 1 kWh
	 */
	public double energyProduce(double minWindSpeed, double maxWindSpeed, double solarRadiation, double temperature) {
		double energy = 0;
		
		if( solarRadiation != 0 ) {
			
			double error = solarRadiation/10; 
			// take a random number in bound +-10%
			double estimateSolarRadiation = solarRadiation - error + 2 * error * random.nextDouble(); // kWh/m^2
			
			for (PhotovoltaicPanel pp : this.photovoltaicPanels) {
				energy += pp.output(estimateSolarRadiation, temperature);
			}
		}
		
		for (WindTurbine wt : this.windTurbines) {
			energy += wt.output(minWindSpeed, maxWindSpeed);
		}
		
		return energy;
	}

	/**
	 * Estimate the energy consumed by the prosumer within given time
	 * 
	 * @param time_in_day Time in a day between 0 and 24
	 * @return Energy in 1kWh
	 */
	// The output when renewable energy generator is not considered
	public double energyConsume(int time_in_day) {

		if (time_in_day < 0 || time_in_day > 23) {
			System.out.println("Error: Time(in hour) is out of bound");
			return 0;
		}

		// the mean energy consumption data in kWh
		double base = this.energy_consumption[time_in_day];

		// assume edges of plus/negative 20% is bound 95% of area of normal distribution
		// and calculate standard deviation
		double stdDev = (0.4 * base) / (2 * 1.96);
		return Math.max(random.nextGaussian() * stdDev + base, 0);

	}

	/**
	 * Estimate the total net energy for given hour
	 * 
	 * @param minWindSpeed Minimum wind speed in 1 m/s
	 * @param maxWindSpeed Maximum wind speed in 1 m/s
	 * @param solarRadiation Solar radiation in 1 kW/m^2
	 * @param temperature Temperature in 1 degree Celsius
	 * @param time_in_day Time in a day between 0 and 24
	 * @return Energy in 1 kWh
	 */
	public double output(double minWindSpeed, double maxWindSpeed, double solarRadiation, double temperature, int time_in_day) {

		// energy production in 1 hour
		double energy_production = energyProduce(minWindSpeed, maxWindSpeed, solarRadiation, temperature); // in kWh
		double energy_consumption = energyConsume(time_in_day); // in kWh

		double total_energy = energy_production - energy_consumption;

		return total_energy;
	}

}
