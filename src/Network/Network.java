package Network;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Algorithm.*;
import Parser.TopologyJsonParser;
import Plan.*;
import Weather.*;

/**
 * A class that simulates a smart grid system.
 * In this topology which prosumers are nodes, and connections between two prosumers are links
 * The simulation period is defined to 1 hour.
 * 
 * Resource: 
 * 2017 electricity price (per kWh) in Netherlands
 * http://ec.europa.eu/eurostat/statistics-explained/index.php/Electricity_price_statistics
 * https://www.worldstandards.eu/electricity/plug-voltage-by-country/
 * https://en.wikipedia.org/wiki/Electric_power_transmission
 * https://www.researchgate.net/post/Why_is_resistance_of_a_transmission_line_negligible_and_the_resistance_of_a_distribution_system_is_considered_to_be_predominant
 * 
 * @author Luigi
 *
 */
public abstract class Network implements Runnable {

	/** A array of prosumers */
	// index of prosumer in array is also the index of row and column in matrix
	// which is stored as id in prosumer
	protected ArrayList<Prosumer> prosumers;

	/** Energy to sell/buy for all prosumers at a particular hour */
	// +/- number means sell/buy
	protected double[] prosumers_energy;

	/** Prices of each prosumer at a particular hour */
	protected double[] prosumers_price;

	/** Resistances from each prosumer to electric utility */
	protected double[] resistance_form_EU;

	/** A map between prosumer id and householder */
	protected HashMap<Integer, String> householder_id;

	/**
	 * Electric_Utility is represented by id of one prosumer, which sell/buy energy
	 * to/from prosumer
	 */
	protected int electric_Utility;

	/** Price of buying electricity from electric utility */
	protected double priceEU_sell;

	/** Price of selling electricity to electric utility */
	protected double priceEU_buy;

	/** Weather Database */
	protected WeatherDatabase weather_database;

	/** Voltage of the network (in 1 V) */
	protected double voltage;

	/** Number of remaining simulation days */
	protected int remaining_day;

	/** Plan that manage the energy flow between prosumers */
	protected Plan plan;

	// matrix representation for a matrix M for two prosumers 0 and 1:
	// prosumer\prosumer 0 1
	//                0 [ x x ]
	//                1 [ x x ]

	// M[x][y] = 0 means there is no electric energy from x to y
	// M[x][y] > 0 means there is electric energy from x to y
	/** A map of energy flow among all prosumers */
	protected double[][] loads_status;

	// if M[x][y] >= 0, M[x][y] is the max load between prosumer x and prosumer y
	/** A map of max load among all prosumers */
	protected double[][] max_loads;

	// M[x][y] = -1 means no connection between prosumer x and prosumer y
	// M[x][y] >= 0 means there is connection between prosumer x and prosumer y
	// which Resistance is value stored in M[x][y]
	/** A map of connection among all prosumers */
	protected double[][] connections;

	/**
	 * Build a topology which prosumers are nodes, and connections between two
	 * prosumers are links
	 * 
	 * @param prosumer_info Name of householders
	 * @param relation_info Relation of prosumers
	 */
	public Network(String prosumer_info, String relation_info) {

		initialBefore(prosumer_info, relation_info);
		
		TopologyJsonParser parser = new TopologyJsonParser(this, prosumer_info, relation_info);
		parser.parseProsumers();
		
		initialAfter();
		
		parser.parseRelations();
		
		buildResistanceEU();

	}

	/**
	 * 
	 * @param prosumer_info
	 * @param relation_info
	 * @param weather_info Weather data
	 */
	public Network(String prosumer_info, String relation_info, String weather_info) {

		this(prosumer_info, relation_info);

		weather_database = new WeatherDatabase(weather_info);

	}

	/**
	 * 
	 * @param prosumer_info
	 * @param relation_info
	 * @param weather_info
	 * @param remaining_day Days need to simulate
	 */
	public Network(String prosumer_info, String relation_info, String weather_info, int remaining_day) {

		this(prosumer_info, relation_info, weather_info);

		this.remaining_day = remaining_day;

	}

