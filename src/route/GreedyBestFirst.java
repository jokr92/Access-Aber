package route;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import database.Node;
import database.SearchDatabase;

public class GreedyBestFirst extends InformedSearch{
	
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

		Node currentNode = getStartNode();
		updatePathCost(currentNode,0);

		expansionList.put(getStartNode(),null);//null because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				//updatePathCost(node,Search.distanceBetweenPoints(currentNode,node));
				updateGoalDistance(node,Search.distanceBetweenPoints(node,getGoalNode()));

				priorityQueue.add(node);
				expansionList.put(node,getStartNode());
			}
		}

		while(!priorityQueue.isEmpty()){

			currentNode = priorityQueue.poll();//To pop the most promising Node
			priorityQueue.clear();//Only one child of a parent is expanded, so the others are discarded

			if(currentNode.getId().equals(getGoalNode().getId())){
				/* If currentNode==goalNode, then there is no need to investigate other paths;
				 * this algorithm is greedy after all
				 */

				Node goalParent=expansionList.get(currentNode);//returns the parent of the goalNode
				updatePathCost(currentNode,(getPathCost(goalParent)+distanceBetweenPoints(currentNode,goalParent)));
				break;
			}else{

				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(child!=currentNode && child!=getStartNode()){
						//if(child.currentMinEstimatedCost>child.distance(parent)+parent.get(distanceTravelled)+child.distance(goalNode)){expansionList.put(child,parent);child.setDistanceTravelled();child.setGoalDistance(same if updated, different if new)}
						if(expansionList.putIfAbsent(child, currentNode)==null){
							//updatePathCost(child,getPathCost(currentNode)+Search.distanceBetweenPoints(currentNode,child));
							updateGoalDistance(child,Search.distanceBetweenPoints(child,getGoalNode()));

							priorityQueue.add(child);
						}
					}
					//Distance from Node<305025164> to Node<295134062>:
					//0.002514673702126127

				}
			}
		}

		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
