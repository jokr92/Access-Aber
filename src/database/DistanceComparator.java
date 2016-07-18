package database;

import java.util.Comparator;

public class DistanceComparator implements Comparator<OSMNode> {

	@Override
	public int compare(OSMNode n1, OSMNode n2) {
		if(n1.getDistanceTravelled()+n1.getDistanceToGoal() > n2.getDistanceTravelled()+n2.getDistanceToGoal()){
			return 1;
		}else if(n1.getDistanceTravelled()+n1.getDistanceToGoal() < n2.getDistanceTravelled()+n2.getDistanceToGoal()){
			return -1;
		}else{
			//TODO The interface 'Node' can break distance-ties
			return 0;
			}
	}

}
