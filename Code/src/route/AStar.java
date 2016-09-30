package route;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import database.Node;

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
			if(getEstimatedMinimalCost(n1) > getEstimatedMinimalCost(n2)){
				return 1;
			}else if(getEstimatedMinimalCost(n1) < getEstimatedMinimalCost(n2)){
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

		Node startNode=getStartNode();
		Node goalNode=getGoalNode();

		Instant start = Instant.now();//Starts a timer used for measuring the run-time of this algorithm

		Node currentNode = startNode;
		updatePathCost(currentNode,0);

		expansionList.put(startNode,null);//Because startNode is the root

		/***************Link startNode's children to the start Node***************/
		priorityQueue.addAll(expandNode(currentNode));

		while(!priorityQueue.isEmpty()){
			this.updateMaxStoredNodes(priorityQueue.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's queue is always recorded

			currentNode = priorityQueue.poll();//To pop the last element in this list.
			//The list can get quite large,
			//so popping the first element would require moving lots of elements one position forwards in the list

			if(currentNode.getExternalId().equals(goalNode.getExternalId())){
				/* If currentNode==goalNode, then there can not be any shorter path to the goalNode,
				 * as all shorter paths are guaranteed have been explored.
				 * (Assuming my implementation of AStar is correct)
				 */

				Node goalParent=expansionList.get(currentNode);//returns the parent of the goalNode
				updatePathCost(currentNode,(getPathCost(goalParent)+distanceBetweenPoints(currentNode,goalParent)));//TODO NullPointerException keeps occurring here when running Main
				break;
			}else{

				//Add new Nodes to the priority queue
				List<Node> children = expandNode(currentNode);
				priorityQueue.removeAll(children);//This forces a reorder of the Nodes that have been changed. .remove(Object) has O(n) time-complexity though... There is probably a better way to force a reorder
				priorityQueue.addAll(children);//a Java priorityQueue does not reorder its contents automatically when their weights are changed. This has to be done manually
			}
		}

		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.

		priorityQueue.clear();
		return getPath(expansionList,startNode,goalNode);
	}
}
