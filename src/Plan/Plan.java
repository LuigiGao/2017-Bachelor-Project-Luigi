package Plan;

import Network.NodeNetwork;

public abstract class Plan {

	protected NodeNetwork network;
	protected int nProsumer;
	protected double voltage;
	protected double time_interval;

	// providers for a particular prosumer with amount of energy provided
	protected double[] energy_sell;

	// used to store rest energy need by prosumer
	protected double rest;

	public Plan(NodeNetwork network) {

		this.network = network;
		this.nProsumer = this.network.getNumberOfProsumers();
		this.voltage = this.network.getVoltage();
		this.time_interval = this.network.getTimeInterval();

	}

	protected abstract void initial();

	// find index of minimum positive value in the array
	protected int findMin(double[] array) {
		double min = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != -1 && min > array[i]) {
				min = array[i];
				index = i;
			}
		}
		return index;
	}

}
