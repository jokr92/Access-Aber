package route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import database.BuildDatabase;
import database.DistanceComparator;
import database.Node;
import database.DistanceMetricNode;
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

	/**
	 * For organising the order of the nodes to be expanded - reverse order
	 * TODO Would a linked-list or heap be better here? I've read that it works well with queues... This is a priority-queue though
	 */
	private static List<Node> queueLis = new ArrayList<Node>();

	static final Comparator<DistanceMetricNode> c = new DistanceComparator();
	static PriorityQueue<DistanceMetricNode> PATIENTQueue = new PriorityQueue<DistanceMetricNode>(/*(BuildDatabase.getNodes().size()/2) ,*/ c);


	/**
	 * For preventing revisiting of nodes, potentially ending in a loop. Holds Ids
	 * TODO Does a String use less memory than a pointer? Probably not
	 * If this list stored a pointer to the Node instead of just its id, would that increase performance? Probably
	 */
	private static List<String> visitedNodes = new ArrayList<String>();
	/**
	 * For handling the children of expanded nodes before they are passed into the queueList
	 */
	private static List<DistanceMetricNode> childList = new ArrayList<DistanceMetricNode>();
	/**
	 * For keeping track of which parent node was expanded to reach a child node.
	 * Formatted like this: child(to),parent(from)
	 * TODO hashmap? parent is unique key, child is value?
	 */
	private static List<String> prevNode = new ArrayList<String>();

	public static Node getStartNode() {
		return startNode;
	}

	public static void setStartNode(DistanceMetricNode startNode) {
		AStar.startNode = startNode;
	}

	public static Node getGoalNode() {
		return goalNode;
	}

	public static void setGoalNode(DistanceMetricNode goalNode) {
		AStar.goalNode = goalNode;
	}

	/**
	 * Expands the start node and the following child nodes until the goal node can be found
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return The nodes expanded to reach the goal node from the start node - in reverse order {@link #getPath(List, String, String)}
	 */
	public static List<DistanceMetricNode> search (DistanceMetricNode startNd, DistanceMetricNode goalNd){
		setStartNode(startNd);
		setGoalNode(goalNd);

		List<DistanceMetricNode> path = new ArrayList<DistanceMetricNode>();

		DistanceMetricNode currentNode=startNode;
		currentNode.setDistanceTravelled(0);

		
		for(Node node:getNavigatableConnectedNodes(currentNode.getId())){
			//Is it safe to cast like this?
			PATIENTQueue.add((DistanceMetricNode) node);
		}

		visitedNodes.add(currentNode.getId());

		/* pq.removeAll(visitedNodes) would also work here,
		 * but then I'd need to iterate over the list again on order to fill prevNode
		 * */
		Iterator<DistanceMetricNode> i = PATIENTQueue.iterator();
		while(i.hasNext()){
			DistanceMetricNode node = i.next();
			if(visitedNodes.contains(node.getId())){
				i.remove();//So that we do not search for it again
			}else{
				prevNode.add(node.getId()+","+startNode.getId());
				node.setDistanceTravelled(distanceBetweenPoints(startNode.getLatitude(),startNode.getLongitude(),node.getLatitude(),node.getLongitude()));
			}
		}

		while(PATIENTQueue.isEmpty()==false){

			currentNode=PATIENTQueue.poll();//To pop the last element in this list.
			visitedNodes.add(currentNode.getId());//The list can get quite large, so popping the first element would require moving lots of elements one position forwards in the list

			if(currentNode.getId().equals(goalNd.getId())){
				/* If currentNode==goalNode, then there can not be any shorter path to the goalNode,
				 * as all shorter paths are guaranteed have been explored.
				 * (Assuming my implementation of AStar is correct)
				 */
				totalPathDistance=currentNode.getDistanceTravelled();/*would always be 0, as currentNode==goalNode*///+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),goalNode.getLatitude(),goalNode.getLongitude());
				//visitedNodes.add(goalNode.getId());					//This is done already
				//prevNode.add(goalNode.getId()+","+currentNode.getId());//This would make the goalNode the parent of itself
				break;
			}else{
			
			//TODO Also adds the parent itself as a child... Currently (1 second / 2x) faster than filtering it out
			//childList.addAll(getNavigatableConnectedNodes(currentNode.getId()));
			for(Node child:getNavigatableConnectedNodes(currentNode.getId())){
				if(!(child.equals(currentNode)))
					//TODO is it ok to cast like this?
					childList.add((DistanceMetricNode) child);
			}

				Iterator<DistanceMetricNode> j = childList.iterator();
				while(j.hasNext()){
					DistanceMetricNode node = j.next();

					if(node.equals(currentNode)||visitedNodes.contains(node.getId())||PATIENTQueue.contains(node)){
						j.remove();//Faster to do it here than in the queueArray, as that array is most likely much larger
					}else{
						node.setDistanceTravelled(currentNode.getDistanceTravelled()+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),node.getLatitude(),node.getLongitude()));
						node.setGoalDistance(distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),goalNd.getLatitude(),goalNd.getLongitude()));
					}
				}

				for(DistanceMetricNode node:childList){
					PATIENTQueue.add(node);
					prevNode.add(node.getId()+","+currentNode.getId());
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
	static List<DistanceMetricNode> sortByDistance(List<DistanceMetricNode> sortedNodes, List<DistanceMetricNode> unsortedNodes) throws NullPointerException{

		double unsortedNodeDistance;

		if(sortedNodes.isEmpty()&&!unsortedNodes.isEmpty()){
			if(unsortedNodes.get(unsortedNodes.size()-1).getDistanceToGoal()<=0){
				unsortedNodes.get(unsortedNodes.size()-1).setGoalDistance(AStar.distanceBetweenPoints(unsortedNodes.get(unsortedNodes.size()-1).getLatitude(), unsortedNodes.get(unsortedNodes.size()-1).getLongitude(), goalNode.getLatitude(), goalNode.getLongitude()));
			}
			sortedNodes.add(unsortedNodes.get(unsortedNodes.size()-1));//to pop the last element of the list. Moves fewer (none) elements in larger lists 
			unsortedNodes.remove(unsortedNodes.size()-1);
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

		double distance = (Math.abs(latitude1-latitude2)+Math.abs(longitude1-longitude2));

		/*This ensures that all distances are >0 (CRUCIAL FOR PATH-COST CALCULATIONS),
		 * but some accuracy is lost when dealing with nodes close to each other.
		 */
		return Math.max(distance, Double.MIN_VALUE);
	}

	/**
	 * TODO Could this be done in a hash table? "parent = key, child = value" for example
	 * Splits a string in two, and returns either the first or the second half
	 * @param node The node to split in two
	 * @param part	Indicates which part to return. 0 for the first half(child/to); 1 for the second(parent/from).
	 * @return	One half of the received String
	 */
	private static String splitPath(String node,int part){
		if(part==0){
			node=node.substring(0, node.indexOf(","));
		}else if(part==1){
			node=node.substring(node.indexOf(",")+1, node.length());
		}
		return node;
	}

	/**
	 * Goes through the list of expanded nodes from the back to the front, and returns the complete path from the goal node to the start node
	 * @param expandedNodes List of expanded nodes and which node they were expanded from
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return A sequence of the nodes that represent the path from the start node to the goal node, in reverse order
	 */
	private static List<DistanceMetricNode> getPath(List<String> expandedNodes,DistanceMetricNode startNd, DistanceMetricNode goalNd){
		goalNode=goalNd;
		startNode=startNd;

		List<DistanceMetricNode>path = new ArrayList<DistanceMetricNode>();
		path.add(goalNode);
		String previousNode=goalNode.getId();//splitPath(expandedNodes.get(expandedNodes.size()-1),1);//To register the goal node which should be the last node expanded

		for(int i=expandedNodes.size()-1;i>=0;i--){
			if(splitPath(expandedNodes.get(i),0).equals(previousNode)){
				previousNode=splitPath(expandedNodes.get(i),1);
				path.add((DistanceMetricNode) SearchDatabase.searchForNode(previousNode));
				if(splitPath(expandedNodes.get(i),1).equals(startNode.getId())){
					//System.out.println("startNode found");
					break;
				}
			}
		}

		return path;
	}

	/**
	 * Searches for the Nodes connected to the input, and returns only the Nodes fit for navigation, and the Nodes connected to the goal(regardless) - if found
	 * @param parentNode Node to expand (find references to)
	 * @return The children of the parent. I.e every Node in a Way related to this Node
	 * @see database.SearchDatabase #FilterAccessibleWays(List)
	 */
	static List<Node> getNavigatableConnectedNodes(String parentNode){
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
