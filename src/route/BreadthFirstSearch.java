package route;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

import database.Node;
import database.SearchDatabase;

public class BreadthFirstSearch extends Search{

	@Override
	public List<Node> findPath() {
		Instant start = Instant.now();

		ArrayDeque<Node> queue = new ArrayDeque<Node>();

		Node currentNode;

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				queue.offerLast(node);
				expansionList.putIfAbsent(node,getStartNode());
			}
		}

		while(!queue.isEmpty()){
			this.updateMaxStoredNodes(queue.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's queue is always recorded

			currentNode = queue.pollFirst();

			if(currentNode.equals(getGoalNode())){
				break;
			}else{
				
				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(!(child.equals(getStartNode()))
							&& child!=currentNode
							&& expansionList.putIfAbsent(child,currentNode)==null){
						queue.offerLast(child);
					}
				}
			}
		}
		
		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.
		return getPath(expansionList,getStartNode(),getGoalNode());
	}
}
