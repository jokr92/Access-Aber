package route;

import java.util.HashMap;
import java.util.Map;

import database.Node;

abstract class InformedSearch extends Search {
	
	/**
	 * The total path-cost accumulated by this Node's parent nodes, starting at {@link #getStartNode()}.
	 * Format: Node , path-cost
	 */
	private Map<Node, Double> pathCost = new HashMap<Node, Double>();
	/**
	 * The straight-line distance from a Node to the goalNode.
	 * Format: <Node,straight-line distance>
	 */
	private Map<Node, Double> goalDistance = new HashMap<Node, Double>();
	
	
	/**
	 * 
	 * @param n The Node to update
	 * @param cost The path-cost of reaching this Node from the start Node, going through all of its parents
	 */
	void updatePathCost(Node n,double cost){
		pathCost.put(n, cost);
	}
	
	/**
	 * 
	 * @param n The Node to get the total path cost to
	 * @return The path cost required to reach this Node, or Positive Infinity if this value has not been calculated yet
	 */
	double getPathCost(Node n){
		double cost=pathCost.getOrDefault(n,Double.POSITIVE_INFINITY);
		return cost;
	}
	
	/**
	 * 
	 * @param n The Node to update
	 * @param distance The new shortest possible distance to the goal from this Node
	 */
	void updateGoalDistance(Node n, double distance){
		goalDistance.put(n, distance);
	}
	
	/**
	 * 
	 * @param n The Node to get the distance from
	 * @return The distance to the goal Node, or Positive Infinity if this value has not been calculated yet
	 */
	double getGoalDistance(Node n){
		double dist=goalDistance.getOrDefault(n,Double.POSITIVE_INFINITY);
		return dist;
	}
	
	/**
	 * Estimates the total path-cost from the received Node to the goalNode.
	 * The estimate is based on the path-cost required to reach the Node from its parents and the straight-line distance to the goalNode
	 * @param n The Node to estimate the shortest possible total path-cost for
	 * @return the smallest possible path-cost from this Node to the goalNode, plus the path-cost required to reach this Node from its parents
	 * @see #getPathCost(Node)
	 * @see #getGoalDistance(Node)
	 */
	double getEstimatedMinimalCost(Node n){
		double cost=getPathCost(n);
		double dist=getGoalDistance(n);
		return (cost+dist);
	}
}