	/**
	 * Initial variables
	 * 
	 * @param prosumer_info
	 * @param relation_info
	 */
	private void initialBefore(String prosumer_info, String relation_info) {

		this.prosumers = new ArrayList<Prosumer>();
		this.householder_id = new HashMap<Integer, String>();
		
		// In real world, the voltage used for transmission is very high which around ~100kV with power ~1MVA(~1000kW)
		// But in this program, the power is only about ~1kW(~1kVA), which reduce the voltage to ~100V for a reasonable output
		this.voltage = 230;
		
		this.electric_Utility = 0;
		this.priceEU_sell = 0.16;
		this.priceEU_buy = 0.06;
	}

	/**
	 * Initial the matrix that use for store information of connection, max load,
	 * energy flow, etc...
	 */
	private void initialAfter() {

		this.plan = new PlanBalanceCostEnergyLoss(this);
		//this.plan = new PlanMinEnergyLoss(this);

		this.loads_status = new double[prosumers.size()][prosumers.size()];
		this.max_loads = new double[prosumers.size()][prosumers.size()];
		this.connections = new double[prosumers.size()][prosumers.size()];
		this.prosumers_energy = new double[prosumers.size()];
		this.prosumers_price = new double[prosumers.size()];
		this.resistance_form_EU = new double[prosumers.size()];

		int nProsumer = this.prosumers.size();

		for (int i = 0; i < nProsumer; i++) {
			for (int j = 0; j < nProsumer; j++) {
				this.loads_status[i][j] = 0;
				this.max_loads[i][j] = -1;
				this.connections[i][j] = -1;
			}
			this.prosumers_energy[i] = 0;
			this.prosumers_price[i] = 0;
			this.resistance_form_EU[i] = 0;
		}

	}

	/**
	 * For all prosumers, find the shortest way with resistance to electric utility
	 */
	private void buildResistanceEU() {
		Algorithm algorithm = new DijkstraAlgorithm(this);
		for (int i = 0; i < this.prosumers.size(); i++) {
			ArrayList<Integer> path_to_EU = algorithm.findPath(i, this.electric_Utility);
			if (path_to_EU.size() < 2) {
				// The prosumer i is located at electric utility
				this.resistance_form_EU[i] = 0;
			} else {
				double resistance = this.getResistance(path_to_EU);
				this.resistance_form_EU[i] = resistance;
			}
		}
	}

	/**
	 * Add one prosumer to network
	 * @param prosumer Prosumer
	 */
	public void addProsumer(Prosumer prosumer) {
		this.prosumers.add(prosumer);
	}
	
	/**
	 * Set electric utility to house_id
	 * @param house_id
	 */
	public void setElectricUtility(int house_id) {
		this.electric_Utility = house_id;
	}

	/**
	 * Add max load and resistance between two prosumers
	 * 
	 * @param houseA
	 * @param houseB
	 * @param maxLoad Maximum load in 1 kW
	 * @param resistance Resistance in 1 ohm
	 */
	public void addConnection(int houseA, int houseB, double maxLoad, double resistance) {

		this.max_loads[houseA][houseB] = maxLoad;
		this.max_loads[houseB][houseA] = maxLoad;
		this.connections[houseA][houseB] = resistance;
		this.connections[houseB][houseA] = resistance;

	}

	/**
	 * Map the householder to its id
	 * 
	 * @param householder Name of householder
	 * @param prosumer_ID
	 */
	public void hashHouseId(String householder, int prosumer_ID) {
		this.householder_id.put(prosumer_ID, householder);
	}

	/**
	 * Get name of householder of the id
	 * @param id
	 * @return Name of householder
	 */
	public String getHolder(int id) {
		return this.householder_id.get(id);
	}
	
	/** @return Number of prosuemrs in this network */
	public int getNumberOfProsumers() {
		return this.prosumers.size();
	}

	/** @return Voltage of the network in 1 V */
	public double getVoltage() {
		return this.voltage;
	}
	
	/**
	 * Get energy of the prosumer in current time interval
	 * @param prosumer
	 * @return Energy in 1 kWh
	 */
	public double getProsumerEnergy(int prosumer) {
		return this.prosumers_energy[prosumer];
	}

