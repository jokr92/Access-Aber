package route;

import java.util.HashMap;
import java.util.Map;

import database.Node;

abstract class InformedSearch extends Search {
	
	/**
	 * The total path-cost accumulated by this Node's parent nodes, starting at {@link #getStartNode()}.
	 * Format: <Node,path cost>
	 */
	private Map<Node, Double> pathCost = new HashMap<Node, Double>();
	/**
	 * The straight-line distance from a Node to the goalNode.
	 * Format: <Node,straight-line distance>
	 */
	private Map<Node, Double> goalDistance = new HashMap<Node, Double>();
	
	
	
	void updatePathCost(Node n,double cost){
		pathCost.put(n, cost);
	}
	double getPathCost(Node n){
		double cost=pathCost.getOrDefault(n,Double.POSITIVE_INFINITY);
		return cost;
	}
	
	void updateGoalDistance(Node n, double distance){
		goalDistance.put(n, distance);
	}
	double getGoalDistance(Node n){
		double dist=goalDistance.getOrDefault(n,Double.POSITIVE_INFINITY);
		return dist;
	}
	
	/**
	 * Estimates the total path-cost from the received Node to the goalNode.
	 * The estimate is based on the path-cost required to reach the Node from its parents and the straight-line distance to the goalNode
	 * @param n The Node to estimate the total path-cost for
	 * @return the smallest possible path-cost from this Node to the goalNode
	 */
	double getEstimatedMinimalCost(Node n){
		double cost=getPathCost(n);
		double dist=getGoalDistance(n);
		return (cost+dist);
	}
}
