package route;

import java.util.ArrayDeque;
import java.util.List;

import database.Node;
import database.SearchDatabase;

public class BreadthFirstSearch extends Search{

	@Override
	public List<Node> findPath() {

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
		return getPath(expansionList,getStartNode(),getGoalNode());
	}
}
