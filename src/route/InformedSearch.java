package route;

import java.util.Comparator;
import java.util.PriorityQueue;

import database.DistanceComparator;
import database.DistanceMetricNode;

abstract class InformedSearch extends Search {
	
	/**
	 * The sum of the distances between all Nodes in a path
	 */
	double totalPathDistance=Double.POSITIVE_INFINITY;
	

	static final Comparator<DistanceMetricNode> distanceComparator = new DistanceComparator();

	/**
	 * For organising the order of the nodes to be expanded
	 */
	PriorityQueue<DistanceMetricNode> priorityQueue = new PriorityQueue<DistanceMetricNode>(distanceComparator);
}
