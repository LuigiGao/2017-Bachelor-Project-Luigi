package Plan;

import java.util.ArrayList;

/**
 * Flows is many paths that transfer energy from one prosumer to another
 * prosumer
 * 
 * @author Luigi
 *
 */

public class Flows {

	/** The node that transfer energy */
	private int source;

	/** The node that receive energy */
	private int dest;

	/** A array of flows that have same source and destination */
	private ArrayList<Flow> flows;

	/** Maximum energy that can transfer through this flows */
	private double energy_transmit;

	/** Remaining energy that source have and cannot transfer through this flows */
	private double rest;

	/**
	 * Build flows
	 */
	public Flows() {
		this.flows = new ArrayList<Flow>();
		this.energy_transmit = 0;
	}

	/**
	 * Build flows
	 * 
	 * @param source
	 * @param dest
	 * @param energy_total
	 *            The total energy that source contains
	 */
	public Flows(int source, int dest, double energy_total) {
		this();
		this.source = source;
		this.dest = dest;
		this.rest = energy_total;
	}

	/**
	 * Add a flow to flows
	 * 
	 * @param flow
	 */
	public void addFlow(Flow flow) {
		this.flows.add(flow);
		this.energy_transmit = this.energy_transmit + flow.getEnergy();
		this.rest = this.rest - flow.getEnergy();
	}

	/**
	 * 
	 * @return A array of flows
	 */
	public ArrayList<Flow> getFlows() {
		return this.flows;
	}

	/**
	 * Calculate the sum of energy loss of all flows
	 * 
	 * @param voltage
	 * @param time_interval
	 * @return A double that represent energy loss
	 */
	private double getEnergyLoss(double voltage, double time_interval) {

		if (flows.size() <= 0) {
			System.out.println("Error: no path from source to dest");
			return -1;
		}

		double energy_loss = 0;

		for (Flow flow : flows) {
			energy_loss = energy_loss + flow.getEnergyLoss(voltage, time_interval);
		}

		return energy_loss;
	}

	/**
	 * Calculate the estimate percentage of energy loss
	 * 
	 * @param voltage
	 * @param time_interval
	 * @return A double in [0,1] that represent percentage of energy loss
	 */
	public double getEnergyLossPercentage(double voltage, double time_interval) {
		return getEnergyLoss(voltage, time_interval) / this.energy_transmit; // in 100 percent
	}

	/**
	 * 
	 * @return Whether there is at least one flow
	 */
	public boolean exist() {
		return this.flows.size() > 0;
	}

	/**
	 * 
	 * @return Next flow in array of flows
	 */
	public Flow nextFlow() {
		Flow flow = this.flows.get(0);
		flows.remove(0);
		return flow;
	}

	/**
	 * 
	 * @return Whether there is at least one flow
	 */
	public boolean hasNext() {
		return exist();
	}

	/**
	 * 
	 * @return A integer that represents the beginning node
	 */
	public int getSource() {
		return this.source;
	}

	/**
	 * 
	 * @return A integer that represents the end node
	 */
	public int getDest() {
		return this.dest;
	}

}
