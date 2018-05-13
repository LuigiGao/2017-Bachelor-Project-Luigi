package Plan;

import java.util.ArrayList;

import javafx.util.Pair;

public class PathSolution {

	// <prosumer_id, amount of energy provided>
	private ArrayList<Pair<Integer, Double>> providers;
	// energy provided by electric utility
	private double rest;
	
	public PathSolution( ) {
		this.providers = new ArrayList<Pair<Integer, Double>>( );
		this.rest = 0.0;
	}
	
	public void insertProvider( Pair<Integer, Double> provider ) {
		this.providers.add( provider );
	}
	
	public void setRest( double rest ) {
		this.rest = rest;
	}
	
	public ArrayList<Pair<Integer, Double>> getProviders( ){
		return this.providers;
	}
	
	public double getRest( ) {
		return this.rest;
	}
	
}
