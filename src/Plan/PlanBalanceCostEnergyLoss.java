package Plan;

// an implementation of algorithm discussed in paper
// on paper those algorithm regards consumer as source and provider as dest, which is weird
// but here the provider is source which transmit energy to consumer(dest)

import java.util.ArrayList;

import Algorithm.DijkstraAlgorithm;
import Network.NodeNetwork;
import javafx.util.Pair;

public class PlanBalanceCostEnergyLoss extends Plan {

	private DijkstraAlgorithm algorithm;
	
	//private double[] capacitys;

	private double[] loss_percent;

	// a array of estimate cost for buying energy(energy_require) from each provider
	private double[] estimate;

	private Flows[] flows;

	private double[][] loads_simulation;
	
	public PlanBalanceCostEnergyLoss(NodeNetwork network) {

		super(network);
		this.algorithm = new DijkstraAlgorithm(this.network);

	}

	// energy is amount of energy that dest has
	// give a combination of path for transferring energy_sell from source to dest
	private Flows planEnergyFlow(int source, int dest, double energy_sell) {

		double rest = energy_sell;
		Flows flows = new Flows( source, dest, energy_sell );
		
		ArrayList<Integer> path = algorithm.findShortestPath(source, dest);
		// path.size() > 0 mean there exists a path between source and dest
		while (path.size() > 0) {

			// max load or possible energy of dest
			double capacity = this.network.getCapacity(path);
			//this.capacitys[dest] = capacity;

			double resistance = this.network.getResistance(path);

			if (capacity > rest) {

				flows.addFlow(new Flow(path, rest, resistance));

				// ? Does it update in mamageProviders
				// here we find the possible energy flows
				// this.network.updateCapacity(path, rest);
				
				rest = 0;

				break;
			} else {

				flows.addFlow(new Flow(path, capacity, resistance));
				rest = rest - capacity;

				// ? Does it update in mamageProviders
				// here we find the possible energy flows
				// this.network.updateCapacity(path, capacity);
			}

			path = algorithm.findShortestPath(source, dest);
		}

		return flows;
	}

	private void manageProviders(int consumer, double energy_require) {

		// estimate cost for buying energy from providers one by one
		for (int provider = 0; provider < this.network.getNumberOfProsumers(); provider++) {

			double energy_sell = this.network.getCurrentEnergy(provider);

			// check whether house_i is a provider with extra energy to sell
			if (energy_sell > 0) {
				Flows flows = planEnergyFlow( provider, consumer, energy_sell);
				// whether there is at one flow in flows
				if( flows.exist() ) {
					this.flows[ provider ] = flows;
					this.loss_percent[provider] = flows.getEnergyLossPercentage(this.voltage, this.time_interval);
					this.estimate[provider] = energy_require
							* (1 + this.loss_percent[provider] * this.network.getCurrentPrice(provider));	
				}
			}
		}

		// used to store rest energy need by prosumer which need buy from electric utility
		this.rest = energy_require;
		int provider = findMin( this.estimate );
		// provider == -1 mean there is no more providers
		while (provider != -1) {
			// consume the provider
			this.estimate[provider] = -1;

			// i want to change capacitys dynamiclly !!!!!!!!!!!!!!
			// simulateEnergyFlows take transmit energy through paths and return the energy get by consumer
			Double net_energy_sell = this.network.simulateEnergyFlows( this.flows[provider], this.rest, this.loss_percent[provider] );
			//this.network.get / (1 + this.loss_percent[provider]
			/*
			if (this.rest < this.network.get / (1 + this.loss_percent[provider])) {
				this.energy_sell[provider] = this.rest * (1 + this.loss_percent[provider]);
				this.rest = 0;
				break;
			} else {
				this.energy_sell[provider] = this.capacitys[provider];
				this.rest = this.rest - this.capacitys[provider] / (1 + this.loss_percent[provider]);
			}
			*/
			
			if( net_energy_sell == null ) {
				this.rest = 0;
				break;
			}else {
				this.rest = this.rest - net_energy_sell;
			}

			provider = findMin(estimate);
		}

		// update energy sell for each providers to network
		// this.network.transferEnergy(this.energy_sell);
		// done in simulationEnergyFlow
	}

	public void plan(int consumer, double energy_require) {

		initial();

		// manage energy from each provider and electric utility
		manageProviders(consumer, energy_require);

		/*
		double cost_p = 0.0;
		for (int provider = 0; provider < this.nProsumer; provider++) {
			if (this.energy_sell[provider] > 0)
				// sum of energy sell for each provider time their own price is the cost
				// consumer
				cost_p = cost_p + this.energy_sell[provider] * this.network.getCurrentPrice(provider);
		}
		*/

		double cost_u = 0.0;
		if (this.rest > 0) {
			double loss = this.network.calculateEnergyLoss(this.network.getElecticUtility(), consumer, this.rest);
			cost_u = this.network.getPriceEC() * (this.rest + loss);
		}
		this.network.costEC( consumer, cost_u );
		
	}

	@Override
	protected void initial() {
		this.energy_sell = new double[this.nProsumer];
		this.capacitys = new double[this.nProsumer];
		this.loss_percent = new double[this.nProsumer];
		this.estimate = new double[this.nProsumer];
		this.flows = new Flows[this.nProsumer];
		for (int i = 0; i < this.nProsumer; i++) {
			this.energy_sell[i] = 0;
			this.capacitys[i] = 0;
			this.loss_percent[i] = -1;
			this.estimate[i] = -1;
			this.flows[i] = null;
		}
		this.rest = 0;
	}
}
