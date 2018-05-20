package Plan;

import java.util.ArrayList;

public class Flow {

	private ArrayList<Integer> path;

	// energy that transmit through this path
	private double energy;

	private double resistance_total;

	public Flow(ArrayList<Integer> path, double energy, double resistance ) {
		this.path = path;
		this.energy = energy;
		this.resistance_total = resistance;
	}

	public ArrayList<Integer> getPath() {
		return this.path;
	}

	public double getEnergy() {
		return this.energy;
	}

	public double getResistance() {
		return this.resistance_total;
	}

	public double getEnergyLoss(double voltage, double time_interval) {
		return (this.energy * this.energy * this.resistance_total) / (time_interval * voltage * voltage);
	}
}
