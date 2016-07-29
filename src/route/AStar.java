package route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Map;

import database.DistanceComparator;
import database.DistanceMetricNode;
import database.Node;
import database.SearchDatabase;
import database.Way;

/**
 * Uses the A* algorithm to find a path from one {@link #startNode} to a {@link #goalNode}, via the nodes connected to the start node.
 * Expands the nodes closest to the {@link #goalNode} first, using a priority queue.
 * The path will be complete and optimal, as is key with the A* algorithm.
 * @author Jostein Kristiansen(jok13)
 *@see database.BuildDatabase
 */
public class AStar {

	/**
	 * The initial node
	 */
	private static DistanceMetricNode startNode;
	/**
	 * The desired, final node
	 */
	private static DistanceMetricNode goalNode;

	/**
	 * The sum of the distances between all Nodes in a path
	 */
	private static double totalPathDistance=Double.POSITIVE_INFINITY;

	static final Comparator<DistanceMetricNode> c = new DistanceComparator();
	/**
	 * For organising the order of the nodes to be expanded
	 */
	static PriorityQueue<DistanceMetricNode> priorityQueue = new PriorityQueue<DistanceMetricNode>(/*(BuildDatabase.getNodes().size()/2) ,*/ c);


	/**
	 * For preventing revisiting of nodes, potentially ending in a loop.
	 * TODO EMPTY String: 40Bytes - Pointer: 32/64 bit
	 * TODO change to something other than ArrayList
	 * If this list stored a pointer to the Node instead of just its id, would that increase performance? Probably
	 */
	private static List<Node> visitedNodes = new ArrayList<Node>();
	/**
	 * For handling the children of expanded nodes before they are passed into the queueList
	 * TODO Change to something other than ArrayList
	 */
	private static List<DistanceMetricNode> childList = new ArrayList<DistanceMetricNode>();
	/**
	 * For keeping track of which parent node was expanded to reach a child node.
	 * Formatted like this: child(to),parent(from)
	 * TODO hashmap? parent is unique key, child is value?
	 */
	private static Map<DistanceMetricNode,DistanceMetricNode> prevNode = new HashMap<DistanceMetricNode, DistanceMetricNode>();

	protected static DistanceMetricNode getStartNode() {
		return startNode;
	}

	protected static void setStartNode(DistanceMetricNode startNode) {
		AStar.startNode = startNode;
	}

	protected static DistanceMetricNode getGoalNode() {
		return goalNode;
	}

	protected static void setGoalNode(DistanceMetricNode goalNode) {
		AStar.goalNode = goalNode;
	}

