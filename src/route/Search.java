package route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Node;
/**
 * Top-level class used when implementing a new search-algorithm
 * @author Jostein
 *
 */
public abstract class Search {
	/***************************FIELDS***************************/

	/**
	 * The initial node.
	 * The Root of the search-tree.
	 */
	private Node startNode;
	/**
	 * The desired, final node.
	 */
	private Node goalNode;
	
	/**
	 * For keeping track of which parent node was expanded to reach a child node.
	 * Formatted like this: child(to),parent(from).
	 */
	protected Map<Node, Node> expansionList = new HashMap<Node, Node>();
	
	/**
	 * The actual path followed by the search-algorithm, from parent to child - in reverse order.
	 */
	protected List<Node> path = new ArrayList<Node>();
	
	/***************************FIELDS***************************/
	/***************************METHODS***************************/

	/**
	 * 
	 * @return The Node indicating where in the search-tree a search was started
	 * @see {@link #startNode}
	 */
	public Node getStartNode() {
		return this.startNode;
	}

	/**
	 * Changes the startNode to use for searches, and resets the state of {@link #expansionList} and {@link #path}.
	 * @param startNode The new startNode
	 */
	public void setStartNode(Node startNode) {
		if(getStartNode()!=startNode){
			this.startNode = startNode;
			this.path.clear();
			this.expansionList.clear();
			//TODO This does not reset any searches in progress though...
		}
	}

	/**
	 * @return The Node we want the search to find
	 * @see {@link #goalNode}
	 */
	public Node getGoalNode() {
		return this.goalNode;
	}

	/**
	 * Changes the goalNode to use for searches, and resets the state of {@link #expansionList} and {@link #path}.
	 * @param goalNode The new goalNode
	 */
	public void setGoalNode(Node goalNode) {
		if(getGoalNode()!=goalNode){
			this.goalNode = goalNode;
			this.path.clear();
			this.expansionList.clear();
			//TODO This does not reset any searches in progress though...
		}
	}

	/**
	 * Expands the start node and the following child nodes until the goal node can be found.
	 * {@link #setStartNode(Node)} and {@link #setGoalNode(Node)} should be called before this method.
	 * @return The nodes expanded to reach the goal node from the start node - in reverse order; see {@link #getPath(Map, DistanceMetricNode, DistanceMetricNode)}
	 */
	public abstract List<Node> findPath ();

	/**
	 * TODO BigDecimal might provide better accuracy in these calculations.
	 * Finds the distance between two nodes by comparing their latitude and longitude.
	 * @param latitude1 The latitude of the first node
	 * @param longitude1 The longitude of the first node
	 * @param latitude2 The latitude of the second node
	 * @param longitude2 The longitude of the second node
	 * @return The absolute distance between the two nodes
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
	 * Finds the distance between two nodes by comparing their latitude and longitude.
	 * @param node1 The first Node
	 * @param node2 The second Node
	 * @return The distance between the two nodes
	 */
	public static double distanceBetweenPoints(Node node1, Node node2){
		return distanceBetweenPoints(node1.getLatitude(),node1.getLongitude(),node2.getLatitude(),node2.getLongitude());
	}

	/**
	 * Goes through the list of expanded nodes from the back to the front, and returns the complete path from the goal node to the start node, or null if a complete path does not exist.
	 * @param prevNode2 List of expanded nodes and which node they were expanded from
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return A sequence of the nodes that represent the path from the start node to the goal node - in reverse order - or null if a path could not be found 
	 */
	protected List<Node> getPath(Map<Node, Node> expansionList,Node startNode, Node goalNode){

		List<Node>path = new ArrayList<Node>();
		Node previousNode=goalNode;

		while(previousNode!=null){
			//System.out.println("\nChild: "+previousNode.getId());
			
			path.add(previousNode);
			//if the goalNode was never reached, previousNode will be set to null here, and this loop ends
			previousNode=expansionList.remove(previousNode);
			
			//System.out.println("Parent: "+previousNode.getId());
		}

		if(!(path.get(path.size()-1)==startNode)){
			//In case we were unable to find a complete path back to the start-node from the goal-node
			path.clear();
		}

		return path;
	}
	/***************************METHODS***************************/

}
