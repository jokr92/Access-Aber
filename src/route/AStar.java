package route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private static Node startNode;
	/**
	 * The desired, final node
	 */
	private static Node goalNode;
	
	/**
	 * The sum of the distances between all Nodes in a path
	 */
	private static double totalPathDistance=Double.POSITIVE_INFINITY;
	
	/**
	 * For organising the order of the nodes to be expanded - reverse order
	 */
	private static List<Node> queueList = new ArrayList<Node>();
	/**
	 * For preventing revisiting of nodes, potentially ending in a loop. Holds Ids
	 * TODO Does a String use less memory than a pointer? If this list stored a pointer to the Node instead if just its id, maybe it would increase performance?
	 */
	private static List<String> visitedNodes = new ArrayList<String>();
	/**
	 * For handling the children of expanded nodes before they are passed into the queueList
	 */
	private static List<Node> childList = new ArrayList<Node>();
	/**
	 * For keeping track of which parent node was expanded to reach a child node.
	 * Formatted like this: child(to),parent(from)
	 */
	private static List<String> prevNode = new ArrayList<String>();
	
	public static Node getStartNode() {
		return startNode;
	}

	public static void setStartNode(Node startNode) {
		AStar.startNode = startNode;
	}

	public static Node getGoalNode() {
		return goalNode;
	}

	public static void setGoalNode(Node goalNode) {
		AStar.goalNode = goalNode;
	}

	/**
	 * Expands the start node, and the following child nodes, until the goal node can be found
	 * @param startNd {@link #startNode}
	 * @param goalNd {@link #goalNode}
	 * @return The nodes expanded to reach the goal node from the start node, in reverse order {@link #getPath(List, String, String)}
	 */
	public static List<Node> search (Node startNd, Node goalNd){
		setStartNode(startNd);
		setGoalNode(goalNd);
		
		List<Node> path = new ArrayList<Node>();
		
		Node currentNode=startNode;
		currentNode.setDistanceTravelled(0);
		
		queueList=sortByDistance(queueList, getNavigatableConnectedNodes(startNode.getId()));
		visitedNodes.add(startNode.getId());
		
		Iterator<Node> i = queueList.iterator();
		while(i.hasNext()){
			Node node = i.next();
			if(visitedNodes.contains(node.getId())){
				i.remove();//So that we do not search for it again
			}
		}
		for(Node node:queueList){
		prevNode.add(node.getId()+","+startNode.getId());
		node.setDistanceTravelled(distanceBetweenPoints(startNode.getLatitude(),startNode.getLongitude(),node.getLatitude(),node.getLongitude()));
		}
		
		if(queueList.contains(goalNode)){//In case the goal can be reached directly from the current position
			totalPathDistance=distanceBetweenPoints(startNode.getLatitude(),startNode.getLongitude(),goalNode.getLatitude(),goalNode.getLongitude());
			visitedNodes.add(goalNode.getId());
			prevNode.add(goalNode.getId()+","+startNode.getId());//TODO isn't this done above? line 89
		}else{
			while(queueList.isEmpty()==false&&((queueList.get(queueList.size()-1).getDistanceTravelled()+distanceBetweenPoints(queueList.get(queueList.size()-1).getLatitude(),queueList.get(queueList.size()-1).getLongitude(),goalNode.getLatitude(),goalNode.getLongitude()))<totalPathDistance)){

				currentNode=queueList.get(queueList.size()-1);//To pop the last element in this list.
				visitedNodes.add(currentNode.getId());//The list can get quite large, so popping the first element would require moving lots of elements one position forwards in the list
				queueList.remove(queueList.size()-1);

				childList.addAll(getNavigatableConnectedNodes(currentNode.getId()));
				
				if(childList.contains(goalNode)){
					totalPathDistance=currentNode.getDistanceTravelled()+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),goalNode.getLatitude(),goalNode.getLongitude());
					visitedNodes.add(goalNode.getId());
					prevNode.add(goalNode.getId()+","+currentNode.getId());
				}else {
					Iterator<Node> j = childList.iterator();
					while(j.hasNext()){
						Node node = j.next();
						if(visitedNodes.contains(node.getId())&&node.getDistanceTravelled()<=(currentNode.getDistanceTravelled()+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),node.getLatitude(),node.getLongitude()))){
							j.remove();//Faster to do it here than in the queueArray, as that array is most likely much larger
						}
						node.setDistanceTravelled(currentNode.getDistanceTravelled()+distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),node.getLatitude(),node.getLongitude()));
					}

					queueList=sortByDistance(queueList,childList);
					for(Node node:childList){
						prevNode.add(node.getId()+","+currentNode.getId());
					}
					childList.clear();//This data has been passed on to the queueList, so it is no longer needed
				}
			}
			if(totalPathDistance<Double.POSITIVE_INFINITY){
				path=getPath(prevNode,startNode,goalNode);
			}
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
	static List<Node> sortByDistance(List<Node> sortedNodes, List<Node> unsortedNodes) throws NullPointerException{
	
		double unsortedNodeLatitude;
		double unsortedNodeLongitude;
		double unsortedNodeDistance;
		double sortedNodeLatitude;
		double sortedNodeLongitude;
		
		if(sortedNodes.isEmpty()&&!unsortedNodes.isEmpty()){
			sortedNodes.add(unsortedNodes.get(unsortedNodes.size()-1));//to pop the last element of the list. This is smart if the list is quite large 
			unsortedNodes.remove(unsortedNodes.size()-1);
		}
		
		for(Node unsortedNode:unsortedNodes){//next unsorted node
			unsortedNodeLatitude=unsortedNode.getLatitude();
			unsortedNodeLongitude=unsortedNode.getLongitude();
			unsortedNodeDistance=(unsortedNode.getDistanceTravelled()+distanceBetweenPoints(unsortedNodeLatitude,unsortedNodeLongitude,goalNode.getLatitude(),goalNode.getLongitude()));
			for(int x=sortedNodes.size()-1; x>=0;x--){//next sorted node
				sortedNodeLatitude=sortedNodes.get(x).getLatitude();
				sortedNodeLongitude=sortedNodes.get(x).getLongitude();
				if(unsortedNodeDistance<=(sortedNodes.get(x).getDistanceTravelled()+distanceBetweenPoints(sortedNodeLatitude,sortedNodeLongitude,goalNode.getLatitude(),goalNode.getLongitude()))){
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
	 * TODO Is double accurate enough for this? should BigDecimal be used instead?
	 *Finds the distance between two nodes by comparing the latitude and longitude of both
	 * @param latitude1 The latitude of the first node
	 * @param longitude1 The longitude of the first node
	 * @param latitude2 The latitude of the second node
	 * @param longitude2 The longitude of the second node
	 * @return The distance between the two nodes
	 */
	public static double distanceBetweenPoints(double latitude1, double longitude1, double latitude2, double longitude2){
		
		double latDistance;
		double lonDistance;
		
			if(latitude1>latitude2){
				latDistance = latitude1-latitude2;
			}else{
				latDistance= latitude2-latitude1;
			}
		
			if(longitude1>longitude2){
				lonDistance= longitude1-longitude2;
			}else{
				lonDistance= longitude2-longitude1;
			}

		return latDistance+lonDistance;
		}
	
	/**
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
	private static List<Node> getPath(List<String> expandedNodes,Node startNd, Node goalNd){
		goalNode=goalNd;
		startNode=startNd;
		
		List<Node>path = new ArrayList<Node>();
		path.add(goalNode);
		String previousNode=goalNode.getId();//splitPath(expandedNodes.get(expandedNodes.size()-1),1);//To register the goal node which should be the last node expanded
		
		for(int i=expandedNodes.size()-1;i>=0;i--){
			if(splitPath(expandedNodes.get(i),0).equals(previousNode)){
				previousNode=splitPath(expandedNodes.get(i),1);
				path.add(SearchDatabase.searchForNode(previousNode));
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
		List<Node>nodeList=SearchDatabase.filterAccessibleWays(wayList);//This can remove a Way containing the goal; the code below deals with this
		
		//TODO This makes sure that every Node in a Way with a connection to the goal is returned, regardless of its suitability for navigation. Should only Nodes in suitable Ways be returned, or is this okay?
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
			return nodeList;
	}
}
