package route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import database.DistanceMetricNode;
import database.Node;
import database.SearchDatabase;

/**
 * Uses the A* algorithm to find a path from one {@link #startNode} to a {@link #goalNode}, via the nodes connected to the start node.
 * Expands the nodes closest to the {@link #goalNode} first, using a priority queue.
 * The path will be complete and optimal, as is key with the A* algorithm.
 * @author Jostein Kristiansen(jok13)
 *@see database.BuildDatabase
 */
public class AStar extends InformedSearch{

	@Override
	public List<Node> findPath (Node startNd, Node goalNd){
		
		setStartNode(startNd);
		setGoalNode(goalNd);

		/**
		 * For handling the children of expanded nodes before they are passed into the queueList
		 * TODO Change to something other than ArrayList
		 */
		List<DistanceMetricNode> childList = new ArrayList<DistanceMetricNode>();

		DistanceMetricNode currentNode=(DistanceMetricNode) getStartNode();
		currentNode.setDistanceTravelled(0);

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				//Is it safe to cast like this?

			((DistanceMetricNode) node).setDistanceTravelled(Search.distanceBetweenPoints(getStartNode().getLatitude(),getStartNode().getLongitude(),node.getLatitude(),node.getLongitude()));
			priorityQueue.add((DistanceMetricNode) node);
			expansionList.putIfAbsent(node,getStartNode());
			}
		}

		while(priorityQueue.isEmpty()==false){

			currentNode=priorityQueue.poll();//To pop the last element in this list.
											//The list can get quite large,
											//so popping the first element would require moving lots of elements one position forwards in the list

			if(currentNode.getId().equals(goalNd.getId())){
				/* If currentNode==goalNode, then there can not be any shorter path to the goalNode,
				 * as all shorter paths are guaranteed have been explored.
				 * (Assuming my implementation of AStar is correct)
				 */
				totalPathDistance=currentNode.getDistanceTravelled();/*would always be 0, as currentNode==goalNode here*/
				break;
			}else{

				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(child!=currentNode){
						//TODO is it ok to cast like this?
						childList.add((DistanceMetricNode) child);
					}
				}

				Iterator<DistanceMetricNode> j = childList.iterator();
				while(j.hasNext()){
					DistanceMetricNode node = j.next();

					if(!(node.equals(getStartNode()))&&expansionList.putIfAbsent(node,currentNode)==null){
						node.setDistanceTravelled(currentNode.getDistanceTravelled()+Search.distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),node.getLatitude(),node.getLongitude()));
						node.setGoalDistance(Search.distanceBetweenPoints(currentNode.getLatitude(),currentNode.getLongitude(),goalNd.getLatitude(),goalNd.getLongitude()));
					
						priorityQueue.add(node);
					}else{
						j.remove();//It is faster to remove the children here than in the other lists

					}
				}

				/*
				 * This data has been passed on to the queueList, so it is no longer needed,
				 * but it might be best to not shrink the size of this list,
				 * as that might result in excessive resizing in the future -
				 * .clear() does not shrink the list.
				 */
				childList.clear();
			}
		}
		if(totalPathDistance<Double.POSITIVE_INFINITY){
			path=getPath(expansionList,getStartNode(),getGoalNode());
		}

		return path;
	}

	/**
	 * TODO Not really used anymore
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
				unsortedNodes.get(unsortedNodes.size()-1).setGoalDistance(Search.distanceBetweenPoints(unsortedNodes.get(unsortedNodes.size()-1).getLatitude(), unsortedNodes.get(unsortedNodes.size()-1).getLongitude(), getGoalNode().getLatitude(), getGoalNode().getLongitude()));
			}
			sortedNodes.add(unsortedNodes.remove(unsortedNodes.size()-1));//to pop the last element of the list. Moves fewer (none) elements in larger lists 
		}

		for(DistanceMetricNode unsortedNode:unsortedNodes){//next unsorted node
			if(unsortedNode.getDistanceToGoal()<=0){
				unsortedNode.setGoalDistance(Search.distanceBetweenPoints(unsortedNode.getLatitude(), unsortedNode.getLongitude(), getGoalNode().getLatitude(), getGoalNode().getLongitude()));
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
}
