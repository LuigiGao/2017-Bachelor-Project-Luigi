package Plan;

import java.util.ArrayList;

import Algorithm.*;
import Network.Network;
import javafx.util.Pair;

/**
 * A plan manages the energy flow for minimizing energy loss.
 * But this plan only manager the energy flow due to current status 
 * which means only consider the energy loss for one consumer not all consumers
 * 
 * @author Luigi
 *
 */

public class PlanMinEnergyLoss extends Plan {

	/** A array of prosumer id that energy pass in order */
	private ArrayList<Integer> path;
	
	/** Minimum resistance of paths in 1 ohm */
	private double minResis;

	/** Build the plan for minimizing energy loss */
	public PlanMinEnergyLoss(Network network) {
		super(network);
		this.algorithm = new DijkstraAlgorithm(network);
	}

	/** Find the provider which have the least energy loss */
	private void findMinLossProvider(int consumer) {

		ArrayList<Integer> providers = this.network.getProviders();
		// find provider with path have minimum resistance
		for (int p : providers) {
			ArrayList<Integer> newPath = this.algorithm.findPath(p, consumer);
			if (newPath.size() > 1) {
				double resistance = this.network.getResistance(newPath);
				if (resistance < this.minResis) {
					this.minResis = resistance;
					this.path = newPath;
				}
			}
		}
	}

	/** Plan energy required by the consumer from providers */
	private void findProviders(int consumer, double energy_require) {
		this.rest = energy_require;

		while (this.rest > 0) {

			initial();

			findMinLossProvider(consumer);
			// no path available
			if (this.path.size() < 2)
				break;
			Flow flow = new Flow(path, 0, this.minResis);
			this.rest = this.network.simulateEnergyFlow(flow, this.rest);

		}

	}

	@Override
	protected void initial() {
		this.path = new ArrayList<Integer>();
		this.minResis = Double.MAX_VALUE;
	}

	@Override
	public void plan(int consumer, double energy_require) {

		findProviders(consumer, energy_require);
		this.network.buyEnergyEU(consumer, this.rest);

	}

}
