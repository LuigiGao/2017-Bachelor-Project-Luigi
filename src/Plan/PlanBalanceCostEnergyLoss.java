package Plan;

/**
 * A plan combining both energy loss and cost together to manage energy flows
 * The energy and load first copy from Network, then be changed when the plan
 * starts to find paths
 * The energy and load in Network should be changed after all paths are found,
 * sorted, and taken
 */

import java.util.ArrayList;

import Algorithm.*;
import Network.Network;
import javafx.util.Pair;

public class PlanBalanceCostEnergyLoss extends Plan {

	/** A array of percentage of energy loss for each provider */
	private double[] loss_percent;

	/**
	 * A array of estimate cost for buying energy(energy_require) from each provider
	 */
	private double[] estimate;

	/**
	 * A array of flows which from different sources(providers) to same
	 * destination(consumer)
	 */
	private Flows[] flows;

	/** Remaining energy for each provider when simulate in this plan */
	private double[] energy_simulation;

	/** Loads for each provider when simulate in this plan */
	private double[][] loads_simulation;

	/**
	 * Build the plan
	 * @param network
	 */
	public PlanBalanceCostEnergyLoss(Network network) {

		super(network);
		this.algorithm = new DijkstraAlgorithm(this.network);
		this.loss_percent = new double[this.nProsumer];
		this.estimate = new double[this.nProsumer];
		this.flows = new Flows[this.nProsumer];
		this.energy_simulation = new double[this.nProsumer];
		this.loads_simulation = new double[this.nProsumer][this.nProsumer];

	}

	/**
	 * @param prosumerA
	 * @param prosumerB
	 * @return The load between two prosumers in plan's simulation in 1 kWh
	 */
	public double getLoad(int prosumerA, int prosumerB) {
		return this.loads_simulation[prosumerA][prosumerB];
	}

	/**
	 * Add load energy between prosumerA and prosumerB to the plan's simulation
	 * @param prosumerA
	 * @param prosumerB
	 * @param energy Energy in 1 kWh
	 */
	public void addLoad(int prosumerA, int prosumerB, double energy) {
		this.loads_simulation[prosumerA][prosumerB] = this.loads_simulation[prosumerA][prosumerB] + energy;
	}

	/**
	 * @param consumer Id of prosumer
	 * @return A array of possible provider to the consumer
	 */
	public ArrayList<Integer> getNeighbours(int consumer) {
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		for (int i = 0; i < this.nProsumer; i++) {
			if (this.network.getResistance(consumer, i) >= 0 && this.loads_simulation[i][consumer] == 0
					&& this.loads_simulation[consumer][i] < this.network.getMaxLoad(consumer, i)) {
				neighbours.add(i);
			}
		}
		return neighbours;
	}

	/**
	 * @param path
	 * @return The maximum capacity of the path in kWh for 1 hour
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

			if (this.loads_simulation[prosumerB][prosumerA] == 0) {
				double capacityAB = this.network.getMaxLoad(prosumerA, prosumerB)
						- this.loads_simulation[prosumerA][prosumerB];
				if (capacityAB < min_capacity) {
					min_capacity = capacityAB;
				}
			} else {
				return 0;
			}

			prosumerA = prosumerB;
			index++;
		}
		return Math.min(min_capacity, this.energy_simulation[source]) * 1;
	}

	/**
	 * Simulate the path with energy transmit in the plan
	 * 
	 * @param path A array of prosumers that energy pass in order
	 * @param energy Energy transmit in 1 kWh
	 */
	// simulation in plan, not real energy transfer
	private void simulatePath(ArrayList<Integer> path, double energy) {
		if (path.size() <= 1) {
			return;
		}

		int prosumerA = path.get(0);
		this.energy_simulation[prosumerA] -= energy;
		int prosumerB;

		for (int i = 1; i < path.size(); i++) {
			prosumerB = path.get(i);
			this.loads_simulation[prosumerA][prosumerB] += energy;
			prosumerA = prosumerB;
		}
	}

	/**
	 * Compute a combination of path for transferring energy_sell from source to
	 * dest
	 * 
	 * @param source
	 * @param dest
	 * @param energy_sell Energy that source has in 1 kWh
	 * @return Flows that transfer energy_sell or maximum energy can be transfered
	 */
	private Flows planEnergyFlow(int source, int dest, double energy_sell) {
		// this rest is remaining energy that source have
		double rest = energy_sell;
		Flows flows = new Flows(source, dest, energy_sell);

		ArrayList<Integer> path = algorithm.findPath(source, dest, this.loads_simulation);
		// path.size() > 1 mean there exists a path between source and dest
		while (path.size() > 1 && this.energy_simulation[source] > 0) {

			double capacity = getCapacity(path);
			double resistance = this.network.getResistance(path);

			if (capacity >= rest) {
				flows.addFlow(new Flow(path, rest, resistance));
				simulatePath(path, rest);
				rest = 0;
				break;
			} else {
				flows.addFlow(new Flow(path, capacity, resistance));
				simulatePath(path, capacity);
				rest = rest - capacity;
			}

			path = algorithm.findPath(source, dest, this.loads_simulation);
		}

		return flows;
	}

	/**
	 * Choose providers that support energy to consumer and simulate the energy
	 * flowss
	 * 
	 * @param consumer Prosumer id
	 * @param energy_require Energy in 1 kWh
	 */
	private void manageProviders(int consumer, double energy_require) {

		// estimate cost for buying energy from providers one by one
		for (int provider = 0; provider < this.network.getNumberOfProsumers(); provider++) {

			double energy_sell = this.network.getProsumerEnergy(provider);

			// check whether provider has energy to sell
			if (energy_sell > 0) {

				Flows flows_provider = planEnergyFlow(provider, consumer, energy_sell);
				// whether there is a flow in flows
				if (flows_provider.exist()) {
					this.flows[provider] = flows_provider;
					this.loss_percent[provider] = flows_provider.getEnergyLossPercentage(this.network.getVoltage(), 1);
					// price of buying energy from all provider is estimated now
					this.estimate[provider] = energy_require
							* (1 + this.loss_percent[provider] * this.network.getProsumerPrice(provider));
				}
			}
		}

		this.rest = energy_require;

		// find provider with minimum cost
		int provider = findPosMin(this.estimate);

		// provider == -1 mean there is no more providers
		while (provider != -1) {
			// consume the provider
			this.estimate[provider] = -1;
			// real transfer energy in network, the rest energy that cannot be transfered
			// are calculated
			this.rest = this.network.simulateEnergyFlows(this.flows[provider], this.rest);

			if (this.rest == 0)
				break;

			provider = findPosMin(estimate);
		}

	}

	@Override
	public void plan(int consumer, double energy_require) {

		initial();

		// manage energy from each provider and electric utility
		manageProviders(consumer, energy_require);

		// buy rest energy from electric utility
		this.network.buyEnergyEU(consumer, this.rest);

	}

	@Override
	protected void initial() {
		for (int i = 0; i < this.nProsumer; i++) {
			this.loss_percent[i] = -1;
			this.estimate[i] = -1;
			this.flows[i] = null;
		}
		this.rest = 0;
		this.energy_simulation = this.network.copyCurrentEnergy();
		this.loads_simulation = this.network.copyLoadsStatus();
	}
}
