package Network;

import java.util.ArrayList;

import Analysis.Analyzer;
import Plan.Flow;
import Weather.Weather;

/**
 * A class analysis the performance of the smart grid system
 * @author Luigi
 *
 */

public class Network_Analysis extends Network {

	/** Store and analysis the smart grid data */
	protected Analyzer analyzer;
	
	public Network_Analysis(String prosumer_info, String relation_info, String weather_info, int remaining_day) {
		super(prosumer_info, relation_info, weather_info, remaining_day);
		this.analyzer = new Analyzer(prosumers.size(), this);
	}
	
	@Override
	public void updateEnergyFlow(ArrayList<Integer> path, double energy, double resistance) {

		// update data for analysis
		int provider = path.get(0);
		int consumer = path.get(path.size() - 1);
		// * 1000 because of unit of energy is kWh
		double loss = (energy * energy * resistance) / (1 * this.voltage * this.voltage) * 1000;
		this.analyzer.energyFlow(provider, consumer, energy, loss, this.prosumers_price[provider]);

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

		double loss = flow.getEnergyLoss(this.voltage, 1, energy);
		double energy_require = energy + loss;
		double capacity = getCapacity(flow.getPath());

		if (energy_require < capacity) {
			updateEnergyFlow(flow.getPath(), energy_require, flow.getResistance());
			return 0.0;
		}

		updateEnergyFlow(flow.getPath(), capacity, flow.getResistance());
		return energy - flow.receivedEnergy(capacity, this.voltage, 1);
	}

	@Override
	public void buyEnergyEU(int prosumer, double energy) {
		if (energy > 0) {
			double loss = energyLossEU(prosumer, energy);
			
			// update data for analysis
			this.analyzer.buyEnergyFromEU(prosumer, energy + loss, loss, priceEU_sell);
		}
	}
	
	@Override
	protected void simulation_prosumers(double min_wind_speed, double max_wind_speed, double solar_radiation,
			double temperature, int time_in_day) {
		int id;
		for (Prosumer p : this.prosumers) {
			id = p.getID();
			double energyProduce = p.energyProduce(min_wind_speed, max_wind_speed, solar_radiation, temperature);
			double energyConsume = p.energyConsume(time_in_day);
			this.analyzer.energyProdcution(id, energyProduce);
			this.analyzer.energyConsumption(id, energyConsume);
			
			this.prosumers_energy[id] = energyProduce - energyConsume;
			this.prosumers_price[id] = p.getCurrentPrice();
		}
	}
	
	@Override
	protected  void simulation_topology() {

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
				// update data for analysis
				double loss = this.energyLossEU(i, prosumers_energy[i]);
				this.analyzer.sellEnergyToEU(i, prosumers_energy[i], loss, this.priceEU_buy);
			}
		}

		// clear matrix for current status for next simulation
		resetSimulation();
	}

	@Override
	protected void simulation_hour(Weather weather, int time_in_day) {
		// simulate weather -> renewable energy production -> consumption for each
		// prosumer -> optimising the energy flow

		// in 1 m/s
		double min_wind_speed = weather.getMinWindSpeed();
		double max_wind_speed = weather.getMaxWindSpeed();

		double solar_radiation = weather.getSolarRadiation(time_in_day);
		double temperature = weather.estimateTemperature(time_in_day);

		simulation_prosumers(min_wind_speed, max_wind_speed, solar_radiation, temperature, time_in_day);

		simulation_topology( );

	}
	
	@Override
	protected void simulation_day() {
		int hour = 0;
		Weather weather = this.weather_database.nextWeather();

		for (; hour < 24; hour++) {
			// simulate the consume and produce per hour
			simulation_hour(weather, hour);
			
		}
	}
	
	@Override
	public void run() {

		// print prosumer information
		for (Prosumer p : prosumers)
			System.out.println(p.getGeneratorInfo());

		System.out.println( "Total Days: " + remaining_day );
		System.out.println( );
		
		while (this.remaining_day > 0) {
			this.remaining_day--;
			// simulate the consume and produce per day
			simulation_day();
		}

		// analysis data
		System.out.println( "Analysis: " );
		this.analyzer.analysis();

	}

}
