package Analysis;

import Network.Network;

/**
 * A class that store data when simulation and analysis those data
 * 
 * 
 * @author Luigi
 *
 */

public class Analyzer {

	/** Network that simulate smart grid */
	private Network network;
	
	/** Number of prosumers in network */
	private int size;

	/** Total energy produced by each prosumer */
	private double[] energy_produce;
	
	/** Total energy consumed by each prosumer */
	private double[] energy_consume;
	
	/** Total energy bought from other prosumers by each prosumer */
	private double[] energy_buy_from_prosumers;

	/** Total energy bought from electric utility by each prosumer */
	private double[] energy_buy_from_eu;

	/** Total cost for buying energy from other prosumers by each prosumer */
	private double[] cost_from_prosumers;

	/** Total cost for buying energy from electric utility by each prosumer */
	private double[] cost_from_eu;
	
	/** Total cost for buying energy by each prosumer */
	private double[] cost_total;

	/** Energy selled to other prosumers by each prosumer */
	private double[] energy_sell_to_prosumers;

	/** Energy selled to electric utility by each prosumer */
	private double[] energy_sell_to_eu;

	/** Total profit for selling energy to other prosumers by each prosumer */
	private double[] profit_from_prosumers;

	/** Total profit for selling energy to electric utility by each prosumer */
	private double[] profit_from_eu;

	/** Total profit for selling energy by each prosumer */
	private double[] profit_total;

	/** Sum of all energy transmit */
	private double total_energy_transmit;

	/** Sum of all energy loss */
	private double total_energy_loss;

	/**
	 * Build the analyzer for network
	 * @param size Number of prosumers
	 * @param network
	 */
	public Analyzer(int size, Network network) {
		this.size = size;
		this.network = network;
		initial();
	}

	/** Initial variables */
	private void initial() {
		
		this.energy_produce = new double[size];
		this.energy_consume = new double[size];
		
		this.energy_buy_from_prosumers = new double[size];
		this.energy_buy_from_eu = new double[size];

		this.cost_from_prosumers = new double[size];
		this.cost_from_eu = new double[size];
		this.cost_total = new double[size];

		this.energy_sell_to_prosumers = new double[size];
		this.energy_sell_to_eu = new double[size];

		this.profit_from_prosumers = new double[size];
		this.profit_from_eu = new double[size];
		this.profit_total = new double[size];

		for (int i = 0; i < size; i++) {
			this.energy_produce[i] = 0;
			this.energy_consume[i] = 0;
			this.energy_buy_from_prosumers[i] = 0;
			this.energy_buy_from_eu[i] = 0;
			this.cost_from_prosumers[i] = 0;
			this.cost_from_eu[i] = 0;
			this.cost_total[i] = 0;
			this.energy_sell_to_prosumers[i] = 0;
			this.energy_sell_to_eu[i] = 0;
			this.profit_from_prosumers[i] = 0;
			this.profit_from_eu[i] = 0;
			this.profit_total[i] = 0;
		}
		this.total_energy_transmit = 0;
		this.total_energy_loss = 0;

	}

	/** Record the energy produced by the prosumer */
	public void energyProdcution(int prosumer, double energy ) {
		this.energy_produce[prosumer] += energy;
	}

	/** Record the energy consumed by the prosumer */
	public void energyConsumption(int prosumer, double energy ) {
		this.energy_consume[prosumer] += energy;
	}

	/** Record the energy flow */
	public void energyFlow(int provider, int consumer, double energy, double loss, double price) {
		this.energy_buy_from_prosumers[consumer] += energy;
		this.cost_from_prosumers[consumer] += energy * price;
		this.cost_total[consumer] += energy * price;

		this.energy_sell_to_prosumers[provider] += energy;
		this.profit_from_prosumers[provider] += energy * price;
		this.profit_total[provider] += energy * price;

		this.total_energy_transmit += energy;
		this.total_energy_loss += loss;
	}

