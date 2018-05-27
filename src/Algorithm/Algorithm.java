package Algorithm;

import java.util.ArrayList;

import Network.NodeNetwork;

/**
 * A template of algorithm which apply to find a path
 * 
 * @author Luigi
 *
 */

public abstract class Algorithm {

	/** A graph which contains nodes and relations */
	protected NodeNetwork graph;
	
	/** number of nodes */
	// nNodes = nodes_seller + nodes_consumer
	protected int nNodes;
	
	/** Source where the path start */
	// the prosumer that sells energy
	protected int source;
	
	/** Destination where the path end */
	// the prosumer that want to buy energy
	protected int dest;
	
	/**
	 * Build the algorithm
	 * @param graph
	 */
	public Algorithm( NodeNetwork graph ) {
		this.graph = graph;
		this.nNodes = this.graph.getNumberOfProsumers( );
	}
	
	/**
	 * Initial the variables for finding path using same algorithm
	 * @param source
	 * @param dest
	 */
	protected void initial( int source, int dest ) {
		this.source = source;
		this.dest = dest;
	}
	
	/**
	 * Find a path from source to dest
	 * @param source
	 * @param dest
	 * @return A array of integer indicates the path
	 */
	public abstract ArrayList<Integer> findPath(int source, int dest);

	/**
	 * Find a path from source to dest with given loads
	 * @param source
	 * @param dest
	 * @param loads
	 * @return A array of integer indicates the path
	 */
	public abstract ArrayList<Integer> findPath(int source, int dest, double[][] loads);
	
}