	/**
	 * Expands the start node and the following child nodes until the goal node can be found
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return The nodes expanded to reach the goal node from the start node - in reverse order; see {@link #getPath(Map, DistanceMetricNode, DistanceMetricNode)}
	 */
	public static List<Node> search (DistanceMetricNode startNd, DistanceMetricNode goalNd){
		setStartNode(startNd);
		setGoalNode(goalNd);

		List<Node> path = new ArrayList<Node>();

		DistanceMetricNode currentNode=startNode;
		currentNode.setDistanceTravelled(0);


		for(Node node:getNavigatableConnectedNodes(currentNode.getId())){
			//Is it safe to cast like this?
			priorityQueue.add((DistanceMetricNode) node);
		}

		visitedNodes.add(currentNode);

		/* pq.removeAll(visitedNodes) would also work here,
		 * but then I'd need to iterate over the list again in order to fill prevNode
		 * */
		Iterator<DistanceMetricNode> i = priorityQueue.iterator();
		while(i.hasNext()){
			DistanceMetricNode node = i.next();
			if(visitedNodes.contains(node.getId())){
				i.remove();//So that we do not search for it again
			}else{
				prevNode.put(node,startNode);
				node.setDistanceTravelled(distanceBetweenPoints(startNode.getLatitude(),startNode.getLongitude(),node.getLatitude(),node.getLongitude()));
			}
		}

		while(priorityQueue.isEmpty()==false){

			currentNode=priorityQueue.poll();//To pop the last element in this list.
			visitedNodes.add(currentNode);//The list can get quite large, so popping the first element would require moving lots of elements one position forwards in the list

			if(currentNode.getId().equals(goalNd.getId())){
				/* If currentNode==goalNode, then there can not be any shorter path to the goalNode,
				 * as all shorter paths are guaranteed have been explored.
				 * (Assuming my implementation of AStar is correct)
				 */
				totalPathDistance=currentNode.getDistanceTravelled();/*would always be 0, as currentNode==goalNode here*/
				break;
			}else{

				//TODO Also adds the parent itself as a child... Currently (1 second / 2x) faster than filtering it out
				for(Node child:getNavigatableConnectedNodes(currentNode.getId())){
					if(!(child.equals(currentNode))){
						//TODO is it ok to cast like this?
						childList.add((DistanceMetricNode) child);
					}
				}

				Iterator<DistanceMetricNode> j = childList.iterator();
				while(j.hasNext()){
					DistanceMetricNode node = j.next();

					if(node.getId().equals(currentNode.getId())||visitedNodes.contains(node.getId())||priorityQueue.contains(node)){
						j.remove();//Faster to do it here than in the queueArray, as that array is most likely much larger
					}else{
						node.setDistanceTravelled(currentNode.getDistanceTravelled()+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),node.getLatitude(),node.getLongitude()));
						node.setGoalDistance(distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),goalNd.getLatitude(),goalNd.getLongitude()));
					}
				}

				for(DistanceMetricNode node:childList){
					priorityQueue.add(node);
					prevNode.putIfAbsent(node,currentNode);
				}

				/*
				 * This data has been passed on to the queueList, so it is no longer needed,
				 * but it might be best to not shrink the size of this list,
				 * as that might result in excessive resizing in the future -
				 * .clear() provides this functionality.
				 */
				childList.clear();
			}
		}
		if(totalPathDistance<Double.POSITIVE_INFINITY){
			path=getPath(prevNode,startNode,goalNode);
		}

		return path;
	}

	/**
	 * TODO Make sure that traveled distance is also taken into account here. distance=traveledDistance+distanceToGoalNode. Otherwise, this is greedy Best-First search, not A*
	 * **INSERTION SORT**
	 * Sorts a list of nodes by measuring the distance between each unsorted node and a goal node,
	 * and inserting the unsorted nodes into the list of sorted nodes one at a time.
	 * @param sortedNodes The list of nodes that have already been sorted
	 * @param unsortedNodes The list of nodes to be sorted into the list of sorted nodes
	 * @param goalNd The target node which will determine which order the nodes are sorted in {@link goalNode}
	 * @return A list of sorted nodes, where the nodes with the shortest distance to the goal are put last
	 * @throws NullPointerException
	 */
	protected static List<DistanceMetricNode> sortByDistance(List<DistanceMetricNode> sortedNodes, List<DistanceMetricNode> unsortedNodes) throws NullPointerException{

		double unsortedNodeDistance;

		if(sortedNodes.isEmpty()&&!unsortedNodes.isEmpty()){
			if(unsortedNodes.get(unsortedNodes.size()-1).getDistanceToGoal()<=0){
				unsortedNodes.get(unsortedNodes.size()-1).setGoalDistance(AStar.distanceBetweenPoints(unsortedNodes.get(unsortedNodes.size()-1).getLatitude(), unsortedNodes.get(unsortedNodes.size()-1).getLongitude(), goalNode.getLatitude(), goalNode.getLongitude()));
			}
			sortedNodes.add(unsortedNodes.remove(unsortedNodes.size()-1));//to pop the last element of the list. Moves fewer (none) elements in larger lists 
		}

		for(DistanceMetricNode unsortedNode:unsortedNodes){//next unsorted node
			if(unsortedNode.getDistanceToGoal()<=0){
				unsortedNode.setGoalDistance(AStar.distanceBetweenPoints(unsortedNode.getLatitude(), unsortedNode.getLongitude(), goalNode.getLatitude(), goalNode.getLongitude()));
			}
			unsortedNodeDistance=(unsortedNode.getDistanceTravelled()+unsortedNode.getDistanceToGoal());
			for(int x=sortedNodes.size()-1; x>=0;x--){//next sorted node
				if(unsortedNodeDistance<=(sortedNodes.get(x).getDistanceTravelled()+sortedNodes.get(x).getDistanceToGoal())){
					sortedNodes.add(x+1, unsortedNode);//reverse order. smallest at the back
					break;
				}else if(x==0){
					sortedNodes.add(0, unsortedNode);
					break;
				}
			}

		}
		return sortedNodes;
	}

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

		double distance = Math.sqrt(Math.pow(Math.abs(latitude1-latitude2),2)+ Math.pow(Math.abs(longitude1-longitude2),2));

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
	protected static List<Node> getPath(Map<DistanceMetricNode, DistanceMetricNode> visitedList,DistanceMetricNode startNd, DistanceMetricNode goalNd){
		setGoalNode(goalNd);
		setStartNode(startNd);

		List<Node>path = new ArrayList<Node>();
		path.add(goalNode);
		Node previousNode=goalNode;//splitPath(expandedNodes.get(expandedNodes.size()-1),1);//To register the goal node which should be the last node expanded

		while(!(previousNode.equals(startNode))){
			//System.out.println("\nfrom: "+previousNode.getId());
			previousNode=SearchDatabase.searchForNode(visitedList.get(previousNode).getlocalId());
			//System.out.println("to: "+previousNode.getId());

			if(previousNode.equals(null)){
				//just to be safe
				break;
			}
			path.add(previousNode);
		}

		if(!(path.get(path.size()-1).equals(startNode))){
			//If we were unable to find a complete path
			return null;
		}

		return path;
	}

	/**
	 * Searches for the Nodes connected to the input, and returns only the Nodes fit for navigation, and the Nodes connected to the goal(regardless) - if found
	 * @param parentNode Node to expand (find references to)
	 * @return The children of the parent. I.e every Node in a Way related to this Node
	 * @see database.SearchDatabase #FilterAccessibleWays(List)
	 */
	protected static List<Node> getNavigatableConnectedNodes(String parentNode){
		List<Way> wayList=SearchDatabase.getWaysContainingNode(parentNode);
		List<Node>nodeList=SearchDatabase.filterAccessibleNodes(wayList);//This can remove a Way containing the goal; the code below deals with this

		//Node pNode=SearchDatabase.searchForNode(parentNode);

		//TODO This makes sure that every Node in a Way with a connection to the goal is returned regardless of their suitability for navigation.
		//TODO Should only Nodes in suitable Ways be returned, or is this okay?
		for(Way way:wayList){
			//Makes sure that the list of Nodes fit for navigation contains the goal if it has been found in one or more Ways
			if(way.getNodeRelations().contains(goalNode)&&!nodeList.containsAll(way.getNodeRelations())){
				for(Node node:way.getNodeRelations()){
					if(!nodeList.contains(node)){
						nodeList.add(node);
					}
				}
			}
		}

		//The following seems like a slow (>O(n)) way of ensuring that the parent node isn't returned
		//Adds an additional second to the runtime of AllTests (O(t*2)) - This makes this process incredibly unnecessary 
		//nodeList.removeIf(pNode::equals);

		return nodeList;
	}
}
