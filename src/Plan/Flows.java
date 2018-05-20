package Plan;

import java.util.ArrayList;

public class Flows {

	private int source;
	private int dest;
	private ArrayList<Flow> flows;
	private double energy_transmit;
	private double rest;
	
	public Flows( ) {
		this.flows = new ArrayList<Flow>( );
		this.energy_transmit = 0;
	}
	
	// energy_total is the total energy that source have
	public Flows( int source, int dest, double energy_total ) {
		this( );
		this.source = source;
		this.dest = dest;
		this.rest = energy_total;
	}
	
	public void addFlow( Flow flow ) {
		this.flows.add( flow );
		this.rest = this.rest - flow.getEnergy();
	}
	
	public ArrayList<Flow> getFlows( ){
		return this.flows;
	}
	
	private double getEnergyLoss( double voltage, double time_interval ){
		
		if( flows.size() <= 0 ) {
			System.out.println( "Error: no path from source to dest" );
			return -1;
		}
		
		double energy_loss = 0;
		
		for( Flow flow : flows ) {
			energy_loss = energy_loss + flow.getEnergyLoss(voltage, time_interval);
		}
		
		return energy_loss;
	}
	
	public double getEnergyLossPercentage( double voltage, double time_interval ) {
		return getEnergyLoss( voltage, time_interval ) / this.energy_transmit; // in 100 percent
	}
	
	public boolean exist( ) {
		return this.flows.size() > 0;
	}
	
}
