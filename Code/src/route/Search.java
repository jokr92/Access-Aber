package route;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.Node;
import database.SearchDatabase;
import database.AreaAndBuildingTags;
import database.Way;
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

	private Duration runTime;//Used to measure time-complexity
	private long maxStoredNodes=0;//Used to measure space-complexity

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
	 * Expands the start node and the following child nodes until the goal node can be found.
	 * {@link #setStartNode(Node)} and {@link #setGoalNode(Node)} should be called before this method.
	 * @return The nodes expanded to reach the goal node from the start node - in reverse order; see {@link #getPath(Map, DistanceMetricNode, DistanceMetricNode)}
	 */
	public abstract List<Node> findPath ();

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
	 * 
	 * @return The runtime of this particular instance of a search
	 */
	public Duration getTimeElapsed() {
		return runTime;
	}

	/**
	 * Changes the approximate runtime of this instance of a search by looking at the time-difference between two recorded points in time
	 * @param start System-time at the start of the search
	 * @param end System-time at the end of the search
	 */
	protected void setTimeElapsed(Instant start, Instant end){
		runTime=Duration.between(start, end);
	}

	/**
	 * 
	 * @return The maximum number of Nodes stored in this particular search's queue
	 */
	public long getMaxNodesStored(){
		return maxStoredNodes;
	}

	/**
	 * Updates the counter keeping track of the maximum number of Nodes stored in a search's queue at any one time
	 * Only performs the update if the input value is greater than the value already stored in {@link #maxStoredNodes}
	 * @param numStoredNodes The number of Nodes stored in a search's queue at a single point in time
	 */
	protected void updateMaxStoredNodes(long numStoredNodes){
		if(maxStoredNodes<numStoredNodes){
			maxStoredNodes=numStoredNodes;
		}
	}

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
	protected static double distanceBetweenPoints(Node node1, Node node2){
		double dist=distanceBetweenPoints(node1.getLatitude(),node1.getLongitude(),node2.getLatitude(),node2.getLongitude());
		return dist;
	}

	/**
	 * Goes through the list of expanded nodes from the back to the front, and returns the complete path from the start node to the goal node, or null if a complete path does not exist.
	 * @param expansionList List of expanded nodes and which node they were expanded from
	 * @param startNode {@link #startNode}
	 * @param goalNode {@link #goalNode}
	 * @return A sequence of the nodes that represent the path from the start node to the goal node - or null if a path could not be found 
	 */
	protected List<Node> getPath(Map<Node, Node> expansionList,Node startNode, Node goalNode) throws ArrayIndexOutOfBoundsException{

		List<Node>path = new ArrayList<Node>();
		Node childNode=goalNode;
		Node parentNode;

		while(childNode!=null){
			path.add(childNode);

			parentNode=childNode;
			//if the goalNode was never reached, childNode will be set to null here, and this loop ends
			childNode=expansionList.remove(childNode);//assigns the value associated with this key - i.e its parent Node

			if(childNode!=null){
				/**********************Adds every Node between the parent and child-Node to the path**********************/
				List<String> parentChild = new ArrayList<String>();
				parentChild.add(parentNode.getExternalId());
				parentChild.add(childNode.getExternalId());

				boolean area=false;
				for(Way w:SearchDatabase.getWaysContainingNode(parentChild)){
					if(area==true){break;}

					for(Entry<String, Object> entry:w.getKeyValuePairs()){
						if(area==true){break;}
						for(AreaAndBuildingTags areaTag:AreaAndBuildingTags.values()){
							if(entry.getKey().equals(areaTag.getKey())&&entry.getValue().equals(areaTag.getValue())){
								area=true;
								break;
							}
						}
					}
				}

				if(area==false){
					double shortestDistance=Double.POSITIVE_INFINITY;
					List<Node> shortestPath = new ArrayList<Node>();
					for(Way way:SearchDatabase.getWaysContainingNode(parentChild)){
						List<Node> intermediatePath = new ArrayList<Node>();
						boolean parentFound=false, childFound=false;
						for(Node node:way.getNodeRelations()){

							if(node==childNode){childFound=true;}
							else if(node==parentNode){parentFound=true;}

							if(childFound^parentFound){
								intermediatePath.add(node);
							}else if(parentFound&&childFound){
								intermediatePath.add(node);

								/**********************Checks whether this path is shorter than any previous path**********************/
								double distance=0;
								for(int i=1;i<intermediatePath.size();i++){
									distance+=distanceBetweenPoints(intermediatePath.get(i-1),intermediatePath.get(i));
								}
								if(distance<shortestDistance&&distance>0){
									shortestDistance=distance;
									if(intermediatePath.get(0)==childNode){
										Collections.reverse(intermediatePath);
									}
									shortestPath=intermediatePath;
								}
								break;//Because both the parent and child has been found, the following Nodes are irrelevant
							}
						}
					}
					if(!shortestPath.isEmpty()){
						path.remove(path.size()-1);//TODO necessary?
						path.addAll(shortestPath);
						path.remove(path.size()-1);//TODO necessary?
					}
				}
			}
		}

		if(!(path.get(path.size()-1)==startNode)){
			//In case we were unable to find a complete path back to the start-node from the goal-node
			path.clear();
		}else{
			//Makes sure that the Nodes in the path are returned in the correct order (i.e going from startNode to goalNode)
			Collections.reverse(path);
		}

		return path;
	}
	/***************************METHODS***************************/

}
