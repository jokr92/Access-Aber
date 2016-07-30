package main;

import database.BuildDatabase;
import database.DistanceMetricNode;
import database.Node;
import database.SearchDatabase;
import route.AStar;

public class RunTheSystem {

	/**
	 * The Main method of this system.
	 * @param args Assumes args[0] is startLatitude, args[1] is startLongitude, args[2] is goalLatitude, and args[3] is goal goalLongitude
	 * @author Jostein Kristiansen(jok13)
	 */
	public static void main(double[] args) {
		BuildDatabase.readConfig("map.osm");
		AStar aStar = new AStar();
		
		try{
			//TODO Is this a safe cast? Does this try-catch statement deal with casting-errors?
			DistanceMetricNode startNode = (DistanceMetricNode) SearchDatabase.findClosestNode(args[0], args[1]);
			DistanceMetricNode goalNode = (DistanceMetricNode) SearchDatabase.findClosestNode(args[2],args[3]);

			for(Node step:aStar.search(startNode, goalNode)){
				System.out.println(step);
			}
		}catch(NumberFormatException e){//Is never really reached when args is double. Any missing input is 0 by default
			System.out.println("One or more elements in the input were not of type: double");

			try{
				System.out.println("start Node:" + SearchDatabase.findClosestNode(args[0], args[1]));
			}catch(NumberFormatException s){
				System.out.println("No start Node could be found. Please check input");
			}
			try{
				System.out.println("goal Node:" + SearchDatabase.findClosestNode(args[2], args[3]));
			}catch(NumberFormatException g){
				System.out.println("No goal Node could be found. please check input");
			}
		}
	}

}