	/** Record the energy bought from electric utility by the prosumer */
	public void buyEnergyFromEU(int consumer, double energy, double loss, double price) {
		this.energy_buy_from_eu[consumer] += energy;
		this.cost_from_eu[consumer] += energy * price;
		this.cost_total[consumer] += energy * price;

		this.total_energy_transmit += energy;
		this.total_energy_loss += loss;
	}

	/** Record the energy sold to electric utility by the prosumer */
	public void sellEnergyToEU(int provider, double energy, double loss, double price) {
		this.energy_sell_to_eu[provider] += energy;
		this.profit_from_eu[provider] += energy * price;
		this.profit_total[provider] += energy * price;

		this.total_energy_transmit += energy;
		this.total_energy_loss += loss;
	}

	/** Round the value to fixed digits */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	/** Extends string to fix length by adding space at end */
	private void printPrefix(String s, int length) {
		while (s.length() < length)
			s = s + " ";
		System.out.print(s);
	}

	/** Extends string to fix length by adding space at beginning */
	private void printName(String s, int length) {
		while (s.length() < length)
			s = " " + s;
		System.out.print(s);
	}
	
	/** Print the array of data */
	private void printData(double[] data) {
		for (int i = 0; i < size; i++) {
			String str = Double.toString(round(data[i], 2));
			while (str.length() < 8)
				str = " " + str;
			System.out.print(" " + str);
		}
	}

	/** Print the data stored in analyzer */
	private void printData() {

		printPrefix("Prosumers:", 30);
		for (int i = 0; i < size; i++) {
			printName( network.getProsumer(i).getHousehold(), 9);
		}
		System.out.println();
		
		printPrefix("Energy Production:", 30);
		printData(this.energy_produce);
		System.out.println();
		
		printPrefix("Energy Consumption:", 30);
		printData(this.energy_consume);
		System.out.println();
		
		printPrefix("Energy Buy From Prosumers:", 30);
		printData(this.energy_buy_from_prosumers);
		System.out.println();

		printPrefix("Energy Buy From EU:", 30);
		printData(this.energy_buy_from_eu);
		System.out.println();
		
		printPrefix("Cost To Prosumers:", 30);
		printData(this.cost_from_prosumers);
		System.out.println();

		printPrefix("Cost To EU:", 30);
		printData(this.cost_from_eu);
		System.out.println();

		printPrefix("Total Cost:", 30);
		printData(this.cost_total);
		System.out.println();
		
		// production
		printPrefix("Energy Sell To Prosumers:", 30);
		printData(this.energy_sell_to_prosumers);
		System.out.println();

		printPrefix("Energy Sell To EU:", 30);
		printData(this.energy_sell_to_eu);
		System.out.println();
		
		printPrefix("Profit From Prosumers:", 30);
		printData(this.profit_from_prosumers);
		System.out.println();

		printPrefix("Profit From EU:", 30);
		printData(this.profit_from_eu);
		System.out.println();

		printPrefix("Total Profit:", 30);
		printData(this.profit_total);
		System.out.println();
		
	}

	/** Analysis the data */
	private void analysisData() {

		double total_energy_buy = 0;
		double total_energy_buy_from_eu = 0;
		
		for(int i = 0; i < size; i++) {
			total_energy_buy += energy_buy_from_prosumers[i] + energy_buy_from_eu[i];
			total_energy_buy_from_eu += energy_buy_from_eu[i];
		}
		
		
		System.out.println();
		System.out.println("Total Energy Transmit: " + round(total_energy_transmit, 2) + " kWh");
		System.out.println("Total Energy Loss: " + round(total_energy_loss, 2) + " kWh");
		System.out.println("Percentage Of Loss: " + round(total_energy_loss / total_energy_transmit * 100, 2) + " %");

		System.out.println();
		System.out.println("Total Energy Buy: " + round(total_energy_buy, 2) + " kWh");
		System.out.println("Total Energy Buy From EU: " + round(total_energy_buy_from_eu, 2) + " kWh");
		System.out.println("Percentage Of Energy Buy From EU: " + round(total_energy_buy_from_eu / total_energy_buy * 100, 2) + " %");
	}
	
	/** Print and anaylsis the data */
	public void analysis() {
		printData();
		analysisData();
	}

}
