package route;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import database.Node;
import database.SearchDatabase;

public class BFS extends Search{

	@Override
	public List<Node> findPath() {

		Queue<Node> queue = new LinkedList<Node>();

		Node currentNode;

		expansionList.put(getStartNode(),null);//Because startNode is the root

		for(Node node:SearchDatabase.getNavigatableConnectedNodes(getStartNode())){
			if(node!=getStartNode()){
				queue.offer(node);
				expansionList.putIfAbsent(node,getStartNode());
			}
		}

		while(!queue.isEmpty()){

			currentNode = queue.poll();

			if(currentNode.equals(getGoalNode())){
				break;
			}else{
				
				for(Node child:SearchDatabase.getNavigatableConnectedNodes(currentNode)){
					if(!(child.equals(getStartNode()))
							&& child!=currentNode
							&& expansionList.putIfAbsent(child,currentNode)==null){
						queue.offer(child);
					}
				}
			}
		}
		return getPath(expansionList,getStartNode(),getGoalNode());
	}
}
