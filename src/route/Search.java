package route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Node;

public abstract class Search {
	/***************************FIELDS***************************/

	/**
	 * The initial node
	 */
	private static Node startNode;
	/**
	 * The desired, final node
	 */
	private static Node goalNode;
	
	/**
	 * For keeping track of which parent node was expanded to reach a child node.
	 * Formatted like this: child(to),parent(from)
	 */
	protected Map<Node, Node> expansionList = new HashMap<Node, Node>();
	
	/**
	 * The actual path followed by the search-algorithm - in reverse order
	 */
	List<Node> path = new ArrayList<Node>();
	
	/***************************FIELDS***************************/
	/***************************METHODS***************************/

	public static Node getStartNode() {
		return startNode;
	}

	protected static void setStartNode(Node startNode) {
		Search.startNode = startNode;
	}

	public static Node getGoalNode() {
		return goalNode;
	}

	protected static void setGoalNode(Node goalNode) {
		Search.goalNode = goalNode;
	}

	/**
	 * Expands the start node and the following child nodes until the goal node can be found
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return The nodes expanded to reach the goal node from the start node - in reverse order; see {@link #getPath(Map, DistanceMetricNode, DistanceMetricNode)}
	 */
	public abstract List<Node> findPath (Node startNd, Node goalNd);

	/**
	 * TODO BigDecimal might provide better accuracy in these calculations
	 * Finds the distance between two nodes by comparing their latitude and longitude
	 * @param latitude1 The latitude of the first node
	 * @param longitude1 The longitude of the first node
	 * @param latitude2 The latitude of the second node
	 * @param longitude2 The longitude of the second node
	 * @return The distance between the two nodes
	 */
	public static double distanceBetweenPoints(double latitude1, double longitude1, double latitude2, double longitude2){

		//Distance=squareRoot((x1-y1)^2 + (x2-y2)^2)
		double distance = Math.sqrt(Math.pow(latitude1-latitude2,2)+ Math.pow(longitude1-longitude2,2));

		/*This ensures that all distances are >0 (CRUCIAL FOR PATH-COST CALCULATIONS),
		 * but some accuracy is lost when dealing with nodes really close to each other.
		 * UPDATE(21.July.2016): These calculations seem to work now - at least with the path I have tested everything on. -
		 * - finding the distance from a node to itself will still return >0 (as it should)
		 */
		return Math.max(distance, Double.MIN_VALUE);
	}

	/**
	 * Goes through the list of expanded nodes from the back to the front, and returns the complete path from the goal node to the start node
	 * @param prevNode2 List of expanded nodes and which node they were expanded from
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return A sequence of the nodes that represent the path from the start node to the goal node, in reverse order
	 */
	protected List<Node> getPath(Map<Node, Node> expansionList,Node startNode, Node goalNode){

		List<Node>path = new ArrayList<Node>();
		Node previousNode=goalNode;

		while(previousNode!=null){
			//System.out.println("\nfrom: "+previousNode.getId());
			path.add(previousNode);
			previousNode=expansionList.remove(previousNode);
			//System.out.println("to: "+previousNode.getId());
		}

		if(!(path.get(path.size()-1)==startNode)){
			//In case we were unable to find a complete path back to the start-node from the goal-node
			return null;
		}

		return path;
	}
	/***************************METHODS***************************/

}
