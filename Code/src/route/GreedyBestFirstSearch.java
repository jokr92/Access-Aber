package route;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import database.Node;

/**
 * 
 * @author Jostein
 *
 */
public class GreedyBestFirstSearch extends InformedSearch{
	
	/**
	 * For organizing the order of the nodes to be expanded
	 * TODO Aren't these comparisons backwards? pathCost+goalDistance should be minimized, not maximized
	 */
	PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
        	if(getGoalDistance(n1) > getGoalDistance(n2)){
    			return 1;
    		}else if(getGoalDistance(n1) < getGoalDistance(n2)){
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
		
		Instant start = Instant.now();

		Node currentNode = getStartNode();
		updatePathCost(currentNode,0);

		expansionList.put(getStartNode(),null);//null because startNode is the root

		/***************Link startNode's children to the start Node***************/
		priorityQueue.addAll(expandNode(currentNode));

		while(!priorityQueue.isEmpty()){
			this.updateMaxStoredNodes(priorityQueue.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's queue is always recorded

			currentNode = priorityQueue.poll();//To pop the most promising Node
			priorityQueue.clear();//Only one child of a parent is expanded, so the others are discarded

			if(currentNode.getExternalId().equals(getGoalNode().getExternalId())){
				/* If currentNode==goalNode, then there is no need to investigate other paths;
				 * this algorithm is greedy after all
				 */

				Node goalParent=expansionList.get(currentNode);//returns the parent of the goalNode
				updatePathCost(currentNode,(getPathCost(goalParent)+distanceBetweenPoints(currentNode,goalParent)));
				break;
			}else{

				//Add new Nodes to the priority queue
				List<Node> children = expandNode(currentNode);
				priorityQueue.removeAll(children);//This forces a reorder of the Nodes that have been changed. .remove(Object) has O(n) time-complexity though, so there is probably a better way to force a reorder
				priorityQueue.addAll(children);//a Java priorityQueue does not reorder its contents automatically when their weights are changed. This has to be done manually
				
			}
		}

		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.
		
		priorityQueue.clear();
		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
