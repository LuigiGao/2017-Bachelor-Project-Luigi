package Plan;

import java.util.ArrayList;

/**
 * A flow is a path that can transfer energy from one prosumer to another prosumer
 * 
 * @author Luigi
 *
 */

public class Flow {

	/** The node that transfer energy */
	private int source;
	
	/** The node that receive energy */
	private int dest;

	/** All nodes that energy pass in order */
	private ArrayList<Integer> path;

	/** Energy that transmit through this path */
	// min of path capacity and source's energy
	private double sub_energy;

	/** Resistance of the path */
	private double resistance_total;

	/**
	 * Build the flow
	 * @param path
	 * @param energy
	 * @param resistance
	 */
	public Flow(ArrayList<Integer> path, double energy, double resistance) {
		this.path = path;
		this.sub_energy = energy;
		this.resistance_total = resistance;
		this.source = path.get(0);
		this.dest = path.get(path.size() - 1);
	}

	/**
	 * 
	 * @return The path that transfer energy
	 */
	public ArrayList<Integer> getPath() {
		return this.path;
	}

	/**
	 * 
	 * @return Total energy that can transfer through this path
	 */
	public double getEnergy() {
		return this.sub_energy;
	}

	/**
	 * 
	 * @return Resistance of the path
	 */
	public double getResistance() {
		return this.resistance_total;
	}

	/**
	 * 
	 * @param voltage
	 * @param time_interval
	 * @return Energy loss with the maximum energy can transfer with given voltage and time
	 */
	public double getEnergyLoss(double voltage, double time_interval) {
		return (this.sub_energy * this.sub_energy * this.resistance_total) / (time_interval * voltage * voltage);
	}

	/**
	 * 
	 * @param voltage
	 * @param time_interval
	 * @param energy
	 * @return Energy loss with given energy, voltage and time through this path
	 */
	public double getEnergyLoss(double voltage, double time_interval, double energy) {
		return (energy * energy * this.resistance_total) / (time_interval * voltage * voltage);
	}

	/**
	 * 
	 * @return An integer that represent source node
	 */
	public int getSource() {
		return this.source;
	}

	/**
	 * 
	 * @return An integer that represent destination node
	 */
	public int getDest() {
		return this.dest;
	}

	/**
	 * Calculate the actual received energy by destination when transfer energy through this path
	 * @param energy
	 * @param voltage
	 * @param time_interval
	 * @return A double that represent amount of energy in kWh
	 */
	public double receivedEnergy(double energy, double voltage, double time_interval) {
		double a = this.resistance_total;
		double b = voltage * voltage * time_interval;
		double c = -voltage * voltage * time_interval * energy;

		// result1 should be always bigger than 0
		double result1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		// result2 is always smaller than 0
		// double result2 = (- b - Math.sqrt( b*b-4*a*c)) / (2*a);

		if (result1 < 0) {
			System.out.println("Error: receive negative energy");
			return 0;
		}

		return result1;
	}
}