	/**
	 * Get selling price of the prosumer in current time interval
	 * @param prosumer
	 * @return Selling price in 1 euro/kWh
	 */
	public double getProsumerPrice(int prosumer) {
		return this.prosumers_price[prosumer];
	}

	/** @return Price in 1 euro/kWh when buying energy from electric utility */
	public double getSellPriceEU() {
		return this.priceEU_sell;
	}

	/** @return Price in 1 euro/kWh when selling energy to electric utility */
	public double getBuyPriceEU() {
		return this.priceEU_buy;
	}

	/** Get Prosumer with id */
	public Prosumer getProsumer(int id) {
		return prosumers.get(id);
	}
	
	/** @return Id of electric utility */
	public int getElecticUtility() {
		return this.electric_Utility;
	}

	/**
	 * Get current load between two prosumers
	 * @param prosumerA
	 * @param prosumerB
	 * @return Energy in 1 kWh
	 */
	public double getCurrentLoad(int prosumerA, int prosumerB) {
		return this.loads_status[prosumerA][prosumerB];
	}

	/**
	 * Get maximum load between two prosuemrs
	 * @param prosumerA
	 * @param prosumerB
	 * @return Energy in 1 kWh
	 */
	public double getMaxLoad(int prosumerA, int prosumerB) {
		return this.max_loads[prosumerA][prosumerB];
	}

	/** @return A array of prosumers that need to buy energy */
	public ArrayList<Integer> getConsumers() {
		ArrayList<Integer> consumers = new ArrayList<Integer>();
		for (int i = 0; i < this.prosumers_energy.length; i++) {
			if (this.prosumers_energy[i] < 0) {
				consumers.add(i);
			}
		}
		return consumers;
	}

	/** @return A array of prosumers that need to sell energy */
	public ArrayList<Integer> getProviders() {
		ArrayList<Integer> sellers = new ArrayList<Integer>();
		for (int i = 0; i < this.prosumers_energy.length; i++) {
			if (this.prosumers_energy[i] > 0) {
				sellers.add(i);
			}
		}
		return sellers;
	}

	/**
	 * Get a array of resistances from one prosumer to all other prosumers
	 * @param source
	 * @return A array of resistances
	 */
	public double[] getResistances(int source) {
		int n = this.prosumers.size();
		double[] connection = new double[n];
		for (int i = 0; i < n; i++) {
			connection[i] = this.connections[source][i];
		}
		return connection;
	}

	/**
	 * Get resistance between two prosumers
	 * @param prosumerA
	 * @param prosumerB
	 * @return Resistance in 1 ohm
	 */
	public double getResistance(int prosumerA, int prosumerB) {
		if (prosumerA < 0 || prosumerA >= this.prosumers.size() || prosumerB < 0
				|| prosumerB >= this.prosumers.size()) {
			System.out.println("Error: Invalid house id");
			return -1;
		}
		return this.connections[prosumerA][prosumerB];
	}

	/**
	 * Get resistance of a path, which is sum of all resistance between two prosumers in the path
	 * @param path A array of prosumers that energy pass
	 * @return Resistance in 1 ohm
	 */
	public double getResistance(ArrayList<Integer> path) {

		int index = 0;
		double resistance = 0;

		int prosumerA = path.get(index);
		index++;
		int prosumerB;

		while (index < path.size()) {
			prosumerB = path.get(index);
			resistance = resistance + getResistance(prosumerA, prosumerB);
			prosumerA = prosumerB;
			index++;
		}
		return resistance;

	}

	/**
	 * Get A array of providers that can sell energy to the given consumer.
	 * Check the connection, energy flow and max load between the given consumer
	 * with all prosumers
	 * 
	 * @param prosumer Prosumer id
	 * @return A array of prosumers
	 */
	public ArrayList<Integer> getNeighbours(int prosumer) {
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		int n = this.prosumers.size();
		for (int i = 0; i < n; i++) {
			// three assumption to check the neighbours
			// 1. there is connection between prosumer and i
			// 2. there is no energy flow form i to prosumer
			// 3. the energy flow from prosumer to i is smaller than the max load between them
			if (this.connections[prosumer][i] >= 0 && this.loads_status[i][prosumer] == 0
					&& this.loads_status[prosumer][i] < this.max_loads[prosumer][i]) {
				neighbours.add(i);
			}
		}
		return neighbours;
	}

