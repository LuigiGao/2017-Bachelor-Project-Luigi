package Algorithm;

import java.util.ArrayList;
import java.util.Collections;

import Network.NodeNetwork;

public class DijkstraAlgorithm extends Algorithm {

	// sum of resistance of path
	// private double minDistance;

	// visited[i] = 0 means node i is not visited
	private int[] visited;

	private double[] distances;
	private Integer[] previous;

	public DijkstraAlgorithm(NodeNetwork graph) {
		super(graph);
	}

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

	public ArrayList<Integer> findShortestPath(int source, int dest) {

		// initial the variables
		initial(source, dest);

		// find closest path to des
		for( int i = 0; i < this.nNodes; i++ ) {
			int node = findUnvisitedMinNode();
			// node = this.dest means find closest path
			// node = -1 means unreachable
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

		ArrayList<Integer> path = new ArrayList<Integer>();
		Integer p = this.previous[this.dest];
		while (p != null) {
			path.add(p);
			p = this.previous[p];
		}

		/*
		// reverse the array from <1,2,4> to <4,2,1> means energy flow 4 -> 2 -> 1
		if( path.size() > 0 )
			Collections.reverse(path);
		*/

		// if not reachable, return null
		// otherwise return a a arrayList
		return path;
	}

}
