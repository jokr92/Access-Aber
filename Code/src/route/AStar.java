package route;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
public class AStar extends InformedSearch{

	/**
	 * For organizing the order of the nodes to be expanded
	 * TODO Aren't these comparisons backwards? pathCost+goalDistance should be minimized, not maximized
	 */
	PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
			if(getPathCost(n1)+getGoalDistance(n1) > getPathCost(n2)+getGoalDistance(n2)){
				return 1;
			}else if(getPathCost(n1)+getGoalDistance(n1) < getPathCost(n2)+getGoalDistance(n2)){
				return -1;
			}else{
				//TODO The interface 'Node' can break distance-ties
				//with what? the ID? That's not related to distance at all...
				return 0;
			}
		}
	});

	@Override
	public List<Node> findPath (){
		Instant start = Instant.now();//Starts a timer used for measuring the run-time of this algorithm

		Node currentNode = getStartNode();
		updatePathCost(currentNode,0);

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				updatePathCost(node,Search.distanceBetweenPoints(currentNode,node));
				updateGoalDistance(node,Search.distanceBetweenPoints(node,getGoalNode()));

				priorityQueue.add(node);
				expansionList.put(node,getStartNode());
			}
		}

		while(!priorityQueue.isEmpty()){
			this.updateMaxStoredNodes(priorityQueue.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's queue is always recorded

			currentNode = priorityQueue.poll();//To pop the last element in this list.
			//The list can get quite large,
			//so popping the first element would require moving lots of elements one position forwards in the list

			if(currentNode.getExternalId().equals(getGoalNode().getExternalId())){
				/* If currentNode==goalNode, then there can not be any shorter path to the goalNode,
				 * as all shorter paths are guaranteed have been explored.
				 * (Assuming my implementation of AStar is correct)
				 */

				Node goalParent=expansionList.get(currentNode);//returns the parent of the goalNode
				updatePathCost(currentNode,(getPathCost(goalParent)+distanceBetweenPoints(currentNode,goalParent)));
				break;
			}else{

				//TODO Make sure the distance between tower-Nodes takes into account the distance between the intermediate Nodes as well. The path cannot be guaranteed to be optimal otherwise.
				Node intermediateParent=currentNode;
				Node prevTowerNode=currentNode;
				double distanceCounter = 0;
				
				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					
					if(child!=currentNode && child!=getStartNode()){

						distanceCounter+=Search.distanceBetweenPoints(child, intermediateParent);

						if(!child.isTowerNode()){
							continue;//No need to add a Node to the queue or list of expanded Nodes if it is not part of a junction. This reduces both runtime and memory requirements
						}else{
							//if(child.currentMinEstimatedCost>child.distance(parent)+parent.get(distanceTravelled)+child.distance(goalNode)){expansionList.put(child,parent);child.setDistanceTravelled();child.setGoalDistance(same if updated, different if new)}
							if(getEstimatedMinimalCost(child)>(getPathCost(prevTowerNode)+distanceCounter+Search.distanceBetweenPoints(child,getGoalNode()))){

								updatePathCost(child,getPathCost(prevTowerNode)+distanceCounter);
								updateGoalDistance(child,Search.distanceBetweenPoints(child,getGoalNode()));

								expansionList.put(child, prevTowerNode);
								priorityQueue.remove(child);//To ensure that the Node only appears once in the queue
								priorityQueue.add(child);
							}else{
								//If this Node has not been encountered yet
								if(expansionList.putIfAbsent(child, prevTowerNode)==null){
									priorityQueue.add(child);
								}
							}
							distanceCounter=0;//reset because a towerNode has been encountered and added to the queue
							prevTowerNode=child;
						}
					}
					intermediateParent=child;
				}
			}
		}

		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.

		return getPath(expansionList,getStartNode(),getGoalNode());
	}

	//	/**
	//	 * TODO Not really used anymore
	//	 * TODO Make sure that traveled distance is also taken into account here. distance=traveledDistance+distanceToGoalNode. Otherwise, this is greedy Best-First search, not A*
	//	 * **INSERTION SORT**
	//	 * Sorts a list of nodes by measuring the distance between each unsorted node and a goal node,
	//	 * and inserting the unsorted nodes into the list of sorted nodes one at a time.
	//	 * @param sortedNodes The list of nodes that have already been sorted
	//	 * @param unsortedNodes The list of nodes to be sorted into the list of sorted nodes
	//	 * @param goalNd The target node which will determine which order the nodes are sorted in {@link goalNode}
	//	 * @return A list of sorted nodes, where the nodes with the shortest distance to the goal are put last
	//	 * @throws NullPointerException
	//	 */
	//	protected static List<DistanceMetricNode> sortByDistance(List<DistanceMetricNode> sortedNodes, List<DistanceMetricNode> unsortedNodes) throws NullPointerException{
	//
	//		double unsortedNodeDistance;
	//
	//		if(sortedNodes.isEmpty()&&!unsortedNodes.isEmpty()){
	//			if(unsortedNodes.get(unsortedNodes.size()-1).getDistanceToGoal()<=0){
	//				unsortedNodes.get(unsortedNodes.size()-1).setGoalDistance(Search.distanceBetweenPoints(unsortedNodes.get(unsortedNodes.size()-1).getLatitude(), unsortedNodes.get(unsortedNodes.size()-1).getLongitude(), getGoalNode().getLatitude(), getGoalNode().getLongitude()));
	//			}
	//			sortedNodes.add(unsortedNodes.remove(unsortedNodes.size()-1));//to pop the last element of the list. Moves fewer (none) elements in larger lists 
	//		}
	//
	//		for(DistanceMetricNode unsortedNode:unsortedNodes){//next unsorted node
	//			if(unsortedNode.getDistanceToGoal()<=0){
	//				unsortedNode.setGoalDistance(Search.distanceBetweenPoints(unsortedNode.getLatitude(), unsortedNode.getLongitude(), getGoalNode().getLatitude(), getGoalNode().getLongitude()));
	//			}
	//			unsortedNodeDistance=(unsortedNode.getDistanceTravelled()+unsortedNode.getDistanceToGoal());
	//			for(int x=sortedNodes.size()-1; x>=0;x--){//next sorted node
	//				if(unsortedNodeDistance<=(sortedNodes.get(x).getDistanceTravelled()+sortedNodes.get(x).getDistanceToGoal())){
	//					sortedNodes.add(x+1, unsortedNode);//reverse order. smallest at the back
	//					break;
	//				}else if(x==0){
	//					sortedNodes.add(0, unsortedNode);
	//					break;
	//				}
	//			}
	//
	//		}
	//		return sortedNodes;
	//	}
}
