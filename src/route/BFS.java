package route;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import database.Node;

public class BFS extends Search{

	@Override
	public List<Node> findPath(Node startNd, Node goalNd) {
		setStartNode(startNd);
		setGoalNode(goalNd);
		
		double totalPathDistance=Double.POSITIVE_INFINITY;
		Queue<Node> queue = new LinkedList<Node>();
		
		while(queue.peek()!=null){
			
		}
		
		if(totalPathDistance<Double.POSITIVE_INFINITY){
			path=getPath(expansionList,getStartNode(),getGoalNode());
		}

		return path;
	}

	
}
