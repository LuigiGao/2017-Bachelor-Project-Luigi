package Plan;

import java.util.ArrayList;

import Algorithm.Dijkstra;
import Network.NodeNetwork;
import javafx.util.Pair;

public class PlanMinCost {

	private NodeNetwork network;
	private Dijkstra algorithm;
	
	// used to store all rest energy need by prosumers
	private double[] rests;
	
	public PlanMinCost( NodeNetwork network ) {
		this.algorithm = new Dijkstra( network );
	}
	
	private EnergyFlow( int source, int dest, double energy ) {
		
		double rest = energy;
		ArrayList<Flow> flows = new ArrayList<Flow>( );
		
		ArrayList<Integer> path = algorithm.findShortestPath( source, dest );
		while( path.size() > 0 ) {
			
			double capacity = this.network.getCapacity( path );
			
			if( capacity > rest ) {

				// update flows balabala
				
				this.network.updateCapacity( path, rest );
				break;
			}else {

				// update flows balabala
				
				rest = rest - capacity;
				this.network.updateCapacity( path, capacity );
			}
			
			path = algorithm.findShortestPath( source, dest );
		}
		
		return flows;
	}
	
	private PathSolution findProvider( int house_id ){
		
		return null;
	}
	
	public ArrayList<Double> prosumerCost( ){
		
		return null;
	}
	
}
