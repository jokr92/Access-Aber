package route;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import database.Node;

public class DFS extends Search {

	@Override
	public List<Node> findPath(Node startNd, Node goalNd) {
		setStartNode(startNd);
		setGoalNode(goalNd);
		
		double totalPathDistance=Double.POSITIVE_INFINITY;
		Queue<Node> stack = new LinkedList<Node>();
		
		while(stack.peek()!=null){
			
		}
		
		if(totalPathDistance<Double.POSITIVE_INFINITY){
			path=getPath(expansionList,getStartNode(),getGoalNode());
		}

		return path;
	}

}
