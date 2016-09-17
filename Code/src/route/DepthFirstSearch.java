package route;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

import database.Node;

/**
 * 
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

		for(Node child:expandNode(getStartNode())){
			stack.offerLast(child);
		}

		while(!stack.isEmpty()){
			this.updateMaxStoredNodes(stack.size()+expansionList.size());//Makes sure that the maximum number of Nodes stored by this search-algorithm's stack is always recorded

			currentNode = stack.pollLast();

			if(currentNode.equals(getGoalNode())){
				break;
			}else{
				
				List<Node> children=expandNode(currentNode);
				children.removeAll(stack);
				for(Node child:children){
					stack.offerLast(child);
				}
			}
		}
		
		this.setTimeElapsed(start, Instant.now());//Performed before the path is returned because the search can be considered finished at this point.
		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
