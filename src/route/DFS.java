package route;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import database.Node;
import database.SearchDatabase;
/**
 * TODO This is currently BFS, as it uses a queue instead of a stack
 * @author Jostein
 *
 */
public class DFS extends Search {

	@Override
	public List<Node> findPath() {
		Queue<Node> stack = new LinkedList<Node>();

		Node currentNode;

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				stack.offer(node);
				expansionList.putIfAbsent(node,getStartNode());
			}
		}

		while(!stack.isEmpty()){

			currentNode = stack.poll();

			if(currentNode.equals(getGoalNode())){
				break;
			}else{
				
				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(!(child.equals(getStartNode()))
							&& child!=currentNode
							&& expansionList.putIfAbsent(child,currentNode)==null){
						stack.offer(child);
					}
				}
			}
		}
		return getPath(expansionList,getStartNode(),getGoalNode());
	}

}
