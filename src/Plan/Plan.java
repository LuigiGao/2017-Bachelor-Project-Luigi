package Plan;

import java.util.ArrayList;

import Algorithm.Algorithm;
import Network.NodeNetwork;

/**
 * A template of plan to manage the flow of energy between prosumers
 * 
 * @author Luigi
 *
 */

public abstract class Plan {

	/** Graph with nodes and relation information */
	protected NodeNetwork network;

	/** Number of prosumers */
	protected int nProsumer;

	/** Algorithm used to find path between two prosumers */
	protected Algorithm algorithm;

	/** The rest energy need by prosumer */
	protected double rest;

	/**
	 * Build the plan
	 * 
	 * @param network
	 */
	public Plan(NodeNetwork network) {

		this.network = network;
		this.nProsumer = this.network.getNumberOfProsumers();

	}

	/**
	 * Initial variables for plan
	 */
	protected abstract void initial();

	/**
	 * Give a plan for transfering energy_require to consumer
	 * 
	 * @param consumer
	 * @param energy_require
	 */
	public abstract void plan(int consumer, double energy_require);
	
	/**
	 * Find index of minimum positive value in the array
	 * 
	 * @param array
	 * @return An integer
	 */
	protected int findPosMin(double[] array) {
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
