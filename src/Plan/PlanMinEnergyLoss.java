package Plan;

import java.util.ArrayList;

import Algorithm.DijkstraAlgorithm;
import Network.NodeNetwork;
import javafx.util.Pair;

// this plan only find the min energy loss at current status which means only consider one consumer not all consumers

public class PlanMinEnergyLoss extends Plan {

	private DijkstraAlgorithm algorithm;
	private ArrayList<Integer> path;
	private double minResis;

	public PlanMinEnergyLoss(NodeNetwork network) {
		super(network);
		this.algorithm = new DijkstraAlgorithm(network);
	}

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
