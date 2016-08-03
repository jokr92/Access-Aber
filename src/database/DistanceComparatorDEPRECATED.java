package database;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DistanceComparatorDEPRECATED implements Comparator<Node> {
	
	private Map<Node, Double> pathCost = new HashMap<Node, Double>();
	private Map<Node, Double> goalDistance = new HashMap<Node, Double>();

	@Override
	public int compare(Node n1, Node n2) {
		if(pathCost.get(n1)+goalDistance.get(n1) > pathCost.get(n2)+goalDistance.get(n2)){
			return 1;
		}else if(pathCost.get(n1)+goalDistance.get(n1) < pathCost.get(n2)+goalDistance.get(n2)){
			return -1;
		}else{
			//TODO The interface 'Node' can break distance-ties
			//with what? the ID? That's not related to distance at all...
			return 0;
			}
	}

}
