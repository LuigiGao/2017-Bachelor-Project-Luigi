package Plan;

import java.util.ArrayList;

/**
 * A flow is a path that transfer energy from one prosumer to another prosumer
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

	/** Maximum energy that can transmit through this path in 1 kWh */
	private double sub_energy;

	/** Resistance of the path in 1 ohm */
	private double resistance_total;

	/**
	 * Build the flow
	 * @param path A array of prosumer id that energy pass
	 * @param energy Energy can transmit in 1 kWh
	 * @param resistance Resistance of the path in 1 ohm
	 */
	public Flow(ArrayList<Integer> path, double energy, double resistance) {
		this.path = path;
		this.sub_energy = energy;
		this.resistance_total = resistance;
		this.source = path.get(0);
		this.dest = path.get(path.size() - 1);
	}

	/** @return The path that transfer energy */
	public ArrayList<Integer> getPath() {
		return this.path;
	}

	/** @return Total energy that can transfer through this path in 1 kWh */
	public double getEnergy() {
		return this.sub_energy;
	}

	/** @return Resistance of the path in 1 ohm */
	public double getResistance() {
		return this.resistance_total;
	}

	/**
	 * Calculate the energy loss of this flow with maximum energy can transmit
	 * @param voltage Voltage in 1 V
	 * @param time Time in 1 h
	 * @return Energy loss in 1 kWh 
	 */
	public double getEnergyLoss(double voltage, double time) {
		return (this.sub_energy * this.sub_energy * this.resistance_total) / (time * voltage * voltage);
	}

	/**
	 * Calculate the energy loss of this flow with given energy
	 * @param voltage Voltage in 1 V
	 * @param time Time in 1 h
	 * @param energy Energy transmit in 1 kWh
	 * @return Energy loss in 1 kWh
	 */
	public double getEnergyLoss(double voltage, double time, double energy) {
		return (energy * energy * this.resistance_total) / (time * voltage * voltage);
	}

	/** @return An integer that represent source node */
	public int getSource() {
		return this.source;
	}

	/** @return An integer that represent destination node */
	public int getDest() {
		return this.dest;
	}

	/**
	 * Calculate the actual received energy by destination when transfer energy through this path
	 * @param energy Energy transmit in 1 kWh
	 * @param voltage Voltage of network in 1 V
	 * @param time Time used to transmit energy in 1 h
	 * @return Energy received in kWh
	 */
	public double receivedEnergy(double energy, double voltage, double time) {
		double a = this.resistance_total;
		double b = voltage * voltage * time;
		double c = -voltage * voltage * time * energy;

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
