package Algorithm;

import java.util.ArrayList;
import java.util.Collections;

import Network.Network;
import Plan.PlanBalanceCostEnergyLoss;

/**
 * A class that implements Dijkstra Algorithm to find path between two nodes
 * 
 * @author Luigi
 *
 */

public class DijkstraAlgorithm extends Algorithm {

	/** A array indicates whether the node is visited */
	// visited[i] = 0 means node i is not visited
	private int[] visited;

	/** A array contains the distance of all nodes to the source node */
	private double[] distances;
	
	/** A array contains the previous node for each node */
	// previous[1] = 2 means the previous node of node1 is node2
	private Integer[] previous;
	
	/**
	 * Build the algorithm
	 * @param graph
	 * @param plan
	 */
	public DijkstraAlgorithm(Network graph) {
		super(graph);
	}

	@Override
	protected void initial(int source, int dest) {
		super.initial(source, dest);
		this.visited = new int[this.nNodes];
		this.distances = new double[this.nNodes];
		this.previous = new Integer[this.nNodes];

		for (int i = 0; i < this.nNodes; i++) {
			this.visited[i] = 0;
			this.distances[i] = Double.POSITIVE_INFINITY;
			this.previous[i] = null;
		}
		this.distances[source] = 0;

	}

	/**
	 * Find the unvisited node with minimum distance
	 * @return A integer represents node
	 */
	private int findUnvisitedMinNode() {
		int node = -1;
		double distance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < this.nNodes; i++) {
			// if not visited
			if (this.visited[i] == 0) {
				// find the node with min distance
				if (distance > this.distances[i]) {
					node = i;
				}
			}
		}
		// mark the node to 'visited'
		if (node != -1) {
			this.visited[node] = 1;
		}
		// no available node return -1
		return node;
	}

	/**
	 * Create a path from source to dest
	 * @return A array of prosumer's id as path
	 */
	private ArrayList<Integer> createPath( ){
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add( this.dest );
		Integer p = this.previous[this.dest];
		while (p != null) {
			path.add(p);
			p = this.previous[p];
		}
		
		// reverse the array from <1,2,4> to <4,2,1> means energy flow 4 -> 2 -> 1
		Collections.reverse(path);
		
		return path;
	}
	
	@Override
	public ArrayList<Integer> findPath(int source, int dest) {

		// initial the variables
		initial(source, dest);

		// find closest path from source to dest
		for( int i = 0; i < this.nNodes; i++ ) {
			int node = findUnvisitedMinNode();
			
			// node = this.dest means find closest path and node = -1 means unreachable
			if (node == this.dest || node == -1)
				break;

			ArrayList<Integer> neighbours = this.graph.getNeighbours(node);

			for (int adj : neighbours) {
				double distance = this.distances[node] + this.graph.getResistance(node, adj);
				if (distance < this.distances[adj]) {
					this.distances[adj] = distance;
					this.previous[adj] = node;
				}
			}
		}
		
		return createPath( );
	}

	@Override
	public ArrayList<Integer> findPath(int source, int dest, double[][] loads) {

		// initial the variables
		initial(source, dest);

		// find closest path from source to dest
		for( int i = 0; i < this.nNodes; i++ ) {
			int node = findUnvisitedMinNode();
			
			// node = this.dest means find closest path and node = -1 means unreachable
			if (node == this.dest || node == -1)
				break;

			ArrayList<Integer> neighbours = this.graph.getNeighbours(node, loads);

			for (int adj : neighbours) {
				double distance = this.distances[node] + this.graph.getResistance(node, adj);
				if (distance < this.distances[adj]) {
					this.distances[adj] = distance;
					this.previous[adj] = node;
				}
			}
		}
		
		return createPath( );
	}
	
}