	/**
	 * Get A array of providers that can sell energy to the given consumer for the given current loads
	 * @param prosumer Prosumer id
	 * @param temp_loads Loads between prosumers
	 * @return A array of prosumers
	 */
	public ArrayList<Integer> getNeighbours(int prosumer, double[][] temp_loads) {
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		int n = this.prosumers.size();
		for (int i = 0; i < n; i++) {
			if (this.connections[prosumer][i] >= 0 && temp_loads[i][prosumer] == 0
					&& temp_loads[prosumer][i] < this.max_loads[prosumer][i]) {
				neighbours.add(i);
			}
		}
		return neighbours;
	}

	/**
	 * Get capacity of the path, which is the minimum capacities of all capacities between 
	 * pairs of two prosumers in the path and the energy of the source.
	 * 
	 * @param path A array of prosumers that energy pass
	 * @return Energy in 1 kWh
	 */
	public double getCapacity(ArrayList<Integer> path) {

		double min_capacity = Double.MAX_VALUE;
		int source = path.get(0);

		int index = 0;
		int prosumerA = path.get(index);
		index++;
		int prosumerB;

		while (index < path.size()) {
			prosumerB = path.get(index);

			if (this.loads_status[prosumerB][prosumerA] == 0) {
				double capacityAB = this.max_loads[prosumerA][prosumerB] - this.loads_status[prosumerA][prosumerB];
				if (capacityAB < min_capacity) {
					min_capacity = capacityAB;
				}
			} else {
				return 0;
			}

			prosumerA = prosumerB;
			index++;
		}
		return Math.min(min_capacity * 1, this.prosumers_energy[source]);

	}

	/** @return A copy matrix of load status between all prosumers */
	public double[][] copyLoadsStatus() {
		double[][] copy = new double[this.prosumers.size()][this.prosumers.size()];
		for (int i = 0; i < this.prosumers.size(); i++) {
			for (int j = 0; j < this.prosumers.size(); j++) {
				copy[i][j] = this.loads_status[i][j];
			}
		}
		return copy;
	}

	/**@return A copy array of prosumers' current energy */
	public double[] copyCurrentEnergy() {
		double[] copy = new double[this.prosumers.size()];
		for (int i = 0; i < this.prosumers.size(); i++) {
			copy[i] = this.prosumers_energy[i];
		}
		return copy;
	}

