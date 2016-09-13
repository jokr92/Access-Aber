package route;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

import database.Node;
import database.SearchDatabase;
/**
 * TODO This is currently BFS, as it uses a queue instead of a stack
 * @author Jostein
 *
 */
public class DepthFirstSearch extends Search {

	@Override
	public List<Node> findPath() {
		Instant start = Instant.now();
		
		ArrayDeque<Node> stack = new ArrayDeque<Node>();

		Node currentNode;

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				stack.offerLast(node);
				expansionList.putIfAbsent(node,getStartNode());
			}
		}

		while(!stack.isEmpty()){
			this.updateMaxStoredNodes(stack.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's stack is always recorded

			currentNode = stack.pollLast();

			if(currentNode.equals(getGoalNode())){
				break;
			}else{
				
				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(!(child.equals(getStartNode()))
							&& child!=currentNode
							&& expansionList.putIfAbsent(child,currentNode)==null){
						stack.offerLast(child);
					}
				}
			}
		}
		
		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.
		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
