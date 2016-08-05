package route;

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
		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