	/**
	 * Print information(house number, wind turbines, photovoltaic panels) of
	 * prosumers, and their relationship
	 */
	public void printNodeInfo() {
		for (Prosumer prosumer : this.prosumers) {
			System.out.println(prosumer.getInfo());
		}
		int n = prosumers.size();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				if (this.connections[i][j] >= 0) {
					System.out.println("Connection between " + prosumers.get(i).getHousehold() + " and "
							+ prosumers.get(j).getHousehold() + ": " + this.connections[i][j] + "R, "
							+ this.max_loads[i][j] + "J");
				}
			}
		}
	}

	/**
	 * Print the matrix
	 * @param matrix
	 */
	private void printMatrix(double[][] matrix) {

		int n = this.prosumers.size();
		String line;
		String number = "";

		int digits = 0;
		while (n != 0) {
			digits++;
			n = n / 10;
		}

		if (digits == 0) {
			System.out.println("Error: no valid prosumer");
		} else if (digits > 3) {
			System.out.println("Information: too much prosumers");
		}

		// first index row
		line = "   ";
		for (int i = 0; i < this.prosumers.size(); i++) {
			if (i < 10) {
				line = line + "    " + i;
			} else {
				line = line + "   " + i;
			}
		}
		System.out.println(line);

		// print the matrix
		for (int i = 0; i < this.prosumers.size(); i++) {
			// add index column
			if (i < 10) {
				line = " " + i + " ";
			} else {
				line = "" + i + " ";
			}
			// add matrix value with fix size (max 4 digit)
			for (int j = 0; j < this.prosumers.size(); j++) {

				number = "" + matrix[i][j];
				if (number.length() > 5) {
					number = number.substring(0, 4);
				}
				while (number.length() < 5) {
					number = " " + number;
				}

				// add matrix value
				line = line + number;
			}
			System.out.println(line);
		}
	}

	/** Print matrix of maximum load between all prosumers */
	public void printMaxLoad() {
		System.out.println("Max Load");
		printMatrix(this.max_loads);
	}

	/** Print matrix of resistance between all prosumers */
	public void printConnection() {
		System.out.println("Connection");
		printMatrix(this.connections);
	}

	/** Print matrix of load(energy) status between all prosumers */
	public void printLoadsStatus() {
		System.out.println("Energy flows");
		printMatrix(this.loads_status);
	}

	/**
	 * Calculate the energy loss when transmit energy form electric utility to
	 * consumer
	 * 
	 * @param consumer Prosumer id
	 * @param energy Energy in 1 kWh
	 * @return Energy loss in kWh
	 */
	public double energyLossEU(int consumer, double energy) {
		return (energy * energy * this.resistance_form_EU[consumer])
				/ (1 * this.voltage * this.voltage) * 1000;
	}

	/**
	 * Simulate the energy transmit in flows
	 * 
	 * @param flows
	 *            Many flows that have same source and same destination
	 * @param energy
	 *            Energy needed to transmit in 1 kWh
	 * @return The rest energy cannot transmit through the flow
	 */
	public Double simulateEnergyFlows(Flows flows, double energy) {
		double rest = energy;
		while (rest > 0 && flows.hasNext()) {
			Flow flow = flows.nextFlow();
			rest = simulateEnergyFlow(flow, rest);
		}
		return rest;
	}

	/** Clean matrix for next simulation */
	protected void resetSimulation() {
		for (int i = 0; i < this.prosumers.size(); i++)
			for (int j = 0; j < this.prosumers.size(); j++)
				this.loads_status[i][j] = 0;
	}

	/**
	 * Update network with a path and energy. For each pairs of prosumers, add energy to load
	 * status. For the seller, subtract the energy to current energy storage.
	 * 
	 * @param path A array of prosumers that energy pass
	 * @param energy Energy transmit in 1 kWh
	 * @param resistance Resistance of the path in 1 ohm
	 */
	public abstract void updateEnergyFlow(ArrayList<Integer> path, double energy, double resistance);
	
	/**
	 * Simulate energy transmit in a flow
	 * 
	 * @param flow A path which have one source and one destination
	 * @param energy Energy needed by the destination in 1 kWh
	 * @return The rest energy cannot transmit through the flow in 1 kWh
	 */
	public abstract Double simulateEnergyFlow(Flow flow, double energy);

	/**
	 * Simulate prosumer buy energy from electric utility
	 * 
	 * @param prosumer Prosumer id
	 * @param energy Energy in 1 kWh
	 */
	public abstract void buyEnergyEU(int prosumer, double energy);

	/**
	 * Simulation of prosumers' output in given time slot
	 * @param min_wind_speed Minimum wind speed in 1 m/s
	 * @param max_wind_speed Maximum wind speed in 1 m/s
	 * @param solar_radiation Solar radiation in 1 kW/m^2
	 * @param temperature Temperature in 1 degree Celsius
	 * @param time_in_day Time in day between 0 and 23
	 */
	protected abstract void simulation_prosumers(double min_wind_speed, double max_wind_speed, double solar_radiation,
			double temperature, int time_in_day);
	
	/** Simulation of energy flows for current status */
	protected abstract void simulation_topology();

	/**
	 * Simulation for one hour. Calculate net energy for each prosumer and manage
	 * the energy flow between them.
	 * 
	 * @param weather Weather
	 * @param time_in_day Time in day between 0 and 23
	 */
	protected abstract void simulation_hour(Weather weather, int time_in_day);

	/** 
	 * Simulation for one day. 
	 * Extract weather and simulate the smart grid hourly.
	 *  */
	protected abstract void simulation_day();

	/** Simulate the smart grid s*/
	@Override
	public void run() {

	}

}
