package route;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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
	
	//static final Comparator<Node> distanceComparator = new DistanceComparator();

	/**
	 * For organising the order of the nodes to be expanded
	 */
	//PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(distanceComparator);
	
	PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(10, new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
        	if(pathCost.get(n1)+goalDistance.get(n1) > pathCost.get(n2)+goalDistance.get(n2)){
    			return 1;
    		}else if(pathCost.get(n1)+goalDistance.get(n1) < pathCost.get(n2)+goalDistance.get(n2)){
    			return -1;
    		}else{
    			//TODO The interface 'Node' can break distance-ties
    			//with what? the ID? That's not related to distance at all...
    			return 0;
    			}
        }
    });
	
	void updatePathCost(Node n,double cost){
		pathCost.put(n, cost);
	}
	double getPathCost(Node n){
		return pathCost.getOrDefault(n,Double.POSITIVE_INFINITY);
	}
	
	void updateGoalDistance(Node n, double distance){
		goalDistance.put(n, distance);
	}
	double getGoalDistance(Node n){
		return goalDistance.getOrDefault(n,Double.POSITIVE_INFINITY);
	}
	
	/**
	 * Estimates the total path-cost from the received Node to the goalNode.
	 * The estimate is based on the path-cost required to reach the Node from its parents and the straight-line distance to the goalNode
	 * @param n The Node to estimate the total path-cost for
	 * @return the smallest possible path-cost from this Node to the goalNode
	 */
	double getEstimatedMinimalCost(Node n){
		return (getPathCost(n)+getGoalDistance(n));
	}
}
