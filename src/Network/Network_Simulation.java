package Network;

import java.util.ArrayList;

import Plan.Flow;
import Weather.Weather;

/**
 * A class that simulate the working of smart grid
 * @author Luigi
 *
 */

public class Network_Simulation extends Network {

	public Network_Simulation(String prosumer_info, String relation_info, String weather_info, int remaining_day) {
		super(prosumer_info, relation_info, weather_info, remaining_day);
	}
	
	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	
	@Override
	public void updateEnergyFlow(ArrayList<Integer> path, double energy, double resistance) {

		// update status of network
		int index = 0;
		int prosumerA;
		int prosumerB;

		prosumerA = path.get(index);
		// prosumerA sell energy
		this.prosumers_energy[prosumerA] = this.prosumers_energy[prosumerA] - energy;
		index++;

		while (index < path.size()) {
			prosumerB = path.get(index);
			this.loads_status[prosumerA][prosumerB] = this.loads_status[prosumerA][prosumerB] + energy;

			prosumerA = prosumerB;
			index++;
		}
	}
	
	@Override
	public Double simulateEnergyFlow(Flow flow, double energy) {

		int source = flow.getSource();
		int dest = flow.getDest();

		double loss = flow.getEnergyLoss(this.voltage, 1, energy);
		double energy_require = energy + loss;
		double capacity = getCapacity(flow.getPath());

		if (energy_require < capacity) {
			updateEnergyFlow(flow.getPath(), energy_require, flow.getResistance());
			System.out.println("Prosumer " + getHolder(dest) + " buy " + round(energy_require, 2) + " energy from Prosumer " + getHolder(source));
			return 0.0;
		}

		updateEnergyFlow(flow.getPath(), capacity, flow.getResistance());
		System.out.println("Prosumer " + getHolder(dest) + " buy " + round(capacity, 2) + " kWh from Prosumer " + getHolder(source));
		return energy - flow.receivedEnergy(capacity, this.voltage, 1);
	}

	@Override
	public void buyEnergyEU(int prosumer, double energy) {
		if (energy > 0) {
			System.out.println("Prosumer " + getHolder(prosumer) + " buy " 
							+ round(energy + energyLossEU(prosumer, energy), 2) + " kWh energy from Electric Utility");
		}
	}

	@Override
	protected void simulation_prosumers(double min_wind_speed, double max_wind_speed, double solar_radiation,
			double temperature, int time_in_day) {
		int id;
		for (Prosumer p : this.prosumers) {
			id = p.getID();
			// simulate net energy for each prosumer
			this.prosumers_energy[id] = p.output(min_wind_speed, max_wind_speed, solar_radiation, temperature, time_in_day);
			// simulate current price for each prosumer
			this.prosumers_price[id] = p.getCurrentPrice();
		}
	}
	
	@Override
	protected void simulation_topology() {

		// for each buyer find energy providers ( prosumers or electric utility )
		for (int i = 0; i < this.getNumberOfProsumers(); i++) {
			if (this.prosumers_energy[i] < 0)
				// plan and simulate the energy flow
				this.plan.plan(i, -this.prosumers_energy[i]);
			// this.planMinEnergyLoss.plan(i, -this.prosumers_energy[i]);
		}

		// finally sell rest energy to electric utility
		for (int i = 0; i < this.getNumberOfProsumers(); i++) {
			if (this.prosumers_energy[i] > 0) {
				System.out.println("Prosumer " + getHolder(i) + " sell " + round(prosumers_energy[i], 2) + " kWh energy to Electric Utility");
			}
		}

		// clear matrix for current status for next simulation
		resetSimulation();
	}
	
	@Override
	protected void simulation_hour(Weather weather, int time_in_day) {
		// simulate weather -> renewable energy production -> consumption for each
		// prosumer -> optimising the energy flow

		System.out.println("At " + time_in_day);

		// in 1 m/s
		double min_wind_speed = weather.getMinWindSpeed();
		double max_wind_speed = weather.getMaxWindSpeed();

		double solar_radiation = weather.getSolarRadiation(time_in_day);
		double temperature = weather.estimateTemperature(time_in_day);

		//weather information
		System.out.println("min wind speed: " + round(min_wind_speed, 2) + " max wind speed: " + round(max_wind_speed, 2)
				+ " solar radiation: " + round(solar_radiation, 2) + " temperature: " + round(temperature, 2) );

		simulation_prosumers(min_wind_speed, max_wind_speed, solar_radiation, temperature, time_in_day);

		simulation_topology( );

	}
	
	@Override
	protected void simulation_day() {
		int hour = 0;
		Weather weather = this.weather_database.nextWeather();
		System.out.println(weather.date() + ":");

		for (; hour < 24; hour++) {

			simulation_hour(weather, hour);
			
			try {
				Thread.sleep( 2000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void run() {
		
		// print prosumer information
		for (Prosumer p : prosumers)
			System.out.println(p.getGeneratorInfo());
		
		while (this.remaining_day > 0) {
			this.remaining_day--;
			// simulate the consume and produce per day
			simulation_day();
		}

	}
	
}
