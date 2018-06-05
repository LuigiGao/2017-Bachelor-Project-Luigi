package Plan;

import java.util.ArrayList;

/**
 * Flows is many paths that transfer energy from same source 
 * to same destination
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

	/** Maximum energy that can transfer through this flows in 1 kWh */
	private double energy_transmit;

	/** Remaining energy that source have and cannot transfer through this flows in 1 kWh */
	private double rest;

	/** Build flows */
	public Flows() {
		this.flows = new ArrayList<Flow>();
		this.energy_transmit = 0;
	}

	/**
	 * @param source Energy provider
	 * @param dest Energy consumer
	 * @param energy_total The total energy that source contains in 1 kWh
	 */
	public Flows(int source, int dest, double energy_total) {
		this();
		this.source = source;
		this.dest = dest;
		this.rest = energy_total;
	}

	/**
	 * Add a flow to flows
	 * @param flow
	 */
	public void addFlow(Flow flow) {
		this.flows.add(flow);
		this.energy_transmit = this.energy_transmit + flow.getEnergy();
		this.rest = this.rest - flow.getEnergy();
	}

	/** @return A array of flows */
	public ArrayList<Flow> getFlows() {
		return this.flows;
	}

	/**
	 * Calculate the sum of energy loss of all flows
	 * @param voltage Voltage of the network in 1 V
	 * @param time Time used to transmit energy in 1 h
	 * @return Energy loss in 1 kWh
	 */
	private double getEnergyLoss(double voltage, double time) {

		if (flows.size() <= 0) {
			System.out.println("Error: no path from source to dest");
			return -1;
		}

		double energy_loss = 0;

		for (Flow flow : flows) {
			energy_loss = energy_loss + flow.getEnergyLoss(voltage, time);
		}

		return energy_loss;
	}

	/**
	 * Calculate the estimate percentage of energy loss
	 * @param voltage Voltage of the network in 1 V
	 * @param time Time used to transmit energy in 1 h
	 * @return Percentage of energy loss in [0,1]
	 */
	public double getEnergyLossPercentage(double voltage, double time_interval) {
		return getEnergyLoss(voltage, time_interval) / this.energy_transmit; // in 100 percent
	}

	/** @return Indicates whether there is a flow */
	public boolean exist() {
		return this.flows.size() > 0;
	}

	/** @return Next flow in array of flows */
	public Flow nextFlow() {
		Flow flow = this.flows.get(0);
		flows.remove(0);
		return flow;
	}

	/** @return Indicates whether there is a flow */
	public boolean hasNext() {
		return exist();
	}

	/** @return The beginning node */
	public int getSource() {
		return this.source;
	}

	/** @return The end node */
	public int getDest() {
		return this.dest;
	}

}
