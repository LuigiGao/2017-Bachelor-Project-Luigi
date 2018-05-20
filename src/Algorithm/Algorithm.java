package Algorithm;

import java.util.ArrayList;

import Network.NodeNetwork;

public class Algorithm {

	protected NodeNetwork graph;
	
	// house id with extra energy
	protected ArrayList<Integer> nodes_seller;
	
	
	protected int nNodes;
	
	// resistance
	//private double[] relations;
	
	// the house want to buy energy
	protected int source;
	protected int dest;
	
	
	// only store the shortest path
	//private ArrayList<Integer> path;
	
	
	public Algorithm( NodeNetwork graph ) {
		this.graph = graph;
	}
	
	protected void initial( int source, int dest ) {
		this.nodes_seller = graph.getSellers( );
		//this.relations = graph.getConnections( source );
		this.source = source;
		this.dest = dest;
		//this.path = new ArrayList<Integer>( );
		this.nNodes = this.graph.getNumberOfProsumers( );
	}
	
	
}
